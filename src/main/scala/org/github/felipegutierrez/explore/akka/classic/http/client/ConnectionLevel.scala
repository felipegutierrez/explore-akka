package org.github.felipegutierrez.explore.akka.classic.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.{Sink, Source}

import scala.util.{Failure, Success}

object ConnectionLevel {
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
  }
}
