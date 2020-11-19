package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;

public class JobManagerApp {

    public static void main(String[] args) {
        // 1 - configure the Actor System
        Config config = ConfigFactory
                .parseString("akka.cluster.roles = [" + Utils.ROLE_CONTROLLER + "]" +
                        ",akka.remote.artery.canonical.port = 2551")
                .withFallback(ConfigFactory.load("clustering/controller.conf"));
        final ActorSystem system = ActorSystem.create("JobManagerActorSystem", config);

        // 2 - start the PI Controller actor
        ActorRef controllerActor = system.actorOf(PIControllerActor.props(), "controllerActor");

        // 3 - schedule the PI Controller to send parameters in a fixed rate
        Cancellable cancellable = system.scheduler().scheduleWithFixedDelay(
                Duration.ofSeconds(2),
                Duration.ofSeconds(20),
                controllerActor,
                new MessageControllerTrigger(),
                system.dispatcher(),
                controllerActor
        );
    }
}
