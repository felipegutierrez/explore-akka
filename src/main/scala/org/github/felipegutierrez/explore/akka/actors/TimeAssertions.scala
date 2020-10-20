package org.github.felipegutierrez.explore.akka.actors

import akka.actor.{Actor, Props}

import scala.util.Random

object TimeAssertions extends App {

  object WorkerActor {

    case class WorkResult(result: Int)

    val propsWorkerActor = {
      Props(new WorkerActor)
    }
  }

  class WorkerActor extends Actor {

    import WorkerActor._

    override def receive: Receive = {
      case "work" =>
        // long computation
        Thread.sleep(500)
        sender() ! WorkResult(43)
      case "workSequence" =>
        val r = new Random()
        for (_ <- 1 to 10) {
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }

}
