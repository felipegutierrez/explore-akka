package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class PIControllerMonitorSignals extends AbstractLoggingActor {

    private final Map<Integer, ActorRef> operators = new HashMap<Integer, ActorRef>();
    private int newParameter = 0;

    public static Props props() {
        return Props.create(PIControllerMonitorSignals.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageGlobalSignal.class, this::computeMessageGlobalSignal)
                .match(MessageTrigger.class, this::triggerNewParameter)
                .build();
    }

    private void computeMessageGlobalSignal(MessageGlobalSignal message) {
        System.out.println("computing message: " + message + " from " + getSender());
        computeGlobalSignal(message);
        if (!operators.containsKey(getSender().path().uid())) {
            operators.put(getSender().path().uid(), getSender());
        }
    }

    private void computeGlobalSignal(MessageGlobalSignal message) {
        if (message.outPollAvg < 50 || message.outPollAvg > 80) {
            if (message.outPollAvg < 50) {
                // NO BACK PRESSURE
                newParameter = newParameter - 5;
            } else {
                // BACK PRESSURE
                newParameter = newParameter + 5;
            }
        } else {
            // within the range
        }
    }

    private void triggerNewParameter(MessageTrigger trigger) {
        for (Map.Entry<Integer, ActorRef> entry : operators.entrySet()) {
            ActorRef operatorActor = entry.getValue();
            System.out.println("trigger new parameter to actor: " + operatorActor);
            operatorActor.tell(new MessageParameter(newParameter), getSelf());
        }
    }
}
