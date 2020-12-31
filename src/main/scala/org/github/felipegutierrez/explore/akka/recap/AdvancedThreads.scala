package org.github.felipegutierrez.explore.akka.recap

import java.time.LocalDateTime
import java.util.concurrent.{Executors, TimeUnit}

import scala.collection.mutable
import scala.util.Random

object AdvancedThreads {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val myThread = new AdvancedThreads()
    myThread.usingThePool()
    println
    myThread.inceptionThreads(50).start()
    // println
    // myThread.naiveProdCons()
    // println
    // myThread.smartProdCons()
    // println
    // myThread.prodConsLargeBuffer()
    println
    myThread.multiProdCons(3, 6)
  }

  class AdvancedThreads {
    val pool = Executors.newFixedThreadPool(10)

    def usingThePool() = {
      pool.execute(() => println(s"something in the pool: ${LocalDateTime.now()}"))
      pool.execute(() => {
        Thread.sleep(2000)
        println(s"done after 2 second: ${LocalDateTime.now()}")
      })
      pool.execute(() => {
        Thread.sleep(2000)
        println(s"almost done: ${LocalDateTime.now()}")
        Thread.sleep(2000)
        println(s"done after 4 second: ${LocalDateTime.now()}")
      })
      pool.awaitTermination(4500, TimeUnit.MILLISECONDS)
      Thread.sleep(5000)
      println(s"pool.isTerminated: ${pool.isTerminated}")
    }

    def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })

    class SimpleContainer {
      private var value: Int = 0

      def isEmpty: Boolean = value == 0

      def set(newValue: Int) = value = newValue

      def get = {
        val result = value
        value = 0
        result
      }
    }

    def naiveProdCons(): Unit = {
      val container = new SimpleContainer

      val consumer = new Thread(() => {
        println("[consumer] waiting...")
        while (container.isEmpty) {
          println("[consumer] actively waiting...")
        }

        println("[consumer] I have consumed " + container.get)
      })

      val producer = new Thread(() => {
        println("[producer] computing...")
        Thread.sleep(500)
        val value = 42
        println("[producer] I have produced, after long work, the value " + value)
        container.set(value)
      })

      consumer.start()
      producer.start()
    }

    // wait and notify
    def smartProdCons(): Unit = {
      val container = new SimpleContainer

      val consumer = new Thread(() => {
        println("[consumer] waiting...")
        container.synchronized {
          container.wait()
        }

        // container must have some value
        println("[consumer] I have consumed " + container.get)
      })

      val producer = new Thread(() => {
        println("[producer] Hard at work...")
        Thread.sleep(2000)
        val value = 42

        container.synchronized {
          println("[producer] I'm producing " + value)
          container.set(value)
          container.notify()
        }
      })

      consumer.start()
      producer.start()
    }

    def prodConsLargeBuffer(): Unit = {
      val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
      val capacity = 3

      val consumer = new Thread(() => {
        val random = new Random()

        while (true) {
          buffer.synchronized {
            if (buffer.isEmpty) {
              println("[consumer] buffer empty, waiting...")
              buffer.wait()
            }

            // there must be at least ONE value in the buffer
            val x = buffer.dequeue()
            println("[consumer] consumed " + x)

            // hey producer, there's empty space available, are you lazy?!
            buffer.notify()
          }

          Thread.sleep(random.nextInt(250))
        }
      })

      val producer = new Thread(() => {
        val random = new Random()
        var i = 0

        while (true) {
          buffer.synchronized {
            if (buffer.size == capacity) {
              println("[producer] buffer is full, waiting...")
              buffer.wait()
            }

            // there must be at least ONE EMPTY SPACE in the buffer
            println("[producer] producing " + i)
            buffer.enqueue(i)

            // hey consumer, new food for you!
            buffer.notify()

            i += 1
          }

          Thread.sleep(random.nextInt(500))
        }
      })

      consumer.start()
      producer.start()
    }

    def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
      val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
      val capacity = 20

      (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
      (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
    }
  }

  /**
   * Consumer with a fixed buffer
   *
   * @param id
   * @param buffer
   */
  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          /*
            producer produces value, two Cons are waiting
            notifies ONE consumer, notifies on buffer
            notifies the other consumer
           */
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue() // OOps.!
          println(s"[consumer $id] consumed " + x)

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(250))
      }
    }
  }

  /**
   * Producer with a fixed buffer
   *
   * @param id
   * @param buffer
   * @param capacity
   */
  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notifyAll()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

}
