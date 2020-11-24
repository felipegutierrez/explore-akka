package org.github.felipegutierrez.explore.akka.persistence.event_sourcing

import java.util.UUID

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import com.typesafe.config.ConfigFactory

object PersistentActorsExercise extends App {

  run()

  def run() = {

    val system = ActorSystem("PersistentVotingActors", ConfigFactory.load().getConfig("votingPersistentExercise"))
    val votingActor = system.actorOf(Props[VotingActor], "votingActor")

    scala.io.Source.stdin.getLines().foreach { line =>
      votingActor ! Vote(UUID.randomUUID().toString, line)
    }
  }

  // COMMANDS
  case class Vote(citizenPID: String, candidate: String)

  // EVENTS
  case class VoteRecorded(voteId: Int, citizenPID: String, candidate: String)

  case class VoteRejected(msg: String)

  class VotingActor extends PersistentActor with ActorLogging {

    val CANDIDATES: Set[String] = Set("Martin", "Roland", "Jonas", "Daniel")
    var latestVoteIndex = 0
    var polls: Map[String, Int] = Map()

    override def persistenceId: String = "votingIds"

    override def receiveCommand: Receive = {
      case Vote(citizenPID, candidate) =>
        if (!CANDIDATES.contains(candidate)) sender() ! VoteRejected("invalid candidate")
        else {
          /* When we receive a command
         * 1 - we create an EVENT to persist into the store
         * 2 - we persist the event, pass a callback that will get triggered once the event is written
         * 3 - we update the actor's state when the event has persisted
         */
          log.info(s"received vote from $citizenPID to $candidate")
          persist(VoteRecorded(latestVoteIndex, citizenPID, candidate)) { e =>
            latestVoteIndex += 1
            val candidateVotes = polls.getOrElse(candidate, 0)
            polls += (candidate -> (candidateVotes + 1))
            log.info(s"Persisted $e as vote from #${e.citizenPID} to ${e.candidate}. Polls: $polls")
          }
        }
    }

    override def receiveRecover: Receive = {
      /** Best practice: follow the logic in the persist step of receiveCommand */
      case VoteRecorded(voteIndex, citizenPID, candidate) =>
        latestVoteIndex = voteIndex
        // totalAmount += amount
        val candidateVotes = polls.getOrElse(candidate, 0)
        polls += (candidate -> (candidateVotes + 1))
        log.info(s"recovered vote #$voteIndex from $citizenPID for $candidate ,polls: $polls")
    }
  }

}
