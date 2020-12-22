package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.github.felipegutierrez.explore.akka.classic.http.lowlevel.HttpsServerContext

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
      } ~ path("home") {
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
      } ~ path("api" / "guitar") {
        get {
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>here is my guitar!</body>
              |</html>
              |""".stripMargin
          ))
        } ~ post {
          complete(StatusCodes.OK)
        }
      }

    println("try:")
    println("http GET localhost:8080/status")
    println("http POST localhost:8080/status")
    println("http GET localhost:8080/home")
    println("http POST localhost:8080/home")
    println("http GET localhost:8080/api/guitar")
    println("http POST localhost:8080/api/guitar")
    Http()
      .newServerAt("localhost", 8080)
      // .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindFlow(simpleRoute)
  }
}

