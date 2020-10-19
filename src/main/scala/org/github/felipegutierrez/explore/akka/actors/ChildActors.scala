package org.github.felipegutierrez.explore.akka.actors

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}

object ChildActors extends App {
  run()

  def run() = {
    import Parent._
    val system = ActorSystem("ChildActors")
    val parent = system.actorOf(Props[Parent], "Parent")
    parent ! CreateChild("child")
    parent ! TellChild("hey kid!")

    // actor selection
    val childSelection: ActorSelection = system.actorSelection("/user/Parent/child")
    childSelection ! "I found you!"
  }

  object Parent {

    case class CreateChild(name: String)

    case class TellChild(message: String)

  }

  class Parent extends Actor {

    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating a child")
        // creating a new Child actor inside the Parent actor
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} I got: $message")
    }
  }

}
