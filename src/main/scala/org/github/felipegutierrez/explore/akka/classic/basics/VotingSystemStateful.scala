package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingSystemStateful {
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
    var candidate: Option[String] = None

    override def receive: Receive = {
      case Vote(c) => candidate = Some(c)
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAggregator extends Actor {
    var stillWaiting: Set[ActorRef] = Set()
    var currentStats: Map[String, Int] = Map()

    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        stillWaiting = citizens
        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
      case VoteStatusReply(None) =>
        // citizen hasn't voted yet
        sender() ! VoteStatusRequest // this might be an infinite loop
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        currentStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty) println(s"[VoteAggregator] poll stats: $currentStats")
        else stillWaiting = newStillWaiting
    }
  }

}
