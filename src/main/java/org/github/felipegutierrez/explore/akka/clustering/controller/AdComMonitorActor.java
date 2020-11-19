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
                .build();
    }
}
