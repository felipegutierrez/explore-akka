package org.github.felipegutierrez.explore.akka.clustering.basic

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ClusterSubscriber extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) => log.info(s"new member in the cluster: ${member.address}")
    case MemberUp(member) if member.hasRole("numberCruncher") => log.info(s"WELCOME BROTHER: ${member.address}")
    case MemberUp(member) => log.info(s"let's say welcome to the new member: ${member.address}")
    case MemberRemoved(member, previousStatus) => log.info(s"member: ${member.address} was removed from $previousStatus")
    case UnreachableMember(member) => log.info(s"member: ${member.address} is unreachable")
    case m: MemberEvent => log.info(s"member event not handle: $m")
  }
}
