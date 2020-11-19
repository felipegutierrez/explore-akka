package org.github.felipegutierrez.explore.akka.clustering.chat;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;

public class ChatAppJ {
    public ChatAppJ(String nickname, int port, boolean authorize) {
        Config config = ConfigFactory
                .parseString("akka.remote.artery.canonical.port = " + port)
                .withFallback(ConfigFactory.load("clustering/clusteringChat.conf"));
        final ActorSystem system = ActorSystem.create("RTJVMCluster", config);

        ActorRef chatActor = system.actorOf(ChatActorJ.props(nickname, port, authorize), "chatActor");

        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            String input = scan.nextLine();
            System.out.println("Input is: " + input);
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("sending message to leave the chat...");
                chatActor.tell(new MessageQuitJ(), chatActor);
            } else {
                chatActor.tell(new MessageUserJ(input), chatActor);
            }
        }
    }
}
