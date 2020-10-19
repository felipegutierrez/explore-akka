package org.github.felipegutierrez.explore.akka.actors

import akka.actor.{Actor, ActorSystem, Props}

object CounterActor extends App {
  run()

  def run() = {
    import Counter._
    val actorSystem = ActorSystem("System")
    val countActor = actorSystem.actorOf(Props[Counter], "Counter")
    (1 to 5).foreach(_ => countActor ! Increment)
    (1 to 3).foreach(_ => countActor ! Decrement)
    countActor ! Print
  }

  object Counter {

    case object Increment

    case object Decrement

    case object Print

  }

  class Counter extends Actor {

    import Counter._

    var count = 0

    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"[counter] current count is: $count")
    }
  }

}
