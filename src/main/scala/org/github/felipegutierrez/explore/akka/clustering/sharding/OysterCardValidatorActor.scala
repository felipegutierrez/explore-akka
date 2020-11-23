package org.github.felipegutierrez.explore.akka.clustering.sharding

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate

import scala.concurrent.duration._

/**
 * Store an enormous amount of data so it needs to use akka-cluster sharding.
 */
class OysterCardValidatorActor extends Actor with ActorLogging {

  import TurnstileMessages._

  override def preStart(): Unit = {
    super.preStart()
    log.info("Validator starting")
    context.setReceiveTimeout(10 seconds)
  }

  override def receive: Receive = {
    case EntryAttempt(card@OysterCard(id, amount), _) =>
      log.info(s"Validating $card")
      if (amount > 2.5) sender() ! EntryAccepted
      else sender() ! EntryRejected(s"[$id] not enough funds, please top up")
    case ReceiveTimeout =>
      context.parent ! Passivate(TerminateValidator)
    case TerminateValidator =>

      /** I am sure that I won't be contacted again, so safe to stop */
      context.stop(self)
  }
}
