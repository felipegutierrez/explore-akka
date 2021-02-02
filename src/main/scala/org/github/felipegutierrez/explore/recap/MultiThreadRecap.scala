package org.github.felipegutierrez.explore.recap

import com.typesafe.scalalogging.LazyLogging

import scala.util.Random

object MultiThreadRecap {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {

    val multiThreadRecap: MultiThreadRecap = new MultiThreadRecap()
    // multiThreadRecap.runTwoThreads(5)

    unsafeWithdrawAndDeposit(500000)
    safeWithdrawAndDeposit(500000)
  }

  def unsafeWithdrawAndDeposit(amount: Int): Int = {
    val ba: BankAccount = new BankAccount(amount)
    for (_ <- 0 to amount) {
      new Thread(() => ba.withdraw(1)).start()
    }
    for (_ <- 0 to amount) {
      new Thread(() => ba.deposit(1)).start()
    }
    println(s"final amount " + ba.getAmount())
    ba.getAmount()
  }

  def safeWithdrawAndDeposit(amount: Int): Int = {
    val safeBa: BankAccount = new BankAccount(amount)
    for (_ <- 0 to amount) {
      new Thread(() => safeBa.safeWithdraw(1)).start()
    }
    for (_ <- 0 to amount) {
      new Thread(() => safeBa.safeDeposit(1)).start()
    }
    println(s"safe final amount " + safeBa.getAmount())
    safeBa.getAmount()
  }

  def randomSleep(): Unit = {
    Thread.sleep((Random.nextInt(5) * 100))
  }
}

class MultiThreadRecap extends LazyLogging {

  import MultiThreadRecap._

  def runTwoThreads(number: Int): Long = {
    logger.info("creating threads on JVM ...........")
    val mainId = Thread.currentThread().getId
    logger.info("main thread id: " + mainId)
    val aThread = new Thread(new Runnable {
      override def run(): Unit = {
        (1 to number).foreach(i => {
          randomSleep()
          logger.info("I am running in parallel " + i)
          logger.info(s"thread: " + Thread.currentThread().getId)
        })
      }
    })

    val aSecThread = new Thread(() => {
      (1 to number).foreach(i => {
        randomSleep()
        logger.info("I am running in parallel with syntax sugar " + i)
        logger.info(s"thread: " + Thread.currentThread().getId)
      })
    })

    aThread.start()
    aSecThread.start()
    aThread.join()
    aSecThread.join()
    mainId
  }
}

class BankAccount(private var amount: Int) {
  override def toString: String = "" + amount

  def getAmount(): Int = amount

  //    def allowWithdraw(money: Int): Boolean = {
  //      if (money <= amount) true
  //      else false
  //    }

  def withdraw(money: Int) = this.amount -= money

  def deposit(money: Int) = this.amount += money

  def safeWithdraw(money: Int) = this.synchronized {
    this.amount -= money
  }

  def safeDeposit(money: Int) = this.synchronized {
    this.amount += money
  }
}
