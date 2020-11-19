package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class JobManagerApp {

    public static void main(String[] args) {
        // 1 - configure the Actor System
        Config config = ConfigFactory
                .parseString("akka.remote.artery.canonical.port = 2551")
                .withFallback(ConfigFactory.load("clustering/controller.conf"));
        final ActorSystem system = ActorSystem.create("JobManagerActorSystem", config);

        // 2 - start the PI Controller actor
        ActorRef controllerActor = system.actorOf(PIControllerActor.props(), "controllerActor");
    }
}
