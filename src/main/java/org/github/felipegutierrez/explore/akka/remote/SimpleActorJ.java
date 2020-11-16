package org.github.felipegutierrez.explore.akka.remote;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class SimpleActorJ extends AbstractActor {

    static Props props() {
        return Props.create(SimpleActorJ.class, () -> new SimpleActorJ());
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(Object.class, msg -> {
                    System.out.println("Received [" + msg + "] from " + sender());
                }).build();
    }
}
