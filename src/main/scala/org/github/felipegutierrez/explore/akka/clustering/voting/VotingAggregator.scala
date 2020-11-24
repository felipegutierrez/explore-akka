package org.github.felipegutierrez.explore.akka.clustering.voting

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}
import akka.persistence.PersistentActor

import scala.concurrent.duration._

object VoteMessages {

  case class Vote(person: Person, candidate: String)

  case object VoteAccepted

  case class VoteRejected(reason: String)

}

class VotingAggregator extends PersistentActor with ActorLogging {

  import VoteMessages._

  val CANDIDATES: Set[String] = Set("Martin", "Roland", "Jonas", "Daniel")
  var personsVoted: Set[String] = Set()
  var polls: Map[String, Int] = Map()
  context.setReceiveTimeout(60 seconds)

  override def persistenceId: String = "voting-persistent-agg"

  override def receiveCommand: Receive = {
    case v@Vote(Person(id, age), candidate) =>
      log.info(s"received vote $v")
      if (personsVoted.contains(id)) sender() ! VoteRejected("already voted")
      else if (age < 18) sender() ! VoteRejected("not above legal voting age")
      else if (!CANDIDATES.contains(candidate)) sender() ! VoteRejected("invalid candidate")
      else {
        /* When we receive a command
         * 1 - we create an EVENT to persist into the store
         * 2 - we persist the event, pass a callback that will get triggered once the event is written
         * 3 - we update the actor's state when the event has persisted
         */
        persist(v) { e =>
          personsVoted += v.person.id
          val candidateVotes = polls.getOrElse(candidate, 0)
          polls += (candidate -> (candidateVotes + 1))
          sender() ! VoteAccepted
          log.info(s"Recording vote from person $id for $candidate")
        }
      }
    case ReceiveTimeout =>
      log.info(s"TIME'S UP, here are the poll results: $polls")
      context.setReceiveTimeout(Duration.Undefined)
      context.become(offline)
  }

  override def receiveRecover: Receive = {
    /** Best practice: follow the logic in the persist step of receiveCommand */
    case v@Vote(Person(id, age), candidate) =>
      personsVoted += v.person.id
      val candidateVotes = polls.getOrElse(candidate, 0)
      polls += (candidate -> (candidateVotes + 1))
      log.info(s"recovered vote from ${v.person} to $candidate ,polls: $polls")
  }

  // override def receive: Receive = online(Set(), Map())
  /**
   * TODO: enhance personsVoted and polls with Akka persistence.
   * Since this actor handles state in personsVoted and polls we have to persist
   * its state somewhere when the master singleton dies another singleton takes over
   * (hang-over protocol) and the state has to be transferred.
   */
  @deprecated
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
