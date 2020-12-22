package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object BasicServerHighLevel {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("BasicServerHighLevel")
    // import system.dispatcher

    val simpleRoute: Route =
      path("status") { // Directive
        get {
          complete(StatusCodes.OK) // Directive
        } ~ post {
          complete(StatusCodes.Forbidden)
        }
      } ~ (path("home") | path("index")) {
        get {
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>hello from Akka HTTP high level API</body>
              |</html>
              |""".stripMargin
          ))
        } ~ post {
          complete(StatusCodes.Forbidden)
        }
      } ~ path("api" / "guitars") {
        get {
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>here are my guitars!</body>
              |</html>
              |""".stripMargin
          ))
        } ~ post {
          complete(StatusCodes.OK)
        }
      } ~ (path("api" / "guitar" / IntNumber) | (path("api" / "guitar") & parameter('id.as[Int]))) { (itemNumber: Int) =>
        get {
          println(s"I found the guitar $itemNumber")
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            s"""
               |<html>
               | <body>I found the guitar $itemNumber!</body>
               |</html>
               |""".stripMargin
          ))
        }
      } ~ path("control") {
        extractRequest { (httpRequest: HttpRequest) =>
          extractLog { (log: LoggingAdapter) =>
            log.info(s"I got the http request: $httpRequest")
            complete(StatusCodes.OK,
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                s"""
                   |<html>
                   | <body>I got the http request: $httpRequest</body>
                   |</html>
                   |""".stripMargin
              )
            )
          }
        }
      } ~ (path("controlCompact") & extractRequest & extractLog) { (request, log) =>
        log.info(s"I got the compact http request: $request")
        complete(StatusCodes.OK,
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            s"""
               |<html>
               | <body>I got the compact http request: $request</body>
               |</html>
               |""".stripMargin
          )
        )
      } ~ pathEndOrSingleSlash {
        complete(StatusCodes.OK)
      }

    println("try:")
    println("http GET localhost:8080/status")
    println("http POST localhost:8080/status")
    println("http GET localhost:8080/home")
    println("http GET localhost:8080/index")
    println("http POST localhost:8080/home")
    println("http GET localhost:8080/api/guitars")
    println("http POST localhost:8080/api/guitars")
    println("http GET localhost:8080/api/guitar?id=43")
    println("http GET localhost:8080/api/guitar/42")
    println("http GET localhost:8080/api/guitar/42/5")
    println("http GET localhost:8080/control")
    println("http GET localhost:8080/controlCompact")
    Http()
      .newServerAt("localhost", 8080)
      // .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindFlow(simpleRoute)
  }
}

