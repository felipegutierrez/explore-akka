package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{Actor, ActorSystem, Props}

object ChildActorsNaiveBank {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    import CreditCard._
    import NaiveBankAccount._
    val system = ActorSystem("NaiveBank")
    val bankAccount = system.actorOf(Props[NaiveBankAccount], "NaiveBankAccount")
    bankAccount ! InitializeAccount
    bankAccount ! Deposit(100)

    Thread.sleep(500)
    val creditCard = system.actorSelection("/user/NaiveBankAccount/CreditCard")
    creditCard ! CheckStatus
  }

  object NaiveBankAccount {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object InitializeAccount

  }

  class NaiveBankAccount extends Actor {

    import CreditCard._
    import NaiveBankAccount._

    var amount = 0

    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard], "CreditCard")
        // DANGER: NEVER USE AN ACTOR SELF TO A MESSAGE BECAUSE IT VIOLATES THE AKKA PRINCIPLE TO
        // SEND ONLY MESSAGES THAT ARE IMMUTABLE. PASSING 'THIS' ONE CAN ACCESS THE METHOD OF THIS ACTOR
        // IN ANOTHER ACTOR
        creditCardRef ! AttachToAccount(this)
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }

    def deposit(funds: Int) = {
      println(s"${self.path} depositing $funds on top of $amount")
      amount += funds
    }

    def withdraw(funds: Int) = {
      println(s"${self.path} withdrawing $funds from $amount")
      amount -= funds
    }
  }

  object CreditCard {

    // DANGER: NEVER PASS AN ACTOR TO THE ACTOR MESSAGE!!!!
    // THE RIGHT WAY TO DO IS USING ActorRef on the parameters of the message.
    // THIS CALL 'close over' OF MUTABLE STATE
    case class AttachToAccount(bankAccount: NaiveBankAccount)

    case object CheckStatus

  }

  class CreditCard extends Actor {

    import CreditCard._

    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachToAccount(account))
    }

    def attachToAccount(account: NaiveBankAccount): Receive = {
      case CheckStatus => {
        println(s"${self.path} your message has been processed.")
        // DANGER: I CAN ACCESS THE withdraw METHOD, SO WHY NOT!
        // BUT IT VIOLATES THE AKKA PRINCIPLE TO USE ONLY MESSAGES!!!
        account.withdraw(1)
      }
    }
  }

}
