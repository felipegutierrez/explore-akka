package org.github.felipegutierrez.explore.akka.classic.remote.controller;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class PIController {

    public PIController() {
        final ActorSystem remoteSystem = ActorSystem.create("JobManagerActorSystem",
                ConfigFactory.load("remote/controller.conf").getConfig("controllerApp"));
    }

    // public static void main(String[] args) {
    public static void run(String[] args) {
        new PIController();
    }
}
