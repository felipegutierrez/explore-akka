package org.github.felipegutierrez.explore.akka.classic.persistence.event_sourcing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class SafeSenderApp {
    // public static void main(String[] args) {
    public static void run(String[] args) {
        final ActorSystem system = ActorSystem.create("PersistentSafeSender", ConfigFactory.load());
        ActorRef safeSenderActor = system.actorOf(SafeSenderActor.props(), "safeSenderActor");
    }
}
