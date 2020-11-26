package org.github.felipegutierrez.explore.akka.classic.clustering.singleton

import akka.actor.{ActorSystem, Cancellable}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

object PaymentSystemClient extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 0
    """.stripMargin)
    .withFallback(ConfigFactory.load("clustering/clusteringSingleton.conf"))
  val system = ActorSystem("RTJVMCluster", config)

  val proxy = system.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/paymentMasterActor",
      settings = ClusterSingletonProxySettings(system)
    ),
    "paymentSystemProxy"
  )

  val onlineShopCheckout = system.actorOf(OnlineShopCheckout.props(proxy))

  import system.dispatcher

  val routine: Cancellable = system.scheduler.scheduleWithFixedDelay(5 second, 2 seconds) {
    new Runnable {
      def run(): Unit = {
        val randomOrder = Order(List(), Random.nextDouble() * 100)
        onlineShopCheckout ! randomOrder
      }
    }
  }
}
