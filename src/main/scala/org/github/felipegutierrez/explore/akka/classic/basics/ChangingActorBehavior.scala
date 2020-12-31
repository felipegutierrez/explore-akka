package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehavior {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    import Mon._
    val system = ActorSystem("ChangingActorBehavior")
    val fussyKidStateless = system.actorOf(Props[StatelessFussyKid])
    val mon = system.actorOf(Props[Mon])
    mon ! MonStart(fussyKidStateless)
  }

  object StatelessFussyKid {

    case object KidAccept

    case object KidReject

    val HAPPY = "happy"
    val SAD = "sad"
  }

  class StatelessFussyKid extends Actor {

    import Mon._
    import StatelessFussyKid._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(happyReceive, false)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidAccept
    }
  }

  object Mon {

    case class MonStart(kidRef: ActorRef)

    case class Food(food: String)

    case class Ask(msg: String)

    val VEGETABLE = "vegetable"
    val CHOCOLATE = "chocolate"
  }

  class Mon extends Actor {

    import Mon._
    import StatelessFussyKid._

    override def receive: Receive = {
      case MonStart(kidRef) =>
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Ask("do you want to play!")
      case KidAccept => println("yes, my kid is happy!")
      case KidReject => println("my kid is sad =(")
    }
  }

}
