package org.github.felipegutierrez.explore.akka.classic.clustering.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;

public class JobManagerApp {
    // public static void main(String[] args) {
    public static void run(String[] args) {
        // Configure the Actor System
        Config config = ConfigFactory.load("clustering/controller.conf")
                .getConfig("jobManagerPIController");
        final ActorSystem system = ActorSystem.create(Utils.ACTOR_SYSTEM, config);

        // Deploy the PI Controller actor
        ActorRef controllerActor = system.actorOf(PIControllerActor.props(), Utils.ACTOR_CONTROLLER);

        // schedule the PI Controller to send parameters in a fixed rate
        Cancellable scheduleAdComGlobalParameter = system.scheduler().scheduleWithFixedDelay(
                Duration.ofSeconds(2),
                Duration.ofSeconds(20),
                controllerActor,
                new MessageControllerTrigger(),
                system.dispatcher(),
                controllerActor
        );
    }
}
