package org.github.felipegutierrez.explore.akka.classic.persistence.event_sourcing

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistAsyncDemo extends App {

  run()

  def run() = {
    val system = ActorSystem("PersistAsyncDemo")
    val eventAggregator = system.actorOf(Props[EventAggregator], "eventAggregator")
    val streamProcessor = system.actorOf(CriticalStreamProcessor.props(eventAggregator), "streamProcessor")

    streamProcessor ! Command("command1")
    streamProcessor ! Command("command2")
  }

  case class Command(contents: String)
  case class Event(contents: String)

  class CriticalStreamProcessor(eventAggregator: ActorRef) extends PersistentActor with ActorLogging {

    override def persistenceId: String = "critical-stream-processor"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        /** If the state mutates at 1 and 2 we may lose consistence using persistAsync()
         * during the TIME GAP. This is because persistAsync() does NOT STASH messages
         * between persisting an EVENT (1) and processing the event (2). In this case
         * we should use persist() instead of persistAsync() */
        eventAggregator ! s"Processing $contents"
        // 1 - mutate
        persistAsync(Event(contents)) /* 3 - TIME GAP */ { e =>
          // 2 - mutate
          eventAggregator ! e
        }

        /** BUT, persistAsync() still ensures the order of messages persisted to the JOURNAL.
         * It means that events at (1) and (4) have ordering guarantees between each other.
         * And events at (2) and (5) also have ordering guarantees between each other. */
        val processedContents = contents + "_processed"
        // 4 - another even to persist
        persistAsync(Event(processedContents)) /* 3 - TIME GAP */ { e =>
          // 5 - another Event to process
          eventAggregator ! e
        }
    }

    override def receiveRecover: Receive = {
      case message => log.info(s"Recovered: $message")
    }
  }

  class EventAggregator extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"$message")
    }
  }

  object CriticalStreamProcessor {
    def props(eventAggregator: ActorRef) = Props(new CriticalStreamProcessor(eventAggregator))
  }
}
