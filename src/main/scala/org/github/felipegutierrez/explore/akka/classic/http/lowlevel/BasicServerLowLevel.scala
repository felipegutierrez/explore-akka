package org.github.felipegutierrez.explore.akka.classic.http.lowlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.stream.scaladsl.Sink

import scala.util.{Failure, Success}

object BasicServerLowLevel {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("BasicServerLowLevel")
    import system.dispatcher
    println(s"type on the console: 'curl localhost:8080'")
    val serverSource = Http().newServerAt("localhost", 8080).connectionSource()
    val connectionSink = Sink.foreach[IncomingConnection] { connection =>
      println(s"Accepted incoming connection from: ${connection.remoteAddress}")
    }
    val serverBindingFuture = serverSource.to(connectionSink).run()
    serverBindingFuture.onComplete {
      case Success(_) => println(s"server binding successful")
      case Failure(ex) => println(s"server binding failed: $ex")
    }
  }
}
