package com.enron.services

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}
import SparkContextFactory._
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema


/* It is important to note that the case classes shoud contain Seq. In case it is defined a List, there is an error from Spark.*/
case class EmailRecipient(name: String, recipient: Option[String] = None)

case class Email(from: Option[EmailRecipient], to: Option[Seq[EmailRecipient]], cc: Option[Seq[EmailRecipient]], subject: Option[String], dateSent: Option[String])


trait EnronService {
  def getMessageAverageWords: Long

  def getMostPopularEmails(numberResults: Int): Seq[(EmailRecipient, Long)]

}

/**
  * Implementation of the weather dao that retrieves the data from a cassandra database, using spark.
  *
  * Created by dave on 20/09/16.
  */
object SparkEnronService extends EnronService {
  val sparkSession = builder.getOrCreate()



  //Constants read from the appliaction.conf file
  val PATH = ConfigFactory.load().getString("hdfs.data.path")
  val HDFS_URI = ConfigFactory.load().getString("hdfs.url")


  def getMessageAverageWords: Long = ???

  def getMostPopularEmails(numberResults: Int): Seq[(EmailRecipient, Long)] = {
    import sparkSession.implicits._
    import XMLProcessingFilter._
    //Contains all the XML files to treat.
    val fileNames = (FileSystem.get(sparkContext.hadoopConfiguration).listStatus(new Path(PATH))).map(_.getPath.getName)

    //All the dataframes loaded using the xml format.
    val dataframes = fileNames.filter(_.endsWith(".xml")).map { case filename => sqlContext.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "Document") //with this option we are going to load the dataframes from the tag Document. Interesting option
      .load(s"$HDFS_URI$PATH$filename")
    }

    def filterMessages(df: DataFrame): DataFrame = df.filter(df("_DocType") === "Message")

    //Convert the dataframes into a List of Datasets that contains Emails instead of non structured data.
    val emails = dataframes.map(df => filterMessages(df).map{case row => parseRow(row)})
    //Join all the dataframes from all the files into just one dataframe using the reduce function.
    val oneDataframe = emails.reduce(_ union _)

    //As this dataframe is going to be used by multiple different functions, it is good to cache it in memory.
    oneDataframe.cache()
    import XMLProcessingComparing._
    (oneDataframe.rdd.aggregate(Map[EmailRecipient, Long]())(aggregateExecutor, joinWorkersWork).toSeq.sortBy(_._2)(Ordering[Long].reverse)).take(numberResults)
  }
}

/*
   All the processing functions that are gonna be used by the spark workers should be encapsulated in individual and small classes. The main reason is because, if the function is used
   in a map like it is done before, they are serialized and sent to the workers. It can happen that if you included the function inside of the main class, then it is serialized all the class and
   send to the worker node. From my experience this is not good idea. Apart from the performance lost, sending unused bytes to the workers, it can happen that the class that makes the dataframe.map is not serializable.
 */
object XMLProcessingFilter {
  /*
  This function extract the email information and convert into a List of emails.
 */
  private def extractEmails(emailsOpt: Option[String]): Option[List[EmailRecipient]] = {
    emailsOpt match {
      case Some(emails) if emails.contains(",") => Some(emails.split(',').map(toAddress) toList)
      case Some(emails) if emails.contains(";") => Some(emails.split(';').map(toAddress).toList)
      case Some(emails) => Some(List(toAddress(emails)))
      case None => None
    }
  }

  private def toAddress(email: String): EmailRecipient = {
    if (email.contains("&lt;") && email.contains("&gt;")) {
      val indexLT = email.indexOf("&lt;")
      val indexGT = email.indexOf("&gt;")
      EmailRecipient(email.substring(0, indexLT), Some(email.substring(indexLT + 4, indexGT)))
    }
    else EmailRecipient(email)
  }


  /*
   Converts the dataframes into a List of Datasets that contains Emails instead of non structured data.
   */
  def parseRow(row: Row): Email = {
    val tagsNode = row.get(row.fieldIndex("Tags")).asInstanceOf[GenericRowWithSchema]
    val tags = tagsNode.get(0).asInstanceOf[Seq[GenericRowWithSchema]]
    //Convert the Tags Dataframe array into a Map with key the TagName and value the TagValue
    //In the daatabricks xml format, it converts the xml tag attributes into a new column in the df with the schemaIndexName starting by underscore
    val tagValues = tags.foldLeft(scala.collection.Map[String, String]())((total, tag) => total + (tag.get(tag.fieldIndex("_TagName")).asInstanceOf[String] -> tag.get(tag.fieldIndex("_TagValue")).asInstanceOf[String]))
    Email(tagValues.get("#From").map(toAddress), extractEmails(tagValues.get("#To")), extractEmails(tagValues.get("#CC")), tagValues.get("#Subject"), tagValues.get("#DateSent"))
  }

}

/*
   All the processing functions that are gonna be used by the spark workers should be encapsulated in individual and small classes. The main reason is because, if the function is used
   in a map like it is done before, they are serialized and sent to the workers. It can happen that if you included the function inside of the main class, then it is serialized all the class and
   send to the worker node. From my experience this is not good idea. Apart from the performance lost, sending unused bytes to the workers, it can happen that the class that makes the dataframe.map is not serializable.
 */
object XMLProcessingComparing {

  /*
This function join the work done by the different workers
 */
  def joinWorkersWork(result1: Map[EmailRecipient, Long], result2: Map[EmailRecipient, Long]): Map[EmailRecipient, Long] = {
    val result = collection.mutable.Map() ++ result1
    result2.foreach { case email =>
      val foundOpt = result.find(p => sameAddress(p._1, email._1))
      foundOpt match {
        case Some(found) => result(found._1) = result(found._1) + email._2
        case None => result.put(email._1, email._2)
      }
    }
    result.toMap
  }

  def aggregateExecutor(aggregation: Map[EmailRecipient, Long], record: Email): (Map[EmailRecipient, Long]) = {
    val to = record.to
    val cc = record.cc

    val result = collection.mutable.Map() ++ aggregation

    def addEmail(emailRecipient: EmailRecipient, value: Long) = {
      val foundOpt = result.find(p => sameAddress(p._1, emailRecipient))
      foundOpt match {
        case Some(found) => result(found._1) = result(found._1) + value
        case None => result.put(emailRecipient, value)
      }
    }
    if (to.isDefined) to.get.foreach(addEmail(_, 100))
    if (cc.isDefined) cc.get.foreach(addEmail(_, 50))
    result.toMap
  }

  def sameAddress(email1: EmailRecipient, email2: EmailRecipient) = {
    (email1, email2) match {
      case (EmailRecipient(name1, _), EmailRecipient(name2, _)) if name1 == name2 => true
      case (EmailRecipient(name1, _), EmailRecipient(name2, Some(address))) if name1 == address => true
      case (EmailRecipient(_, Some(address)), EmailRecipient(name2, _)) if name2 == address => true
      case (EmailRecipient(_, Some(address1)), EmailRecipient(_, Some(address2))) if address1 == address2 => true
      case _ => false

    }
  }


}

object TestApp extends App {
  val popularEmails = SparkEnronService.getMostPopularEmails(20)

  println(popularEmails.mkString("\n"))
}
