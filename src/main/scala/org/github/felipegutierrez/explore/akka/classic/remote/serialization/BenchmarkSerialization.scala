package org.github.felipegutierrez.explore.akka.classic.remote.serialization

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.serialization.Serializer
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.Datamodel.ProtobufVote

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.UUID
import scala.util.Random

/**
 * at the application.conf under the scope of "serializersBenchmark" change:
 * serialization-bindings
 * "org.github.felipegutierrez.explore.akka.classic.remote.serialization.Vote" = java // set up [java,avro,kryo]
 * - JAVA: Received 1000000 votes in 32.550 seconds
 * - AVRO: Received 1000000 votes in 111.420 seconds
 * - KRYO: Received 1000000 votes in 38.697 seconds
 * - PROTOBUFFER: Received 1000000 votes in 15.583 seconds
 *
 *
 *
 */

case class Vote(ssn: String, candidate: String)

case object VoteEnd

object VoteGenerator {
  val random = new Random()
  val candidates = List("alice", "bob", "charlie", "deedee", "felipe", "fabio", "simone", "mike", "dude")

  def generateVotes(count: Int) = (1 to count).map(_ => Vote(UUID.randomUUID().toString, getRandomCandidate))

  def getRandomCandidate = candidates(random.nextInt(candidates.length))

  def generateProtobufVotes(count: Int) = (1 to count).map { _ =>
    ProtobufVote.newBuilder()
      .setSsn(UUID.randomUUID().toString)
      .setCandidate(getRandomCandidate)
      .build()
  }
}

class VoteAggregator extends Actor with ActorLogging {
  override def receive: Receive = ready

  def ready: Receive = {
    case _: Vote | _: ProtobufVote => context.become(online(1, System.currentTimeMillis()))
  }

  def online(voteCount: Int, originalTime: Long): Receive = {
    case _: Vote | _: ProtobufVote =>
      context.become(online(voteCount + 1, originalTime))
    case VoteEnd =>
      val duration = (System.currentTimeMillis() - originalTime) * 1.0 / 1000
      log.info(f"Received $voteCount votes in $duration%5.3f seconds")
      context.become(ready)
  }
}

object TestVoteAvroSerializer {
  def main(args: Array[String]): Unit = {
    println(AvroSchema[Vote])
  }
}

class VoteAvroSerializer extends Serializer {

  val voteSchema = AvroSchema[Vote]

  override def identifier: Int = 98745

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case vote: Vote =>
      val baos = new ByteArrayOutputStream()
      val avroOutputStream = AvroOutputStream.binary[Vote].to(baos).build()
      avroOutputStream.write(vote)
      avroOutputStream.flush()
      avroOutputStream.close()
      baos.toByteArray
    case _ => throw new IllegalArgumentException("We only support votes in this benchmark serializer")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val inputStream = AvroInputStream.binary[Vote].from(new ByteArrayInputStream(bytes)).build(voteSchema)
    val voteIterator: Iterator[Vote] = inputStream.iterator
    val vote = voteIterator.next()
    inputStream.close()
    vote
  }

  override def includeManifest: Boolean = true
}

object VotingStation {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("serializersBenchmark"))

    val system = ActorSystem("VotingStation", config)
    val actorSelection = system.actorSelection("akka://VotingCentralizer@localhost:2552/user/voteAggregator")

    // val votes = VoteGenerator.generateProtobufVotes(1000000) // using protobuffers
    val votes = VoteGenerator.generateVotes(1000000) // using java, avro, kryo
    votes.foreach(actorSelection ! _)
    actorSelection ! VoteEnd
  }
}

object VotingCentralizer {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("serializersBenchmark"))

    val system = ActorSystem("VotingCentralizer", config)
    val votingAggregator = system.actorOf(Props[VoteAggregator], "voteAggregator")
  }
}
