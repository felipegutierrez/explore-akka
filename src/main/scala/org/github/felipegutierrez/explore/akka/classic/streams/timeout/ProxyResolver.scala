package org.github.felipegutierrez.explore.akka.classic.streams.timeout

import akka.NotUsed
import akka.actor.ActorSystem
import akka.io.dns.DnsProtocol
import akka.io.{Dns, IO}
import akka.pattern.ask
import akka.stream.scaladsl.{Source => strmSource}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.{Source => fileSource}

object ProxyResolver {
//  def main(args: Array[String]): Unit = {
//    run()
//  }
  def run() = {
    val configString =
      """
        | akka.io.dns {
        |  resolver = "async-dns"
        |  async-dns {
        |    nameservers = ["8.8.8.8"]
        |    resolve-ipv4 = true
        |    resolve-ipv6 = false
        |    resolve-timeout = 1s
        |  }
        |}
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("ProxyResolver", ConfigFactory.load(config))
    implicit val timeout = Timeout(3 second)

    val data = fileSource.fromResource("data/input50.txt").getLines()
    val source: strmSource[String, NotUsed] = strmSource.fromIterator(() => data)
    source
      .mapAsync(1) { domain =>
        val resolved: Future[DnsProtocol.Resolved] = (IO(Dns) ? DnsProtocol.Resolve(domain)).mapTo[DnsProtocol.Resolved]
        resolved
      }
      .runForeach(println)
      .failed
      .foreach(println)
  }
}
