package org.github.felipegutierrez.explore.akka.clustering.chat

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.util.Timeout

import scala.concurrent.duration._

object ChatDomain {

  case class ChatMessage(nickname: String, contents: String)

  case class UserMessage(contents: String)

  case class EnterRoom(fullAddress: String, nickname: String)

}

object ChatActor {
  def props(nickname: String, port: Int) = Props(new ChatActor(nickname, port))
}

class ChatActor(nickname: String, port: Int) extends Actor with ActorLogging {

  import ChatDomain._

  implicit val timeout = Timeout(3 seconds)

  // 1: initialize the cluster object
  val cluster = Cluster(context.system)

  // 2: subscribe to cluster event in preStart
  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent]
    )
  }

  // 3: unsubscribe self in postStop
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = online(Map())

  /** chatRoom is the data structure to the users in the chat */
  def online(chatRoom: Map[String, String]): Receive = {
    case MemberUp(member) =>
      // 4: send a special EnterRoom message to the chatActor deployed on a new node (hint: use Actor selection)
      log.info(s"Member deployed on a new node: ${member.address}")
      val remoteActorSelection: ActorSelection = context.actorSelection(s"${member.address}/user/chatActor")
      remoteActorSelection ! EnterRoom(s"${self.path.address}@localhost:$port", nickname)
    case MemberRemoved(member, previousStatus) =>
      // 5: remove the member from your data structure
      val remoteNickname = chatRoom(member.address.toString)
      log.info(s"user ${remoteNickname} left the room after $previousStatus")
      context.become(online(chatRoom - member.address.toString))
    case EnterRoom(remoteAddress, remoteNickname) => {
      // 6: add the member to your data structure
      if (remoteNickname != nickname) {
        log.info(s"$remoteNickname entered the room")
      }
      context.become(online(chatRoom + (remoteAddress -> remoteNickname)))
    }
    case UserMessage(content) =>
      // 7: broadcast the content (as ChatMessage) to the rest of the cluster members
      chatRoom.keys.foreach { address =>
        val chatActorSelection: ActorSelection = context.actorSelection(s"${address}/user/chatActor")
        chatActorSelection ! ChatMessage(nickname, content)
      }
    case ChatMessage(remoteNickname, contents) =>
      log.info(s"[$remoteNickname] said: $contents")
  }
}
