package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

public class AdComMonitorActor extends AbstractLoggingActor {

    private final int id;

    public AdComMonitorActor(int id) {
        this.id = id;
    }

    public static Props props(int id) {
        return Props.create(AdComMonitorActor.class, id);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageAdcomSignals.class, this::receiveAdcomSignalsTrigger)
                .match(MessageAdComParameter.class, this::receiveAdComParameter)
                .build();
    }

    private void receiveAdcomSignalsTrigger(MessageAdcomSignals message) {
        log().info("received AdCom-{} signals trigger: {} from my schedule task: {}. Let's send it to the global PI Controller.", this.id, message, getSender());
        // select the PI Controller remote actor
        ActorSelection controllerActor = getContext().actorSelection("akka://" + Utils.ACTOR_SYSTEM + "@" + Utils.ACTOR_CONTROLLER_HOST + ":" + Utils.ACTOR_CONTROLLER_PORT + "/user/" + Utils.ACTOR_CONTROLLER);
        // send signals to the PI Controller
        controllerActor.tell(message, getSelf());
    }

    private void receiveAdComParameter(MessageAdComParameter message) {
        log().info("received AdCom-{} parameter: {} from: {}", this.id, message, getSender());
    }
}
