package org.github.explore.recap

object MultiThreadRecap extends App {

  println("creating threads on JVM ...........")
  println("main thread id: " + Thread.currentThread().getId)
  val aThread = new Thread(new Runnable {
    override def run(): Unit = {
      (1 to 5).foreach(i => {
        randomSleep()
        println(s"I am running in parallel $i. thread: " + Thread.currentThread().getId)
      })
    }
  })

  val aSecThread = new Thread(() => {
    (1 to 5).foreach(i => {
      randomSleep()
      println(s"I am running in parallel with syntax sugar $i. thread: " + Thread.currentThread().getId)
    })
  })

  def randomSleep(): Unit = {
    Thread.sleep((math.random * 1000).toInt)
  }

  aThread.start()
  aSecThread.start()
  aThread.join()
  aSecThread.join()


  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount

    def getAmount(): Int = {
      amount
    }

    def allowWithdraw(money: Int): Boolean = {
      if (money <= amount) true
      else false
    }

    def withdraw(money: Int) = {
      if (allowWithdraw(money)) {
        this.amount -= money
      }
    }

    def safeWithdraw(money: Int) = this.synchronized {
      if (allowWithdraw(money)) {
        this.amount -= money
      }
    }
  }

  val ba: BankAccount = new BankAccount(50000)
  val aThirdThread = new Thread(() => {
    (1 to 20000).foreach(i => {
      ba.withdraw(i * 100)
    })
  })
  val aFourthThread = new Thread(() => {
    (1 to 20000).foreach(i => {
      ba.withdraw(i * 100)
    })
  })
  aThirdThread.start()
  aFourthThread.start()
  aThirdThread.join()
  aFourthThread.join()
  println(s"final amount " + ba.getAmount())

  val ba01: BankAccount = new BankAccount(50000)
  val aFifthThread = new Thread(() => {
    (1 to 20000).foreach(i => {
      ba01.safeWithdraw(i * 100)
    })
  })
  val aSixthThread = new Thread(() => {
    (1 to 20000).foreach(i => {
      ba01.safeWithdraw(i * 100)
    })
  })
  aFifthThread.start()
  aSixthThread.start()
  aFifthThread.join()
  aSixthThread.join()
  println(s"safeWithdraw final amount " + ba01.getAmount())


}
