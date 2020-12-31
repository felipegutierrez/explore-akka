package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


object ActorCapabilities {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val actorSystem = ActorSystem("ActorsCapabilities")
    println(actorSystem.name)

    val simpleActor = actorSystem.actorOf(Props[SimpleActor], "SimpleActor")
    simpleActor ! "hello actor"
    simpleActor ! 42
    simpleActor ! SpecialMessage("a especial message")
    simpleActor ! SendMessageToYourserlf("a message to myself")

    val aliceActor = actorSystem.actorOf(Props[SimpleActor], "Alice")
    val bobActor = actorSystem.actorOf(Props[SimpleActor], "Bob")
    aliceActor ! SayHiTo(bobActor)

    aliceActor ! "Hi!" // showing the dead letters, when the actor reference is null
    aliceActor ! WirelessPhoneMessage("Hi!", bobActor)
  }

  class SimpleActor extends Actor {
    override def receive: PartialFunction[Any, Unit] = {
      case message: String => println(s"$self : Message received[ $message ]")
      case number: Int => println(s"number received[ $number ]")
      case special: SpecialMessage => println(s"special message received[ $special ]")
      case yourselfMsg: SendMessageToYourserlf =>
        println(s"received[ $yourselfMsg ]")
        self ! yourselfMsg.content
      case SayHiTo(ref) => ref ! "Hi!"
      case "Hi!" => context.sender() ! "Hello, there!" // replying the hi message
      case wirelessPhoneMessage: WirelessPhoneMessage =>
        println("received WirelessPhoneMessage")
        wirelessPhoneMessage.ref forward (wirelessPhoneMessage.content + "-sssss")
      case msg => println(s"word count. I cannot understand ${msg.toString}") // keep it at the last message
    }
  }

  case class SpecialMessage(content: String)

  case class SendMessageToYourserlf(content: String)

  case class SayHiTo(ref: ActorRef)

  case class WirelessPhoneMessage(content: String, ref: ActorRef)

}
