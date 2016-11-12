import sbt._
import Keys._

object Dependencies {

  val commonDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4",
    "org.slf4j" % "slf4j-log4j12" % "1.7.10"
  )

  val sparkVersion = "1.6.2"
  

  val sparkDependencies  : Seq[ModuleID] = commonDependencies ++ Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion ,
    "org.apache.spark" %% "spark-sql" % sparkVersion ,
    "com.datastax.spark" %% "spark-cassandra-connector" % sparkVersion
  )

}
