package org.github.felipegutierrez.explore.akka.classic.clustering.voting

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object VotingStation {
  def props(votingAggregator: ActorRef) = Props(new VotingStation(votingAggregator))
}

class VotingStation(votingAggregator: ActorRef) extends Actor with ActorLogging {

  import VoteMessages._

  override def receive: Receive = {
    case v: Vote => votingAggregator ! v
    case VoteAccepted => log.info("Vote was accepted")
    case VoteRejected(reason) => log.warning(s"Vote was rejected: $reason")
  }
}
