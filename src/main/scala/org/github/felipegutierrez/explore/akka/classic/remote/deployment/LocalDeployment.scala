package org.github.felipegutierrez.explore.akka.classic.remote.deployment

import akka.actor.{ActorSystem, Address, AddressFromURIString, Deploy, PoisonPill, Props}
import akka.remote.RemoteScope
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.remote.hello.SimpleActor

object LocalDeployment {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val system = ActorSystem("LocalActorSystem", ConfigFactory.load("remote/deployingActorsRemotely.conf").getConfig("localApp"))

    /** define the actor path /user/remoteActor and on the remote/deployingActorsRemotely.conf#remoteApp */
    val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor") // /user/remoteActor
    simpleActor ! "hello, remote actor!"

    /**
     * Prints the local reference of the remote actor:
     * Actor[akka://RemoteActorSystem@localhost:2552/remote/akka/LocalActorSystem@localhost:2551/user/remoteActor#-74151124]
     */
    println(simpleActor)

    /** Remote deployment programmatically */
    val remoteSystemAddress: Address = AddressFromURIString("akka://RemoteActorSystem@localhost:2552")
    val remotelyDeployedActor = system.actorOf(Props[SimpleActor].withDeploy(Deploy(scope = RemoteScope(remoteSystemAddress))))
    remotelyDeployedActor ! "Hi remotely deployed actor programmatically!"

    /** Routers with routees deployed remotely */
    val poolRouter = system.actorOf(FromConfig.props(Props[SimpleActor]), "myRouterWithRemoteChildren")
    (1 to 10).map(i => s"message $i").foreach(msg => poolRouter ! msg)

    /** creating remotely deployed actor that watcher for a child termination */
    val parentActor = system.actorOf(Props[ParentActor], "watcher")
    parentActor ! "create"
    Thread.sleep(1000)
    system.actorSelection("akka://RemoteActorSystem@localhost:2552/remote/akka/LocalActorSystem@localhost:2551/user/watcher/remoteChild") ! PoisonPill

  }
}
