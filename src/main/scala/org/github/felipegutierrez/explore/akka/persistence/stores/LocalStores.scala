package org.github.felipegutierrez.explore.akka.persistence.stores

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object LocalStores extends App {

  run()

  def run() = {
    val localStoreSystem = ActorSystem("localStoreSystem", ConfigFactory.load().getConfig("localStores"))
    val persistentActor = localStoreSystem.actorOf(Props[SimplePersistentActor], "persistentActor")

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
