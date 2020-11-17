package org.github.felipegutierrez.explore.akka.remote.wordcount

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSystem, Identify, PoisonPill, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

object WordCountDomain {

  case class Initialize(nWorkers: Int)

  case class WordCountTask(text: String)

  case class WordCountResult(count: Int)

  case object EndWordCount

}

class WordCountWorker extends Actor with ActorLogging {

  import WordCountDomain._

  override def receive: Receive = {
    case ActorIdentity(i, Some(actorRef)) =>
      log.info(s"received ActorIdentity message: $i")
      actorRef ! s"thank you for identifying yourself work actor $i :-)"
    case WordCountTask(text) =>
      log.info(s"I am processing: $text")
      sender() ! WordCountResult(text.split(" ").length)
  }
}

class WordCountMaster extends Actor with ActorLogging {

  import WordCountDomain._

  val workerRouter = context.actorOf(FromConfig.props(Props[WordCountWorker]), "workerRouter")

  override def receive: Receive = onlineWithRouter(0, 0)

  def onlineWithRouter(remainingTasks: Int, totalCount: Int): Receive = {
    case text: String =>
      // split into sentences
      val sentences = text.split("\\. ")
      // send sentences to worker in turn
      sentences.foreach(sentence => workerRouter ! WordCountTask(sentence))
      context.become(onlineWithRouter(remainingTasks + sentences.length, totalCount))
    case WordCountResult(count) =>
      if (remainingTasks == 1) {
        log.info(s"TOTAL RESULT: ${totalCount + count}")
        context.stop(self)
      } else {
        context.become(onlineWithRouter(remainingTasks - 1, totalCount + count))
      }
  }

  /** @deprecated because we are using routers */
  def receiveOld: Receive = {
    case Initialize(nWorkers) =>

      /** deploying the workers REMOTELY on the worker App using "/wordCountMaster/" at remoteActorsWordCount.conf */
      val workers = (1 to nWorkers).map(id => context.actorOf(Props[WordCountWorker], s"wordCountWorker$id"))
      context.become(online(workers.toList, 0, 0))
  }

  /**
   * @deprecated because we are not using Selection but deploying the workers remotely using the Initialize case.
   */
  def identifyWorkers(nWorkers: Int) = {
    log.info("WordCountMaster initializing...")
    /** identify the workers in the remote JVM */
    // 1 - create actor selections for every worker from 1 to nWorkers
    val remoteWorkerSelections = (1 to nWorkers).map(id => context.system.actorSelection(s"akka://WorkersSystem@localhost:2552/user/wordCountWorker$id"))
    // 2 - send Identify messages to the actor selections
    remoteWorkerSelections.foreach(actorRef => actorRef ! Identify("word_count_identity"))
    // 3 - get into an initialization state, while you are receiving ActorIdentities
    context.become(initializing(List(), nWorkers))
  }

  def initializing(workers: List[ActorRef], remainingWorkers: Int): Receive = {
    case ActorIdentity("word_count_identity", Some(workerRef)) =>
      log.info(s"Worker identified $workerRef")
      if (remainingWorkers == 1) context.become(online(workerRef :: workers, 0, 0))
      else context.become(initializing(workerRef :: workers, remainingWorkers - 1))
  }

  /** @deprecated because we are using routers at onlineWithRouter() */
  def online(workers: List[ActorRef], remainingTasks: Int, totalCount: Int): Receive = {
    case text: String =>
      // split into sentences
      val sentences = text.split("\\. ")
      // send sentences to worker in turn
      Iterator.continually(workers).flatten.zip(sentences.iterator).foreach { pair =>
        val (worker, sentence) = pair
        worker ! WordCountTask(sentence)
      }
      context.become(online(workers, remainingTasks + sentences.length, totalCount))
    case WordCountResult(count) =>
      if (remainingTasks == 1) {
        log.info(s"TOTAL RESULT: ${totalCount + count}")
        workers.foreach(w => w ! PoisonPill)
        context.stop(self)
      } else {
        context.become(online(workers, remainingTasks - 1, totalCount + count))
      }
  }
}

object MasterApp extends App {

  run()

  def run() = {
    import WordCountDomain._
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
      """.stripMargin)
      .withFallback(ConfigFactory.load("remote/remoteActorsWordCount.conf"))
    val system = ActorSystem("MasterSystem", config)
    val master = system.actorOf(Props[WordCountMaster], "wordCountMaster")
    master ! Initialize(5)
    Thread.sleep(1000)

    scala.io.Source.fromFile("src/main/resources/txt/hamlet.txt").getLines().foreach { line =>
      master ! line
    }
  }
}

object WorkerApp extends App {

  run()

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
      """.stripMargin)
      .withFallback(ConfigFactory.load("remote/remoteActorsWordCount.conf"))
    val system = ActorSystem("WorkersSystem", config)
    // deploying the worker REMOTELY and not creating then from the WorkerApp
    // (1 to 5).map(i => system.actorOf(Props[WordCountWorker], s"wordCountWorker$i"))
  }
}
