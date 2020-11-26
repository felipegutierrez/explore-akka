package org.github.felipegutierrez.explore.akka.classic.clustering.singleton

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import com.typesafe.config.ConfigFactory

class PaymentSystemWithSingletonMaster(port: Int, shouldStartSingleton: Boolean = true) extends App {


  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
       |""".stripMargin)
    .withFallback(ConfigFactory.load("clustering/clusteringSingleton.conf"))
  val system = ActorSystem("RTJVMCluster", config)

  if (shouldStartSingleton) {
    val paymentMasterActor = system.actorOf(ClusterSingletonManager.props(
      singletonProps = Props[PaymentSystemActor],
      terminationMessage = PoisonPill,
      ClusterSingletonManagerSettings(system)), "paymentMasterActor")
  }
}

object Node1 extends PaymentSystemWithSingletonMaster(2551)

object Node2 extends PaymentSystemWithSingletonMaster(2552)

object Node3 extends PaymentSystemWithSingletonMaster(2553, false)
