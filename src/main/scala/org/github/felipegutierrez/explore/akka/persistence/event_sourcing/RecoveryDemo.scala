package org.github.felipegutierrez.explore.akka.persistence.event_sourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted}

object RecoveryDemo extends App {

  run()

  def run() = {
    val system = ActorSystem("RecoveryDemo")
    val recoveryActor = system.actorOf(Props[RecoveryActor], "recoveryActor")
    for (i <- 1 to 1000) {
      recoveryActor ! Command(s"command $i")
    }
  }

  case class Command(contents: String)

  case class Event(id: Int, contents: String)

  class RecoveryActor extends PersistentActor with ActorLogging {

    override def persistenceId: String = "recovery-actor"

    override def receiveCommand: Receive = online(0)

    def online(latestPersistedEventId: Int): Receive = {
      case Command(contents) =>
        persist(Event(latestPersistedEventId, contents)) { event =>
          log.info(s"Successfully persisted $event, recovery is ${if (this.recoveryFinished) "" else "NOT"} finished.")
          context.become(online(latestPersistedEventId + 1))
        }
    }

    override def receiveRecover: Receive = {
      case RecoveryCompleted =>
        // additional initialization
        log.info("I have finished recovering")

      case Event(id, contents) =>
        //if (contents.contains("314"))
        //   throw new RuntimeException("I can't take this anymore!")
        log.info(s"Recovered: $contents, recovery is ${if (this.recoveryFinished) "" else "NOT"} finished.")
        context.become(online(id + 1))
      /*
        this will NOT change the event handler during recovery
        AFTER recovery the "normal" handler will be the result of ALL the stacking of context.becomes.
       */
    }

    override def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = {
      log.error("I failed at recovery")
      super.onRecoveryFailure(cause, event)
    }

    // override def recovery: Recovery = Recovery(toSequenceNr = 100)
    // override def recovery: Recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.Latest)
    // override def recovery: Recovery = Recovery.none
  }

}
