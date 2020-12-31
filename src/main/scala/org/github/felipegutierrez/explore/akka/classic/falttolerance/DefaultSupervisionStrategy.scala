package org.github.felipegutierrez.explore.akka.classic.falttolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object DefaultSupervisionStrategy {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    import Parent._
    val system = ActorSystem("DefaultSupervisionStrategy")
    val supervisor = system.actorOf(Props[Parent], "supervisor")
    supervisor ! FailChild
    for (i <- 0 to 10) {
      supervisor ! CheckChild(i)
      Thread.sleep(500)
    }
  }

  object Parent {
    object Fail
    object FailChild
    case class CheckChild(i: Int)
    case class Check(i: Int)
  }

  class Parent extends Actor {
    import Parent._
    private val child = context.actorOf(Props[Child], "supervisedChild")

    override def receive: Receive = {
      case FailChild => child ! Fail
      case CheckChild(i) => child ! Check(i)
    }
  }

  class Child extends Actor with ActorLogging {
    import Parent._
    override def preStart(): Unit = log.info("supervised child started")
    override def postStop(): Unit = log.info("supervised child stopped")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"supervised actor restarting because of ${reason.getMessage}")
    override def postRestart(reason: Throwable): Unit =
      log.info("supervised actor restarted")

    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now")
        throw new RuntimeException("I failed")
      case Check(i) =>
        log.info(s"alive and kicking $i")
    }
  }

}
