package org.github.felipegutierrez.explore.akka.clustering.router

import akka.actor.{Actor, ActorLogging}

class WorkerRoutee extends Actor with ActorLogging {
  override def receive: Receive = {
    case SimpleTask(contents) =>
      log.info(s"Processing: $contents")
  }
}
