package org.github.felipegutierrez.explore.akka.persistence.stores

import akka.actor.ActorLogging
import akka.persistence._

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
