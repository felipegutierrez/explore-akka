package org.github.felipegutierrez.explore.akka.classic.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Sink, Source}
import spray.json._

import java.util.UUID
import scala.util.{Failure, Success}

object HostLevel extends CreditCardJsonProtocol with SprayJsonSupport {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("HostLevel")
    // import system.dispatcher

    val poolFlow = Http().cachedHostConnectionPool[Int]("www.google.com")

    Source(1 to 10)
      .map(i => (HttpRequest(), i))
      .via(poolFlow)
      .map {
        case (Success(response), value) =>
          // MAKE SURE TO CALL THIS OTHERWISE CONNECTIONS ARE BLOCKED
          response.discardEntityBytes()
          s"Request $value has received response: $response"
        case (Failure(ex), value) =>
          s"Request $value has failed: $ex"
      }
      .runWith(Sink.foreach[String](println))

    // testing the small payment system
    val creditCards = List(
      CreditCard("4245-4568-7745-0124", "424", "tx-test-account"),
      CreditCard("0000-0000-0000-0000", "458", "tx-suspicious-account"),
      CreditCard("1234-1234-1234-1234", "424", "tx-felipe-account")
    )

    import PaymentSystemDomain._
    val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "rtjvm-store-account", 90))
    val serverHttpRequests = paymentRequests.map(paymentRequest =>
      (
        HttpRequest(
          HttpMethods.POST,
          uri = Uri("/api/payment"),
          entity = HttpEntity(
            ContentTypes.`application/json`,
            paymentRequest.toJson.prettyPrint
          )
        ),
        UUID.randomUUID().toString
      )
    )

    Source(serverHttpRequests)
      .via(Http().cachedHostConnectionPool[String]("localhost", 8080))
      .runForeach {
        case (Success(response@HttpResponse(StatusCodes.Forbidden, headers, entity, protocol)), orderId) =>
          println(s"The order id $orderId was forbidden and returned the response: $response")
        case (Success(response), orderId) =>
          // MAKE SURE TO CALL THIS OTHERWISE CONNECTIONS ARE BLOCKED
          // response.discardEntityBytes()
          println(s"The order id $orderId was successful and returned the response: $response")
        case (Failure(ex), orderId) =>
          s"The order id $orderId has failed: $ex"
      }
  }
}
