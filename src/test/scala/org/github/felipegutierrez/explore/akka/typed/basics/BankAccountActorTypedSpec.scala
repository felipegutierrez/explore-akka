package org.github.felipegutierrez.explore.akka.typed.basics

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BankAccountActorTypedSpec extends AnyWordSpec
  with BeforeAndAfterAll
  with Matchers {

  import BankAccountActorTyped.BankAccountDomain._

  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "when the bank account actor typed deposit and withdraw money" should {
    "have the right balance in the end" in {
      val bankAccountActor = testKit.spawn(BankAccountActorTyped.bankAccount(0), "BankAccountSystemSpec")
      val probePersonActor = testKit.createTestProbe[BankAccountActorTyped.PersonDomain.PersonTxn]("PersonSystemSpec")

      val deposit01: Int = 2000
      val deposit02: Int = 500
      val withdraw01: Int = 700
      val finalBalance: Int = deposit01 + deposit02 - withdraw01

      bankAccountActor ! Deposit(deposit01, probePersonActor.ref)
      bankAccountActor ! Deposit(deposit02, probePersonActor.ref)
      bankAccountActor ! Withdraw(withdraw01, probePersonActor.ref)
      Thread.sleep(2000)
      bankAccountActor ! Balance(probePersonActor.ref)

      probePersonActor.expectMessage(BankAccountActorTyped.PersonDomain.TransactionSuccess(s"success deposit of ${deposit01}"))
      probePersonActor.expectMessage(BankAccountActorTyped.PersonDomain.TransactionSuccess(s"success deposit of ${deposit02}"))
      probePersonActor.expectMessage(BankAccountActorTyped.PersonDomain.TransactionSuccess(s"success withdraw of: ${withdraw01}"))
      probePersonActor.expectMessage(BankAccountActorTyped.PersonDomain.TransactionSuccess(s"Balance: $finalBalance"))
    }
  }
}
