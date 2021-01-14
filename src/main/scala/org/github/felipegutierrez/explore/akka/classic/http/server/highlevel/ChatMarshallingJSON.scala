package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.pattern.ask
import akka.util.Timeout
import spray.json._
// step 1 - import spray json
import scala.concurrent.Future
import scala.concurrent.duration._

// case class Player(nickname: String, characterClass: String, level: Int)
case class Chat(sender:String,receiver:String,message:String, groupChatName:String)
case object GetAllUsersInChat
case class AddUsers(player: Chat)

// step 2 - the JSON protocol
trait ChatJsonProtocol extends DefaultJsonProtocol {
  implicit val chatFormat = jsonFormat4(Chat)
}

class ChatActor extends Actor with ActorLogging {
  var users = Map[String, Chat]()
  override def receive: Receive = {
    case GetAllUsersInChat =>
      log.info(s"getting all users")
      sender() ! users.values.toList
    case AddUsers(user) =>
      log.info(s"trying to add user $user")
      users = users + (user.sender -> user)
    case _ => log.info(s"unknown message")
  }
}

// step 3 - extend ChatJsonProtocol
// step 4 - add sprayJsonSupport
object ChatMarshallingJSON extends ChatJsonProtocol with SprayJsonSupport {
  implicit val system = ActorSystem("ChatMarshallingJSON")
  val chatActor = system.actorOf(Props[ChatActor], "chatActor")
  // boot strap some users
  val chatusers = List(
    Chat("sender1", "receiver1", "message1", "groupChat1"),
    Chat("sender2", "receiver2", "message2", "groupChat2"),
    Chat("sender3", "receiver3", "message3", "groupChat3")
  )
  chatusers.foreach { user =>
    chatActor ! AddUsers(user)
  }

  implicit val defaultTimeout = Timeout(2 seconds)

  val chatRoutes = {
      path("api" / "chat") {
        get {
            // 3: get all users in the chat
            val allUsersInChatFuture: Future[List[Chat]] = (chatActor ? GetAllUsersInChat).mapTo[List[Chat]]
            complete(allUsersInChatFuture)
        }
      }
  }

  def main(args: Array[String]): Unit = {
    println("http GET localhost:8080/api/chat")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(chatRoutes)
  }
}
