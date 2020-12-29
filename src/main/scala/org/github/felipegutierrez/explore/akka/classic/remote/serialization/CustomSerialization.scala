package org.github.felipegutierrez.explore.akka.classic.remote.serialization

import akka.actor.{ActorSystem, Props}
import akka.serialization.Serializer
import com.typesafe.config.ConfigFactory

import spray.json._

case class Person(name: String, age: Int)

class PersonSerializer extends Serializer {

  val SEPARATOR = "//"

  override def identifier: Int = 74238

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case person @ Person(name, age) =>
      // [John||32]
      println(s"Serializing $person")
      s"[$name$SEPARATOR$age]".getBytes()
    case _ => throw new IllegalArgumentException("only persons are supported for this serializer")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val string = new String(bytes)
    val values = string.substring(1, string.length - 1).split(SEPARATOR)
    val name = values(0)
    val age = values(1).toInt

    val person = Person(name, age)
    println(s"Deserialized $person")
    person
  }

  override def includeManifest: Boolean = false
}

class PersonJsonSerializer extends Serializer with DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)

  override def identifier: Int = 42348

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case p: Person =>
      val json: String = p.toJson.prettyPrint
      println(s"Converting $p to $json")
      json.getBytes()
    case _ => throw new IllegalArgumentException("only persons are supported for this serializer")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val string = new String(bytes)
    val person = string.parseJson.convertTo[Person]
    println(s"Deserialized $string to $person")
    person
  }

  override def includeManifest: Boolean = false
}

object CustomSerialization_Local {
  def main(args: Array[String]): Unit = {
    run()
  }
  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
    """.stripMargin)
      .withFallback(ConfigFactory.load().getConfig("customSerializablePerson"))

    val system = ActorSystem("LocalSystem", config)
    val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

    actorSelection ! Person("Alice", 23)
  }
}

object CustomSerialization_Remote {
  def main(args: Array[String]): Unit = {
    run()
  }
  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
    """.stripMargin)
      .withFallback(ConfigFactory.load().getConfig("customSerializablePerson"))

    val system = ActorSystem("RemoteSystem", config)
    val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
  }
}
