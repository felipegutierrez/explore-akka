package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.duration._

object WebSocketDemo {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("WebSocketDemo")

    val textMessage = TextMessage(Source.single("this is a text message"))
    val binaryMessage = TextMessage(Source.single("this is a binary message"))

    val html =
      """
        |<html>
        |    <head>
        |        <script>
        |            var exampleSocket = new WebSocket("ws://localhost:8080/greeter");
        |            console.log("starting websocket...");
        |
        |            exampleSocket.onmessage = function(event) {
        |                var newChild = document.createElement("div");
        |                newChild.innerText = event.data;
        |                document.getElementById("1").appendChild(newChild);
        |            };
        |
        |            exampleSocket.onopen = function(event) {
        |                exampleSocket.send("socket seems to be open...");
        |            };
        |
        |            exampleSocket.send("socket says: hello, server!");
        |        </script>
        |    </head>
        |
        |    <body>
        |        Starting websocket...
        |        <div id="1">
        |        </div>
        |    </body>
        |
        |</html>
    """.stripMargin

    // plain text web socket flow
    def webSocketFlow: Flow[Message, Message, Any] = Flow[Message].map {
      case tm: TextMessage =>
        TextMessage(Source.single("Server says back:") ++ tm.textStream ++ Source.single("!"))
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        TextMessage(Source.single("Server received a binary message..."))
    }

    // dynamic web socket flow to update html without refreshing the page
    case class SocialPost(owner: String, content: String)
    val socialFeed = Source(
      List(
        SocialPost("Martin", " Scala 3 has been announced"),
        SocialPost("Daniel", "A new Rock the JVM course is open"),
        SocialPost("Martin", "I killed Java"),
        SocialPost("Felipe", "I have found a job to work with Scala =)")
      )
    )
    val socialMessages = socialFeed
      .throttle(1, 5 seconds).initialDelay(2 seconds)
      .map(socialPost => TextMessage(s"${socialPost.owner} said: ${socialPost.content}"))
    val socialFlow: Flow[Message, Message, Any] = Flow
      .fromSinkAndSource(Sink.foreach[Message](println), socialMessages)

    val webSocketRoute =
      (pathEndOrSingleSlash & get) {
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            html
          )
        )
      } ~ path("greeter") {
        // handleWebSocketMessages(webSocketFlow)
        handleWebSocketMessages(socialFlow)
      }

    println("access the browser at: localhost:8080")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(webSocketRoute)
  }
}
