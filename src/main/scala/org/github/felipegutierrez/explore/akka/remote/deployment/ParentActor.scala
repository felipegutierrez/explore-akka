package org.github.felipegutierrez.explore.akka.remote.deployment

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import org.github.felipegutierrez.explore.akka.remote.hello.SimpleActor

class ParentActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case "create" =>
      log.info("Creating remote child")
      val child = context.actorOf(Props[SimpleActor], "remoteChild")
      context.watch(child)
    case Terminated(ref) =>
      log.warning(s"Child $ref terminated")
  }
}
