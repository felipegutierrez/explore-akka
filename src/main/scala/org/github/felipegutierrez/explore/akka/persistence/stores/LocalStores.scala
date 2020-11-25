package org.github.felipegutierrez.explore.akka.persistence.stores

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence._
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

  class SimplePersistentActor extends PersistentActor with ActorLogging {
    var nMessages = 0

    override def persistenceId: String = "simple-persistent-actor"

    override def receiveCommand: Receive = {
      case "print" =>
        log.info(s"I have persisted $nMessages so far")
      case "snapshot" => saveSnapshot(nMessages)
      case SaveSnapshotSuccess(metadata) => log.info(s"save snapshot was successful: $metadata")
      case SaveSnapshotFailure(metadata, failure) => log.warning(s"save snapshot [$metadata] failed: $failure")
      case message => persist(message) { _ =>
        log.info(s"persisting $message")
        nMessages += 1
      }
    }

    override def receiveRecover: Receive = {
      case RecoveryCompleted => log.info(s"recovery done")
      case SnapshotOffer(metadata, payload: Int) =>
        log.info(s"recovered snapshot: $payload")
        nMessages = payload
      case message =>
        log.info(s"recovered: $message")
        nMessages += 1
    }
  }

}
