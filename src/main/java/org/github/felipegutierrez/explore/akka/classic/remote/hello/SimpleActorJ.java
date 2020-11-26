package org.github.felipegutierrez.explore.akka.classic.remote.hello;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class SimpleActorJ extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(SimpleActorJ.class);
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(SimpleMessage.class, this::onMessage)
                .match(String.class, this::onString)
                .build();
    }

    private void onString(String message) {
        System.out.println("Received [" + message + "] from " + sender());
    }

    private void onMessage(SimpleMessage simpleMessage) {
        System.out.println("Received [" + simpleMessage.msg + "] from " + sender());
    }
}
