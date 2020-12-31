package org.github.felipegutierrez.explore.akka.classic.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

import scala.concurrent.duration._

object TimersSchedulers {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val system = ActorSystem("SchedulersTimersDemo")
    val simpleActor = system.actorOf(Props[SimpleActor])

    system.log.info("Scheduling reminder for simpleActor")

    implicit val executionContext = system.dispatcher
    // or "import system.dispatcher"
    system.scheduler.scheduleOnce(1 second) {
      simpleActor ! "reminder"
    } // or (system.dispatcher)
    system.log.info("was it scheduled?")

    // a repeating scheduler
    val routine: Cancellable = system.scheduler.scheduleWithFixedDelay(1 second, 2 seconds) {
      new Runnable {
        def run(): Unit = {
          simpleActor ! "heartbeat"
        }
      }
    }

    system.scheduler.scheduleOnce(7 seconds) {
      routine.cancel()
    }

  }

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

}
