package org.github.felipegutierrez.explore.akka.classic.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Sink, Source}
import spray.json._

import scala.util.{Failure, Success}

/**
 * Low level Akka-Http client request API for long living connections
 */
object ConnectionLevel extends CreditCardJsonProtocol with SprayJsonSupport {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("ConnectionLevel")
    import system.dispatcher

    val connectionFlow = Http().outgoingConnection("www.google.com")

    def oneOfRequest(request: HttpRequest) =
      Source.single(request).via(connectionFlow).runWith(Sink.head)

    oneOfRequest(HttpRequest()).onComplete {
      case Success(response) => println(s"got successful response: $response")
      case Failure(exception) => println(s"sending the request failed: $exception")
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
        uri = Uri("/api/payment"),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          paymentRequest.toJson.prettyPrint
        )
      ))
    Source(serverHttpRequests)
      .via(Http().outgoingConnection("localhost", 8080))
      .to(Sink.foreach[HttpResponse](println))
      .run()
  }
}
