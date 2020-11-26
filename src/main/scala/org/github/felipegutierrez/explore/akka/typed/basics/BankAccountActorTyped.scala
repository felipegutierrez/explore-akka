package org.github.felipegutierrez.explore.akka.typed.basics

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object BankAccountActorTyped {

  import BankAccountDomain._
  import PersonDomain._

  val person: Behavior[PersonTxn] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case LiveTheLife(account) =>
        context.log.info(s"received live the life with account: $account")
        account ! Balance(context.self)
        account ! Deposit(1000, context.self)
        account ! Deposit(2000, context.self)
        account ! Deposit(500, context.self)
        account ! Balance(context.self)
        account ! Withdraw(500, context.self)
        account ! Deposit(5000, context.self)
        account ! Balance(context.self)
        account ! Withdraw(2000, context.self)
        account ! Balance(context.self)
        Behaviors.same
      case msg =>
        context.log.info(s"received msg: $msg")
        Behaviors.same
    }
  }

  def main(args: Array[String]): Unit = {
    run()
  }
  
  def run(): Unit = {
    import PersonDomain._

    val bankAccountActor = ActorSystem(bankAccount(0), "BankAccountSystem")
    val actorPerson = ActorSystem(person, "BankAccountSystem")
    actorPerson ! LiveTheLife(bankAccountActor)

    Thread.sleep(5000)
    actorPerson.terminate()
    bankAccountActor.terminate()
  }

  def bankAccount(totalBalance: Int): Behavior[BankAccountTxn] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case deposit@Deposit(value, from) =>
        if (deposit.value <= 0) {
          deposit.from ! TransactionFailure(s"deposit value not allowed: ${deposit.value}")
          bankAccount(totalBalance)
        } else {
          deposit.from ! TransactionSuccess(s"success deposit of ${deposit.value}")
          bankAccount(totalBalance + deposit.value)
        }
      case withdraw@Withdraw(value, from) =>
        if (withdraw.value <= 0) {
          withdraw.from ! TransactionFailure(s"withdraw value not allowed: ${withdraw.value}")
          bankAccount(totalBalance)
        } else if (withdraw.value > totalBalance) {
          withdraw.from ! TransactionFailure(s"insufficient total balance: ")
          bankAccount(totalBalance)
        } else {
          withdraw.from ! TransactionSuccess(s"success withdraw of: ${withdraw.value}")
          bankAccount(totalBalance - withdraw.value)
        }
      case balance@Balance(from) =>
        balance.from ! TransactionSuccess(s"Balance: $totalBalance")
        bankAccount(totalBalance)
    }
  }

  object BankAccountDomain {

    trait BankAccountTxn

    case class Deposit(value: Int, from: ActorRef[PersonDomain.PersonTxn]) extends BankAccountTxn

    case class Withdraw(value: Int, from: ActorRef[PersonDomain.PersonTxn]) extends BankAccountTxn

    case class Balance(from: ActorRef[PersonDomain.PersonTxn]) extends BankAccountTxn

  }

  object PersonDomain {

    import BankAccountDomain._

    trait PersonTxn

    case class TransactionSuccess(message: String) extends PersonTxn

    case class TransactionFailure(message: String) extends PersonTxn

    case class LiveTheLife(account: ActorRef[BankAccountTxn]) extends PersonTxn

  }

}
