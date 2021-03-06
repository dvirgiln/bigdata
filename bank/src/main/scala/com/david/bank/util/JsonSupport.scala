package com.david.bank.util

import com.david.bank.client.ClientActor.OperationPerformed
import com.david.bank.client.{ ClientBasicInfo, ClientCreatedResponse, ClientDetailedInfo, ClientsBasicInfo, Deposit, Error }
import com.david.bank.transaction.TransactionActor.TransactionErrors
import com.david.bank.user.UserActor.ActionPerformed
import com.david.bank.user.{ User, Users }
import spray.json.{ JsString, JsValue, JsonFormat, deserializationError }
import com.david.bank.transaction.TransactionDomain._
//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._
  import DateMarshalling._

  implicit val urlJsonFormat = new JsonFormat[TransactionStatus.Value] {
    override def read(json: JsValue): TransactionStatus.Value = json match {
      case JsString(status) => TransactionStatus.withNameOpt(status).getOrElse(deserializationError("Transaction Status does not match"))
      case _ => deserializationError("Transaction Status should be string")
    }

    override def write(obj: TransactionStatus.Value): JsValue = JsString(obj.toString)
  }
  implicit val userJsonFormat = jsonFormat4(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val transactionJsonFormat = jsonFormat6(Transaction)
  implicit val transactionsJsonFormat = jsonFormat1(Transactions)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
  implicit val transactionErrorsJsonFormat = jsonFormat2(TransactionErrors)
  implicit val clientDetailedJsonFormat = jsonFormat3(ClientDetailedInfo)
  implicit val clientBasicJsonFormat = jsonFormat2(ClientBasicInfo)
  implicit val operationPerformedJsonFormat = jsonFormat1(OperationPerformed)
  implicit val clientsBasicInfoJsonFormat = jsonFormat1(ClientsBasicInfo)
  implicit val errorJsonFormat = jsonFormat1(Error)
  implicit val depositJsonFormat = jsonFormat2(Deposit)
  implicit val clientCreatedJsonFormat = jsonFormat1(ClientCreatedResponse)

}
