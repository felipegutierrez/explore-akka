package org.github.felipegutierrez.explore.akka.clustering.wordcount

import akka.actor.{Actor, ActorLogging, ActorRef, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.duration._

class WordCountMaster extends Actor with ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(3 seconds)

  val cluster = Cluster(context.system)

  var workers: Map[Address, ActorRef] = Map()
  var pendingRemoval: Map[Address, ActorRef] = Map()

  /** subscribe the events that the master node must handle on the cluster and the master itself */
  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  /** unsubscribe the master from the cluster */
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = handleClusterEvents.orElse(handleWorkerRegistration)

  def handleClusterEvents: Receive = {
    case MemberUp(member) if member.hasRole("worker") =>
      log.info(s"Member is up: ${member.address}")
      if (pendingRemoval.contains(member.address)) {
        pendingRemoval = pendingRemoval - member.address
      } else {
        val workerSelection = context.actorSelection(s"${member.address}/user/worker")
        workerSelection.resolveOne().map(ref => (member.address, ref)).pipeTo(self)
      }
    case UnreachableMember(member) if member.hasRole("worker") =>
      log.info(s"Member detected as unreachable: ${member.address}")
      val workerOption = workers.get(member.address)
      workerOption.foreach { ref =>
        pendingRemoval = pendingRemoval + (member.address -> ref)
      }
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Member ${member.address} removed after $previousStatus")
      workers = workers - member.address
    case m: MemberEvent =>
      log.info(s"Another member event I don't care about: $m")
  }

  /** receives messages from: (ref => (member.address, ref)).pipeTo(self) */
  def handleWorkerRegistration: Receive = {
    case pair: (Address, ActorRef) =>
      log.info(s"Registering worker: $pair")
      workers = workers + pair
  }
}
