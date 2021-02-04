package org.github.felipegutierrez.explore.akka.typed.monitoring

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import kamon.Kamon

import scala.util.Random

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

object AkkaQuickStartWithKamon {
    def main(args: Array[String]): Unit = {
      run()
    }

  def run() = {
    Kamon.init()
    import GreeterMain._
    val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart")
    val allPerson = List("Charles", "Bob", "Felipe", "Simone", "Fabio")
    def randomPerson = allPerson(Random.nextInt(allPerson.length))
    while (true) {
      greeterMain ! SayHello(randomPerson)
      Thread.sleep(1000)
    }
  }
}
