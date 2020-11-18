package org.github.felipegutierrez.explore.akka.clustering.chat

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}

class ChatActor extends Actor with ActorLogging {

  import ClusteringChat.ChatDomain._
  // TODO 1: initialize the cluster object

  // TODO 2: subscribe to cluster event in preStart

  // TODO 3: unsubscribe self in postStop


  override def receive: Receive = {
    case MemberUp(member) =>
    // TODO 4: send a special EnterRoom message to the chatActor deployed on a new node (hint: use Actor selection)
    case MemberRemoved(member, previousStatus) =>
    // TODO 5: remove the member from your data structure
    case EnterRoom(remoteAddress, remoteNickname) =>
    // TODO 6: add the member to your data structure
    case UserMessage(content) =>
    // TODO 7: broadcast the content (as ChatMessage) to the rest of the cluster members
    case ChatMessage(remoteNickname, contents) =>
      log.info(s"[$remoteNickname] said: $contents")
  }
}
