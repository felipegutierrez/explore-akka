package org.github.felipegutierrez.explore.akka.clustering.router

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig

case class SimpleTask(contents: String)
case object StartWork

class MasterWithRouter extends Actor with ActorLogging {

  val router = context.actorOf(FromConfig.props(Props[WorkerRoutee]), "clusterAwareRouter")

  override def receive: Receive = {
    case StartWork =>
      log.info("Starting work")
      (1 to 100).foreach { id =>
        router ! SimpleTask(s"Simple task $id")
      }
  }
}
