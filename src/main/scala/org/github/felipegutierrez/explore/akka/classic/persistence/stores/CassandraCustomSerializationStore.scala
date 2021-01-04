package org.github.felipegutierrez.explore.akka.classic.persistence.stores

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import com.typesafe.config.ConfigFactory

object CassandraCustomSerializationStore {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val cassandraCustomSerializationStoreSystem = ActorSystem("cassandraCustomSerializationStoreSystem", ConfigFactory.load().getConfig("cassandraSerializableStore"))
    val userRegistrationActor = cassandraCustomSerializationStoreSystem.actorOf(Props[UserRegistrationActor], "userRegistrationActor")

    for (i <- 1 to 10) {
      userRegistrationActor ! RegisterUser(s"user_$i@email.com", s"user_$i")
    }
  }

  // ACTOR
  class UserRegistrationActor extends PersistentActor with ActorLogging {
    var currentId = 0

    override def persistenceId: String = "user-registration"

    override def receiveCommand: Receive = {
      case RegisterUser(email, name) =>
        persist(UserRegistered(currentId, email, name)) { e =>
          currentId += 1
          log.info(s"user persisted: $e")
        }
    }

    override def receiveRecover: Receive = {
      case user@UserRegistered(id, email, name) =>
        currentId = user.id
        log.info(s"user recovered: $user")
    }
  }

}
