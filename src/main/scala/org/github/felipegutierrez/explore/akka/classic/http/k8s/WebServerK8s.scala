package org.github.felipegutierrez.explore.akka.classic.http.k8s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

object WebServerK8s {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }
  def run() = {
    val configString =
      """
        | akka {
        |   loglevel = "INFO"
        | }
      """.stripMargin
    // val system = ActorSystem("ConfigDemo", ConfigFactory.load(config))
    implicit val system = ActorSystem("systemK8S")
    import system.dispatcher

    // val config = ConfigFactory.load()
    val config = ConfigFactory.load(ConfigFactory.parseString(configString))
    val host = config.getString("http.host")
    val port = config.getInt("http.port")
    val message = config.getString("http.message")

    val route = path("") {
      get {
        println("Received request")
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, message))
      }
    }

    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, host, port)

    bindingFuture.map { serverBinding =>
      println(s"Web server bound to ${serverBinding.localAddress}.")
    }.onComplete {
      case Failure(t) =>
        println(s"Failed to bind to $host:$port!", t)
        system.terminate()
      case Success(_) =>
    }
  }
}
