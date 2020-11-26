package org.github.felipegutierrez.explore.akka.classic.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}

import scala.concurrent.duration._

object TimerBasedHeartbeatDemo extends App {

  run()

  def run() = {
    val system = ActorSystem("TimerBasedHeartbeatDemo")
    val timerHeartbeatActor = system.actorOf(Props[TimerBasedHeartbeatActor], "timerActor")
    import TimerBasedHeartbeatActor._
    implicit val executionContext = system.dispatcher
    system.scheduler.scheduleOnce(5 seconds) {
      timerHeartbeatActor ! Stop
    }
  }

  object TimerBasedHeartbeatActor {

    case object Start

    case object Stop

    case object MyTimerKey

    case object Reminder

  }

  class TimerBasedHeartbeatActor extends Actor with ActorLogging with Timers {

    import TimerBasedHeartbeatActor._

    timers.startSingleTimer(MyTimerKey, Start, 500 millis)

    override def receive: Receive = {
      case Start =>
        log.info("bootstrapping")
        timers.startTimerWithFixedDelay(MyTimerKey, Reminder, 1 second)
      case Stop =>
        log.warning("stopping!")
        timers.cancel(MyTimerKey)
        context.stop(self)
      case Reminder =>
        log.info("i am alive")
    }
  }

}
