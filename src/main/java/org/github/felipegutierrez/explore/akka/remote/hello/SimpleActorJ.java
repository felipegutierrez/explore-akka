package org.github.felipegutierrez.explore.akka.remote.hello;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class SimpleActorJ extends AbstractLoggingActor {

    static Props props() {
        return Props.create(SimpleActorJ.class);
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(SimpleMessage.class, this::onMessage).build();
    }

    private void onMessage(SimpleMessage simpleMessage) {
        System.out.println("Received [" + simpleMessage.msg + "] from " + sender());
    }
}
