package org.github.felipegutierrez.explore.akka.classic.remote.serialization

import akka.actor.{ActorSystem, Props}
import akka.serialization.Serializer
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import com.typesafe.config.ConfigFactory

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

case class BankAccount(iban: String, bankCode: String, amount: Double, currency: String)

case class CompanyRegistry(name: String, accounts: Seq[BankAccount], activityCode: String, marketCap: Double)

object AvroSchemaTest {
  def main(args: Array[String]): Unit = {
    println(s"BankAccount avro schema: ${AvroSchema[BankAccount]}")
    println(s"CompanyRegistry avro schema: ${AvroSchema[CompanyRegistry]}")
  }
}

class MyFirstAvroSerializer extends Serializer {
  val schema = AvroSchema[CompanyRegistry]

  override def identifier: Int = 454874

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case c: CompanyRegistry =>
      val baos = new ByteArrayOutputStream()
      val avroOutputStream = AvroOutputStream.binary[CompanyRegistry].to(baos).build() // schema
      avroOutputStream.write(c)
      avroOutputStream.flush()
      avroOutputStream.close()
      baos.toByteArray
    case _ => throw new IllegalArgumentException(s"we only support CompanyRegistry for Avro")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val avroInputStream = AvroInputStream.binary[CompanyRegistry].from(new ByteArrayInputStream(bytes)).build(schema)
    val companyRegistryIterator: Iterator[CompanyRegistry] = avroInputStream.iterator
    val companyRegistry = companyRegistryIterator.next()
    avroInputStream.close()
    companyRegistry
  }

  override def includeManifest: Boolean = false
}

object AvroSerialization_Local {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("avroSerializablePerson"))

    val system = ActorSystem("LocalSystem", config)
    val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

    actorSelection ! CompanyRegistry(
      "Google",
      Seq(
        BankAccount("US-1234", "google-bank", 4.3, "gazillion dollars"),
        BankAccount("GB-4321", "google-bank", 0.5, "trillion pounds")
      ),
      "ads",
      523895
    )
  }
}

object AvroSerialization_Remote {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("avroSerializablePerson"))

    val system = ActorSystem("RemoteSystem", config)
    val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
  }
}
