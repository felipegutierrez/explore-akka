package org.github.felipegutierrez.explore.akka.typed.monitoring

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import kamon.Kamon

import scala.util.Random

//#greeter-actor
object Greeter {

  val counterSendMsg = Kamon.counter("counter-send-msg")

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    //#greeter-send-messages
    message.replyTo ! Greeted(message.whom, context.self)
    counterSendMsg.withTag("whom", message.whom).increment()
    //#greeter-send-messages
    Behaviors.same
  }

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  final case class Greeted(whom: String, from: ActorRef[Greet])

}

//#greeter-actor

//#greeter-bot
object GreeterBot {

  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info("Greeting {} for {}", n, message.whom)
      if (n == max) {
        Behaviors.stopped
      } else {
        message.from ! Greeter.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
}

//#greeter-bot

//#greeter-main
object GreeterMain {

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      //#create-actors
      val greeter = context.spawn(Greeter(), "greeter")
      //#create-actors

      Behaviors.receiveMessage { message =>
        //#create-actors
        val replyTo = context.spawn(GreeterBot(max = 3), message.name)
        //#create-actors
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }

  final case class SayHello(name: String)

}

//#greeter-main

//#main-class
object AkkaQuickstart {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    Kamon.init()

    import GreeterMain._

    //#actor-system
    val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart")
    // val helloGreeter: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain("hello"), "AkkaQuickStart")
    // val goodDayGreeter: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain("goodDay"), "AkkaQuickStart")
    //#actor-system
    // val allGreeters = Vector(greeterMain, helloGreeter, goodDayGreeter)
    // def randomGreeter = allGreeters(Random.nextInt(allGreeters.length))
    val allPerson = List("Charles", "Bob", "Felipe", "Simone", "Fabio")

    def randomPerson = allPerson(Random.nextInt(allPerson.length))

    //#main-send-messages
    while (true) {
      // randomGreeter ! SayHello("Charles")
      greeterMain ! SayHello(randomPerson)
      Thread.sleep(1000)
    }
    //#main-send-messages
  }
}

//#main-class
//#full-example
