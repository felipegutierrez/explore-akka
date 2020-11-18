package org.github.felipegutierrez.explore.akka.clustering.chat

object ClusteringChat extends App {

  object ChatDomain {
    case class ChatMessage(nickname: String, contents: String)
    case class UserMessage(contents: String)
    case class EnterRoom(fullAddress: String, nickname: String)
  }

  run()

  def run() = {
    object Alice extends ChatApp("Alice", 2551)
    object Bob extends ChatApp("Bob", 2552)
    object Felipe extends ChatApp("Felipe", 2553)
  }

  // clusteringChat.conf
}
