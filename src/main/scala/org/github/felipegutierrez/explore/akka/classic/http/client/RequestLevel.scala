package org.github.felipegutierrez.explore.akka.classic.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import spray.json._

import scala.util.{Failure, Success}

/**
 * High level Akka-Http client request API for low latency, low volume of data, and for short lived connections
 */
object RequestLevel extends CreditCardJsonProtocol with SprayJsonSupport {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("RequestLevel")
    import system.dispatcher

    val responseFuture = Http().singleRequest(HttpRequest(uri = "http://www.google.com"))
    responseFuture.onComplete {
      case Success(response) =>
        // MAKE SURE TO CALL THIS OTHERWISE CONNECTIONS ARE BLOCKED
        response.discardEntityBytes()
        println(s"The request was successful and returned: $response")
      case Failure(ex) =>
        println(s"The request has failed: $ex")
    }

    // testing the small payment system
    val creditCards = List(
      CreditCard("4245-4568-7745-0124", "424", "tx-test-account"),
      CreditCard("0000-0000-0000-0000", "458", "tx-suspicious-account"),
      CreditCard("1234-1234-1234-1234", "424", "tx-felipe-account")
    )

    import PaymentSystemDomain._
    val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "rtjvm-store-account", 90))
    val serverHttpRequests = paymentRequests.map(paymentRequest =>
      HttpRequest(
        HttpMethods.POST,
        uri = "http://localhost:8080/api/payment",
        entity = HttpEntity(
          ContentTypes.`application/json`,
          paymentRequest.toJson.prettyPrint
        )
      )
    )

    Source(serverHttpRequests)
      .mapAsync(10)(request => Http().singleRequest(request))
      .runForeach(println)
  }
}
