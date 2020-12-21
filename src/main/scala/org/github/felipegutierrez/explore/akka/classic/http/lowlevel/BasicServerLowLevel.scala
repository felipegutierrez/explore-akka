package org.github.felipegutierrez.explore.akka.classic.http.lowlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Sink

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object BasicServerLowLevel {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("BasicServerLowLevel")
    import system.dispatcher
    println(s"type on the console: 'curl localhost:8888'")
    val serverSource = Http().newServerAt("localhost", 8888).connectionSource()
    val connectionSink = Sink.foreach[IncomingConnection] { connection =>
      println(s"Accepted incoming connection from: ${connection.remoteAddress}")
    }
    val serverBindingFuture = serverSource.to(connectionSink).run()
    serverBindingFuture.onComplete {
      case Success(binding) =>
        println(s"server binding successful")
        binding.terminate(2 seconds)
      case Failure(ex) => println(s"server binding failed: $ex")
    }

    /** Method 1: synchronously serve HTTP responses */
    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(HttpMethods.GET, uri, headers, entity, protocol) =>
        HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Hello Akka HTTP
              | </body>
              |</html>
              |""".stripMargin)
        )
      case request: HttpRequest =>
        request.discardEntityBytes()
        HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! page not found =(
              | </body>
              |</html>
              |""".stripMargin)
        )
    }
    val httpSyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
      connection.handleWithSyncHandler(requestHandler)
    }
    println(s"call on the console: 'http GET localhost:8080'")
    // Http().newServerAt("localhost", 8080).connectionSource().runWith(httpSyncConnectionHandler)
    // short version >>> 
    Http().newServerAt("localhost", 8080).bindSync(requestHandler)
  }
}
