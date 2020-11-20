package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

public class AdComMonitorActor extends AbstractLoggingActor {
    public static Props props() {
        return Props.create(AdComMonitorActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageAdcomSignals.class, this::receiveAdcomSignalsTrigger)
                .match(MessageAdComParameter.class, this::receiveAdComParameter)
                .build();
    }

    private void receiveAdcomSignalsTrigger(MessageAdcomSignals message) {
        log().info("received AdCom signals trigger: {}. Let's send it to the global PI Controller.", message);
        // select the PI Controller remote actor
        ActorSelection controllerActor = getContext().actorSelection("akka://JobManagerActorSystem@localhost:2551/user/controllerActor");
        // send signals to the PI Controller
        controllerActor.tell(message, getSelf());
    }

    private void receiveAdComParameter(MessageAdComParameter message) {
        log().info("received AdCom parameter: {}", message);
    }
}
