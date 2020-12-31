package org.github.felipegutierrez.explore.akka.classic.http.client

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import spray.json._

import scala.concurrent.duration._

case class CreditCard(serialNumber: String, securityCode: String, account: String)

object PaymentSystemDomain {

  case class PaymentRequest(creditCard: CreditCard, receiverAccount: String, amount: Double)

  case object PaymentAccepted

  case object PaymentRejected

}

trait CreditCardJsonProtocol extends DefaultJsonProtocol {
  implicit val creditCardFormat = jsonFormat3(CreditCard)
  implicit val paymentRequestFormat = jsonFormat3(PaymentSystemDomain.PaymentRequest)
}

class PaymentValidatorActor extends Actor with ActorLogging {

  import PaymentSystemDomain._

  override def receive: Receive = {
    case PaymentRequest(CreditCard(serialNumber, securityCode, senderAccount), receiverAccount, amount) =>
      log.info(s"$senderAccount is trying to send $amount dollars to $receiverAccount")
      if (serialNumber == "0000-0000-0000-0000") sender() ! PaymentRejected
      else sender() ! PaymentAccepted
  }
}

object PaymentSystem extends CreditCardJsonProtocol with SprayJsonSupport {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    println("starting microservice for payment ...")
    implicit val system = ActorSystem("PaymentSystem")
    import PaymentSystemDomain._
    import system.dispatcher

    val paymentValidator = system.actorOf(Props[PaymentValidatorActor], "paymentValidator")
    implicit val timeout = Timeout(2 seconds)

    val paymentRoute =
      path("api" / "payment") {
        post {
          entity(as[PaymentRequest]) { paymentRequest =>
            val validationResponseFuture = (paymentValidator ? paymentRequest).map {
              case PaymentRejected => StatusCodes.Forbidden
              case PaymentAccepted => StatusCodes.OK
              case _ => StatusCodes.BadRequest
            }
            complete(validationResponseFuture)
          }
        }
      }

    println("http POST localhost:8080/api/payment < src/main/resources/json/paymentRequest.json")
    println("http POST localhost:8080/api/payment < src/main/resources/json/invalidPaymentRequest.json")
    Http().newServerAt("localhost", 8080).bind(paymentRoute)
  }
}

