package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class PIControllerMonitorSignals extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(PIControllerMonitorSignals.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // .match(MessageCreateGlobalMonitorSignals.class, this::createGlobalMonitorSignals)
                .build();
    }

    //    private void createGlobalMonitorSignals(MessageCreateGlobalMonitorSignals message) {
    //        log().info("received create message: " + message);
    //    }
}
