package org.github.felipegutierrez.explore.akka.classic.clustering.k8s

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class SimpleClusterK8sListener extends Actor with ActorLogging {

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
    case MemberJoined(member) => log.info(s"new member in the K8S cluster: ${member.address}")
    case MemberUp(member) if member.hasRole("numberCruncher") => log.info(s"WELCOME BROTHER: ${member.address}")
    case MemberUp(member) => log.info(s"K8S cluster saying welcome to the new member at K8S: ${member.address}")
    case MemberRemoved(member, previousStatus) => log.info(s"K8S cluster - member: ${member.address} was removed from $previousStatus")
    case UnreachableMember(member) => log.info(s"K8S cluster - member: ${member.address} is unreachable")
    case m: MemberEvent => log.info(s"K8S cluster - member event not handle: $m")
  }
}
