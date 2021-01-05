package org.github.felipegutierrez.explore.akka.classic.persistence.stores

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object LocalStores {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val localStoreSystem = ActorSystem("localStoreSystem", ConfigFactory.load().getConfig("localStores"))
    val persistentActor = localStoreSystem.actorOf(SimplePersistentActor.props("local-actor"), "persistentActor")

    for (i <- 1 to 1000) {
      persistentActor ! s"i love akka $i"
    }
    persistentActor ! "print"
    persistentActor ! "snapshot"
    for (i <- 1001 to 2000) {
      persistentActor ! s"i love akka $i"
    }
  }

}
