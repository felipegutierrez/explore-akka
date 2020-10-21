package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}

object OneForOneSupervisionStrategy extends App {

  run()

  def run() = {
    import Supervisor._
    val system = ActorSystem("OneForOneSupervisionStrategy")
    val supervisor = system.actorOf(Props[Supervisor], "supervisor")
    supervisor ! PropsMessage(Props[FussyWordCounter], "child")

    val child = system.actorSelection("/user/supervisor/child")
    child ! "I love Akka"
    child ! Report

    child ! "Akka is awesome because I am learning to think in a whole new way"
    child ! Report
  }

  object Supervisor {

    case object Report

    case class PropsMessage(props: Props, name: String)

  }

  class Supervisor extends Actor {

    import Supervisor._

    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }

    override def receive: Receive = {
      case propsMessage: PropsMessage =>
        val childRef = context.actorOf(propsMessage.props, propsMessage.name)
        sender() ! childRef
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      // empty
    }
  }

  class FussyWordCounter extends Actor {

    import Supervisor._

    var words = 0

    override def receive: Receive = {
      case Report =>
        println(s"total words is $words")
        sender() ! words
      case "" => throw new NullPointerException("sentence is empty")
      case sentence: String =>
        if (sentence.length > 20) throw new RuntimeException("sentence is too big")
        else if (!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("sentence must start with uppercase")
        else words += sentence.split(" ").length
      case _ => throw new Exception("can only receive strings")
    }
  }

}
