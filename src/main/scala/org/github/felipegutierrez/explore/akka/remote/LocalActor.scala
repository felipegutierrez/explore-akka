package org.github.felipegutierrez.explore.akka.remote

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSystem, Identify, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object LocalActor extends App {

  run()

  def run() = {
    val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("remote/remoteActors.conf"))
    val localSimpleActor = localSystem.actorOf(Props[SimpleActor], "localSimpleActor")
    localSimpleActor ! "hello LOCAL actor"

    /** 1 - send a message to the REMOTE simple actor using actor selection */
    val remoteActorSelection = localSystem.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
    remoteActorSelection ! "Hello from the \"LOCAL\" JVM"

    /** 2 - using actor ref */
    import localSystem.dispatcher
    implicit val timeout = Timeout(3 seconds)
    val remoteActorRefFuture: Future[ActorRef] = remoteActorSelection.resolveOne()
    remoteActorRefFuture.onComplete {
      case Success(actorRef) => actorRef ! s"Hello message from a FUTURE Actor Ref =)"
      case Failure(exception) => println(s"failed because: $exception")
    }

    /** 3 - resolving the actor reference by asking a message identification */
    val remoteActorResolver = localSystem.actorOf(Props[ActorResolver], "remoteActorResolver")

  }

  class ActorResolver extends Actor with ActorLogging {
    override def preStart(): Unit = {
      val selection = context.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActor")
      selection ! Identify(42)
    }

    override def receive: Receive = {
      case ActorIdentity(42, Some(actorRef)) => actorRef ! "thank you for identifying yourself :-)"
    }
  }
}

