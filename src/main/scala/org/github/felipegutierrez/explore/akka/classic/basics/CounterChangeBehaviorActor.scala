package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorSystem, Props}

object CounterChangeBehaviorActor extends App {
  run()

  def run() = {
    import Counter._
    val actorSystem = ActorSystem("System")
    val countActor = actorSystem.actorOf(Props[Counter], "CounterChangeBehaviorActor")
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

    override def receive: Receive = countReceive(0)

    def countReceive(counter: Int): Receive = {
      case Increment =>
        println(s"[$counter] incrementing...")
        // the context.become will handle the next state (counter + 1)
        context.become(countReceive(counter + 1))
      case Decrement =>
        println(s"[$counter] decrementing...")
        // the context.become will handle the next state (counter - 1)
        context.become(countReceive(counter - 1))
      case Print => println(s"counter is $counter")
    }
  }

}
