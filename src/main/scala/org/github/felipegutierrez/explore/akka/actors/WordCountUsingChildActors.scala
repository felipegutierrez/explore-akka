package org.github.felipegutierrez.explore.akka.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object WordCountUsingChildActors extends App {
  run()

  def run() = {
    println("Distributed word counting using 1 master actor and 3 child actors")
    val system = ActorSystem("WordCountUsingChildActors")
    val client = system.actorOf(Props[ClientActor], "ClientActor")
    client ! "go"
  }

  class ClientActor extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val texts = List("testing word count with Akka and Scala and using the distributed pattern",
          "I love Akka because it is distributed and thread safe", "Make sure to not call methods but only send messages",
          "now should be the time to come back to worker 0 because we are using round-robin strategy",
          "do you confirm that it worker ?")
        texts.foreach(text => master ! text)
      case count: Int => println(s"[test actor] i received a reply: $count")
    }
  }

  object WordCounterMaster {

    case class Initialize(nChildren: Int)

    case class WordCountTask(id: Int, text: String)

    case class WordCountReply(id: Int, count: Int)

  }

  class WordCounterMaster extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(nWorkers) => {
        println(s"initializing...")
        val workerSeq: Seq[ActorRef] = for (w <- 1 to nWorkers) yield context.actorOf(Props[WordCounterWorker], s"worker_$w")
        context.become(withWorkers(workerSeq, 0, 0, Map()))
      }
    }

    def withWorkers(workers: Seq[ActorRef], currentWorkIndex: Int, currentTaskId: Int, requestMap: Map[Int, ActorRef]): Receive = {
      // the master receive a text
      case text: String =>
        println(s"[master] I have received: $text - I will send to worker $currentWorkIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        // gets a worker from the Sequence stateful parameter of the method
        val workerRef = workers(currentWorkIndex)
        // sends the task to the worker reference
        workerRef ! task
        // get the new index of a new worker for the next task
        val nextWorkerIndex = (currentWorkIndex + 1) % workers.length
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)
        // assign the new worker with a new ID index using context.become
        context.become(withWorkers(workers, nextWorkerIndex, newTaskId, newRequestMap))
      case WordCountReply(id, count) =>
        println(s"[master] I have received a reply for task id $id with $count words")
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(withWorkers(workers, currentWorkIndex, currentTaskId, requestMap - id))
    }
  }

  class WordCounterWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id, text) => {
        println(s"${self.path} I have received tasks $id with $text")
        sender() ! WordCountReply(id, text.split(" ").length)
      }
    }
  }

}
