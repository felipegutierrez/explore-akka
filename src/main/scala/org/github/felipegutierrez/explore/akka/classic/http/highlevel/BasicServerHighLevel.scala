package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
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
      path("home") { // Directive
        complete(StatusCodes.OK) // Directive
      }

    println("http GET localhost:8080/home")
    Http().newServerAt("localhost", 8080).bindFlow(simpleRoute)
  }
}

