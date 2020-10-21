package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, AllForOneStrategy, Props, SupervisorStrategy}

object AllForOneSupervisionStrategy extends App {

  run()

  def run() = {
    import Supervisor._
    val system = ActorSystem("AllForOneSupervisionStrategy")
    val allForOneSupervisor = system.actorOf(Props[Supervisor], "allForOneSupervisor")
    allForOneSupervisor ! PropsMessage(Props[FussyWordCounter], "child1")
    val child1 = system.actorSelection("/user/allForOneSupervisor/child1")

    allForOneSupervisor ! PropsMessage(Props[FussyWordCounter], "child2")
    val child2 = system.actorSelection("/user/allForOneSupervisor/child2")

    child2 ! "Testing supervision"
    child2 ! Report

    child1 ! ""
    Thread.sleep(500)
    child2 ! Report
  }

  object Supervisor {

    case object Report

    case class PropsMessage(props: Props, name: String)

  }

  class Supervisor extends Actor {

    import Supervisor._

    override val supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
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
