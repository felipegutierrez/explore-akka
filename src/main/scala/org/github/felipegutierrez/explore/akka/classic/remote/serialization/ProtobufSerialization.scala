package org.github.felipegutierrez.explore.akka.classic.remote.serialization

import akka.actor.{ActorSystem, Props}
import com.google.protobuf.util.JsonFormat
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.remote.serialization.Datamodel.OnlineStoreUser

object ProtobufSerialization_Local {
  //    def main(args: Array[String]): Unit = {
  //      run()
  //    }

  def run() = {
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("protobufSerializablePerson"))

    val system = ActorSystem("LocalSystem", config)
    val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteActor")

    val onlineStoreUser = OnlineStoreUser.newBuilder()
      .setUserId(1234)
      .setUserName("Felipe-rocktheJVM")
      .setUserEmail("Felipe@rocktheJVM.com")
      .setUserPhone("1234-7890")
      .build()
    println(s"sending message:\n${JsonFormat.printer().print(onlineStoreUser)}")
    actorSelection ! onlineStoreUser
  }
}

object ProtobufSerialization_Remote {
  //    def main(args: Array[String]): Unit = {
  //      run()
  //    }

  def run() = {
    // println("sudo apt install protobuf-compiler")
    // println("cd src")
    // println("protoc --java_out=main/java main/protobuf/datamodel.proto")
    println("sbt protobuf:protobufGenerate")

    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
        |""".stripMargin)
      .withFallback(ConfigFactory.load().getConfig("protobufSerializablePerson"))

    val system = ActorSystem("RemoteSystem", config)
    val simpleActor = system.actorOf(Props[SimpleActor], "remoteActor")
  }
}
