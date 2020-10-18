package org.github.felipegutierrez.explore.akka.actors

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.github.felipegutierrez.explore.akka.actors.BankAccountActor.Person.LiveTheLife

object BankAccountActor extends App {

  run()

  def run() = {
    val actorSystem = ActorSystem("BankAccountSystem")
    val bankAccount = actorSystem.actorOf(Props[BankAccount], "BankAccount")
    val person = actorSystem.actorOf(Props[Person], "Person")
    person ! LiveTheLife(bankAccount)
  }

  object BankAccount {
    case class Deposit(value: Int)
    case class Withdraw(value: Int)
    case object Balance
    case class TransactionSuccess(message: String)
    case class TransactionFailure(message: String)
  }

  class BankAccount extends Actor {
    import BankAccount._
    var totalBalance = 0

    override def receive: Receive = {
      case Deposit(deposit) =>
        if (deposit <= 0) {
          context.sender() ! TransactionFailure(s"deposit value not allowed: ${deposit}")
        } else {
          totalBalance += deposit
          context.sender() ! TransactionSuccess(s"success deposit")
        }
      case Withdraw(withdraw) =>
        if (withdraw <= 0) {
          context.sender() ! TransactionFailure(s"withdraw value not allowed: ${withdraw}")
        } else if (withdraw > totalBalance) {
          context.sender() ! TransactionFailure(s"insufficient total balance: ")
        } else {
          totalBalance -= withdraw
          context.sender() ! TransactionSuccess(s"success withdraw")
        }
      case Balance => context.sender() ! TransactionSuccess(s"Balance: $totalBalance")
      case msg =>
        println(s"I cannot understand ${msg.toString}")
        context.sender() ! Failure
    }
  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends Actor {
    import BankAccount._
    import Person._
    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(1000)
        account ! Deposit(2000)
        account ! Deposit(500)
        account ! Balance
        account ! Withdraw(500)
        account ! Deposit(5000)
        account ! Balance
        account ! Withdraw(2000)
        account ! Balance
      case msg => println(msg.toString)
    }
  }
}
