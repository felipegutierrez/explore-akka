package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class AdComMonitorSignals extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(AdComMonitorSignals.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::onString)
                .build();
    }

    private void onString(String message) {
        System.out.println("Received [" + message + "] from " + sender());
    }

}
