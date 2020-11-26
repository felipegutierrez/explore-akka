package org.github.felipegutierrez.explore.akka.classic.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

import scala.concurrent.duration._

object SelfClosingScheduler extends App {

  run()

  def run() = {

    val system = ActorSystem("SelfClosingScheduler")
    val selfClosingActor = system.actorOf(Props[SelfClosingActor])

    implicit val executionContext = system.dispatcher
    val pingRoutine: Cancellable = system.scheduler.scheduleWithFixedDelay(250 milliseconds, 250 milliseconds) {
      new Runnable {
        var i = 1

        def run(): Unit = {
          selfClosingActor ! s"[$i] ping"
          if (i % 10 == 0) Thread.sleep(1000)
          i += 1
        }
      }
    }

    val pongRoutine: Cancellable = system.scheduler.scheduleOnce(1 second) {
      new Runnable {
        def run(): Unit = {
          selfClosingActor ! "PONG"
        }
      }
    }
  }

  class SelfClosingActor extends Actor with ActorLogging {

    var schedule = createTimeoutWindow()

    def createTimeoutWindow(): Cancellable = {
      implicit val executionContext = context.system.dispatcher
      context.system.scheduler.scheduleOnce(1 second) {
        self ! "timeout"
      }
    }

    override def receive: Receive = {
      case "timeout" =>
        log.info("Stopping SelfClosingActor")
        context.stop(self)
      case message =>
        log.info(s"received message ${message.toString}")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

}
