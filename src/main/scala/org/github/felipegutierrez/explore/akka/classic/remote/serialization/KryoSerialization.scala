package org.github.felipegutierrez.explore.akka.classic.remote.serialization

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

// case class Book(name: String, year: Int)

object KryoSerialization_Local {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("kryoSerializablePerson"))

    val system = ActorSystem("LocalSystem", config)
    val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

    actorSelection ! Person("Alice", 23)
  }
}

object KryoSerialization_Remote {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("kryoSerializablePerson"))

    val system = ActorSystem("RemoteSystem", config)
    val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
  }
}
