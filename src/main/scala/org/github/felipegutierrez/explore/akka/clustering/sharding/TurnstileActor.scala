package org.github.felipegutierrez.explore.akka.clustering.sharding

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object TurnstileMessages {

  case class OysterCard(id: String, amount: Double)

  case class EntryAttempt(oysterCard: OysterCard, date: Date)

  case object EntryAccepted

  case class EntryRejected(reason: String)

  // passivate message
  case object TerminateValidator

}

object TurnstileActor {
  def props(validator: ActorRef) = Props(new TurnstileActor(validator))
}

class TurnstileActor(validator: ActorRef) extends Actor with ActorLogging {

  import TurnstileMessages._

  override def receive: Receive = {
    case o: OysterCard => validator ! EntryAttempt(o, new Date)
    case EntryAccepted => log.info("GREEN: please pass")
    case EntryRejected(reason) => log.info(s"RED: $reason")
  }
}
