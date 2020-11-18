package org.github.felipegutierrez.explore.akka.clustering.chat

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class ChatApp(nickname: String, port: Int) {
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
       """.stripMargin)
    .withFallback(ConfigFactory.load("clustering/clusteringChat.conf"))

  val system = ActorSystem("RTJVMCluster", config)
  val chatActor = system.actorOf(Props[ChatActor], "chatActor")

  import ClusteringChat.ChatDomain._

  scala.io.Source.stdin.getLines().foreach { line =>
    chatActor ! UserMessage(line)
  }
}
