package com.enron.services



/*CODE USED FOR WHOLETEXTFILES option. This is not necessary and it is not used.
   THIS was  MY INITIAL OPTION USING wholeTextFiles, but it is not a good solution.
   BUT as it is interesting code for the future I keep it.
 */
object DeprecatedXMLProcessing {

  /*

      val data = sc.wholeTextFiles("hdfs://localhost:9000/data/enron/")
      val files = data.map { case (filename, content) => processXML(content)}

      //Interesting code as it uses Scala XML parsing
    */

  /* def processXML(content: String): List[Email] = {
    val xmlDocument = scala.xml.XML.loadString(content)

    val documents = xmlDocument \ "root" \ "batch" \ "documents" \ "document"

    val messages = documents.filter { a => val value = a \ "@DocType"
      value.text == "Message"
    }

    messages.foldLeft(List[Email]()) { case (total, message) => getEmail(message) :: total }
  }

  def getEmail(message: scala.xml.Node): Email = {

    def processTagNode(node: Option[scala.xml.Node]): Option[String] = node match {
      case Some(tag) => Some((tag \ "@TagValue").text)
      case None => None
    }
    val tags = message \ "tags" \ "tag"
    val cc = tags.filter(tag => (tag \ "@TagName").text == "#CC").headOption
    val to = tags.filter(tag => (tag \ "@TagName").text == "#To").headOption
    val subject = tags.filter(tag => (tag \ "@TagName").text == "#Subject").headOption
    val from = tags.filter(tag => (tag \ "@TagName").text == "#From").headOption
    val dateSent = tags.filter(tag => (tag \ "@TagName").text == "#DateSent").headOption
    Email(processTagNode(from), processTagNode(to), processTagNode(cc), processTagNode(subject), processTagNode(dateSent))
  }*/
}
