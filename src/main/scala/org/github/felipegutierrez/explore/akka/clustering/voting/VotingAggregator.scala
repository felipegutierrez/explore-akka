package org.github.felipegutierrez.explore.akka.clustering.voting

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}

import scala.concurrent.duration._

object VoteMessages {

  case class Vote(person: Person, candidate: String)

  case object VoteAccepted

  case class VoteRejected(reason: String)

}

class VotingAggregator extends Actor with ActorLogging {

  import VoteMessages._

  val CANDIDATES: Set[String] = Set("Martin", "Roland", "Jonas", "Daniel")

  context.setReceiveTimeout(60 seconds)

  override def receive: Receive = online(Set(), Map())

  /**
   * TODO: enhance personsVoted and polls with Akka persistence.
   * Since this actor handles state in personsVoted and polls we have to persist
   * its state somewhere when the master singleton dies another singleton takes over
   * (hang-over protocol) and the state has to be transferred.
   */
  def online(personsVoted: Set[String], polls: Map[String, Int]): Receive = {
    case Vote(Person(id, age), candidate) =>
      if (personsVoted.contains(id)) sender() ! VoteRejected("already voted")
      else if (age < 18) sender() ! VoteRejected("not above legal voting age")
      else if (!CANDIDATES.contains(candidate)) sender() ! VoteRejected("invalid candidate")
      else {
        log.info(s"Recording vote from person $id for $candidate")
        val candidateVotes = polls.getOrElse(candidate, 0)
        sender() ! VoteAccepted
        context.become(online(personsVoted + id, polls + (candidate -> (candidateVotes + 1))))
      }
    case ReceiveTimeout =>
      log.info(s"TIME'S UP, here are the poll results: $polls")
      context.setReceiveTimeout(Duration.Undefined)
      context.become(offline)
  }

  def offline: Receive = {
    case v: Vote =>
      log.warning(s"Received $v, which is invalid as the time is up")
      sender() ! VoteRejected("cannot accept votes after the polls closing time")
    case m =>
      log.warning(s"Received $m - will not process more messages after polls closing time")
  }
}
