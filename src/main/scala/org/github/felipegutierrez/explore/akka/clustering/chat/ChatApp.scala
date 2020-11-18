package org.github.felipegutierrez.explore.akka.clustering.chat

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

class ChatApp(nickname: String, port: Int) extends App {

  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
       """.stripMargin)
    .withFallback(ConfigFactory.load("clustering/clusteringChat.conf"))

  val system = ActorSystem("RTJVMCluster", config)
  val chatActor = system.actorOf(ChatActor.props(nickname, port), "chatActor")

  import ChatDomain._

  scala.io.Source.stdin.getLines().foreach { line =>
    chatActor ! UserMessage(line)
  }
}

object Alice extends ChatApp("Alice", 2551)

object Bob extends ChatApp("Bob", 2552)

object Felipe extends ChatApp("Felipe", 2553)
