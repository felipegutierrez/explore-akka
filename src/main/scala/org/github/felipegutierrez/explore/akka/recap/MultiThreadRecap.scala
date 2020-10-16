package org.github.felipegutierrez.explore.akka.recap

object MultiThreadRecap extends App {

  def run(): Unit = {
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


    aThread.start()
    aSecThread.start()
    aThread.join()
    aSecThread.join()


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

    val ba: BankAccount = new BankAccount(50000)
    for (_ <- 1 to 50000) {
      new Thread(() => ba.withdraw(1)).start()
    }
    for (_ <- 1 to 50000) {
      new Thread(() => ba.deposit(1)).start()
    }
    println(s"final amount " + ba.getAmount())

    val safeBa: BankAccount = new BankAccount(50000)
    for (_ <- 1 to 50000) {
      new Thread(() => safeBa.safeWithdraw(1)).start()
    }
    for (_ <- 1 to 50000) {
      new Thread(() => safeBa.safeDeposit(1)).start()
    }
    println(s"safe final amount " + safeBa.getAmount())
  }

  def randomSleep(): Unit = {
    Thread.sleep((math.random * 1000).toInt)
  }
}
