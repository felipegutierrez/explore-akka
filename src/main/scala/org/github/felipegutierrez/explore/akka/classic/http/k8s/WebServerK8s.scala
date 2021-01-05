package org.github.felipegutierrez.explore.akka.classic.http.k8s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

object WebServerK8s {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }
  def run() = {
    implicit val system = ActorSystem("systemK8S")

    val route = (path("app" / "k8s") & extractRequest & extractLog) {
      (request, log) => {
        get {
          log.info(s"I got the compact http request: $request")
          complete(StatusCodes.OK,
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              s"""
                 |<html>
                 | <body>I got the compact http request: $request on to k8s (minikube) =)</body>
                 |</html>
                 |""".stripMargin
            )
          )
        } ~ pathEndOrSingleSlash {
          complete(StatusCodes.BadRequest)
        }
      }
    } ~ pathEndOrSingleSlash {
      complete(StatusCodes.Forbidden)
    }

    println("try:")
    println("http GET http://localhost:8001/app/k8s")
    Http()
      .newServerAt("localhost", 8001)
      .bindFlow(route)
  }
}
