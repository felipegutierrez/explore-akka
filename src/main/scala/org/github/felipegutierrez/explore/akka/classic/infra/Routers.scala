package org.github.felipegutierrez.explore.akka.classic.infra

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.routing._
import com.typesafe.config.ConfigFactory

object Routers extends App {

  run()

  def run() = {
    val system = ActorSystem("routersDemo", ConfigFactory.load().getConfig("routersDemo"))

    val manualRoundRobinMaster = system.actorOf(Props(classOf[ManualMaster], 5, RoundRobinRoutingLogic()), "manualRoundRobinMaster")
    for (i <- 1 to 10) {
      manualRoundRobinMaster ! s"[$i] uniques hello!"
    }

    val manualBroadcastMaster = system.actorOf(Props(classOf[ManualMaster], 3, BroadcastRoutingLogic()), "manualBroadcastMaster")
    for (i <- 1 to 5) {
      manualBroadcastMaster ! s"[$i] broadcast hello to the world!"
    }

    val programmaticallyRoundRobinMaster = system.actorOf(RoundRobinPool(5).props(Props[Worker]), "simplePoolMaster")
    for (i <- 1 to 10) {
      programmaticallyRoundRobinMaster ! s"[$i] hello from a programmatically round-robin router Actor!"
    }

    // poolMaster2 matches with application.conf
    val poolMaster2 = system.actorOf(FromConfig.props(Props[Worker]), "poolMaster2")
    for (i <- 1 to 10) {
      poolMaster2 ! s"[$i] round-robin hello from application.conf!"
    }

    // Group router: router with actors created elsewhere
    val workerList: List[ActorRef] = (1 to 5).map(i => system.actorOf(Props[Worker], s"worker_$i")).toList
    val workersPath: List[String] = workerList.map(workerRef => workerRef.path.toString)
    val groupMaster: ActorRef = system.actorOf(RoundRobinGroup(workersPath).props())
    for (i <- 1 to 10) {
      groupMaster ! s"[$i] round-robin hello from a GROUP of Actors created in another system!"
    }

    // groupMaster2 matches with application.conf
    val groupMaster2 = system.actorOf(FromConfig.props(), "groupMaster2")
    for (i <- 1 to 10) {
      groupMaster2 ! s"[$i] Hello from the world"
    }

    groupMaster2 ! Broadcast("broadcast hello to everyone")

  }

  class ManualMaster(numberOfWorkers: Int, routingLogic: RoutingLogic) extends Actor with ActorLogging {
    // step 1 -  creating 5 actors routees based on Worker actors
    private val workers = for (i <- 1 to numberOfWorkers) yield {
      val worker = context.actorOf(Props[Worker], s"worker_$i")
      context.watch(worker)
      // destination for messages routed via a [[Router]]
      ActorRefRoutee(worker)
    }
    // step 2 - define a router Logic
    private var router = Router(routingLogic, workers)

    override def receive: Receive = {
      // define a handler to route the messages
      case Terminated(ref) =>
        // step 4 - handle the termination/lifecycle of the routees
        router.removeRoutee(ref)
        val newWorker = context.actorOf(Props[Worker])
        context.watch(newWorker)
        router.addRoutee(newWorker)
      case message =>
        // step 3 - route the message without involving the Master, but the sender()
        router.route(message, sender())
    }
  }

  class Worker extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

}
