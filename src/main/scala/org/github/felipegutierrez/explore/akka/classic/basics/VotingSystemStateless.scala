package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingSystemStateless {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val actorSystem = ActorSystem("System")
    val alice = actorSystem.actorOf(Props[Citizen], "Alice")
    val bob = actorSystem.actorOf(Props[Citizen], "Bob")
    val peter = actorSystem.actorOf(Props[Citizen], "Peter")
    val felipe = actorSystem.actorOf(Props[Citizen], "Felipe")

    alice ! Vote("Martin")
    bob ! Vote("Jonas")
    peter ! Vote("Roland")
    felipe ! Vote("Roland")

    val voteAggregator = actorSystem.actorOf(Props[VoteAggregator])
    voteAggregator ! AggregateVotes(Set(alice, bob, peter, felipe))
  }

  case class Vote(candidate: String)

  case object VoteStatusRequest

  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate: String): Receive = {
      case VoteStatusRequest =>
        // this makes the vote to be stateful through the context.become()
        println(s"[VoteStatusRequest] candidate: $candidate")
        sender() ! VoteStatusReply(Some(candidate))
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAggregator extends Actor {
    override def receive: Receive = {
      case AggregateVotes(citizenRef: Set[ActorRef]) =>
        citizenRef.foreach(c => c ! VoteStatusRequest)
        context.become(awaitingStatus(citizenRef, Map()))
    }

    def awaitingStatus(stillWaiting: Set[ActorRef], currentStats: Map[String, Int]): Receive = {
      case VoteStatusReply(None) =>
        // citizen hasn't voted yet
        sender() ! VoteStatusRequest // this might be an infinite loop
      case VoteStatusReply(Some(candidate)) =>
        println(s"[VoteAggregator] candidate: $candidate")
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty) println(s"[VoteAggregator] poll stats: $newStats")
        else context.become(awaitingStatus(newStillWaiting, newStats))
    }
  }

}
