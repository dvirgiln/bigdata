package com.example.temperatures.controller

/**
  * Created by dave on 16/11/16.
  */
import java.text._
import java.util._
import javax.xml.datatype._

trait DateMarshalling {

  import spray.json._

  implicit object GregorianCalendarFormat extends JsonFormat[XMLGregorianCalendar] {

    def write(obj: XMLGregorianCalendar) = DateFormat.write(obj.toGregorianCalendar.getTime)

    def read(json: JsValue) = {
      val c = new GregorianCalendar
      c.setTime(DateFormat.read(json))
      DatatypeFactory.newInstance.newXMLGregorianCalendar(c)
    }
  }

  implicit object DateFormat extends JsonFormat[Date] {

    def write(date : Date) : JsValue = JsString(dateToIsoString(date))

    def read(json: JsValue) : Date = json match {

      case JsString(rawDate) => parseIsoDateString(rawDate) match {
        case None => deserializationError(s"Expected ISO Date format, got $rawDate")
        case Some(isoDate) => isoDate
      }

      case unknown => deserializationError(s"Expected JsString, got $unknown")
    }
  }

  private val localIsoDateFormatter = new ThreadLocal[SimpleDateFormat] {
    override def initialValue() = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  }

  def dateToIsoString(date: Date) = localIsoDateFormatter.get().format(date)

  def parseIsoDateString(date: String): Option[Date] = {
    if (date.length != 28) None
    else try Some(localIsoDateFormatter.get().parse(date))
    catch {
      case p: ParseException => None
    }
  }
}