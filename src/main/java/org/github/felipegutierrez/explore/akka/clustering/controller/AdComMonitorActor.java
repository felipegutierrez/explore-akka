package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class AdComMonitorActor extends AbstractLoggingActor {
    public static Props props() {
        return Props.create(AdComMonitorActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageAdComParameter.class, this::receiveAdComParameter)
                .build();
    }

    private void receiveAdComParameter(MessageAdComParameter message) {
        log().info("received AdCom parameter: {}", message);
    }
}
