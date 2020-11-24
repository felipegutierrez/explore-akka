package org.github.felipegutierrez.explore.akka.persistence.event_sourcing;

import akka.actor.ActorLogging;
import akka.actor.Props;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;

import java.util.UUID;

public class SafeSenderActor extends AbstractPersistentActorWithAtLeastOnceDelivery implements ActorLogging {

    public static Props props() {
        return Props.create(SafeSenderActor.class);
    }

    @Override
    public String persistenceId() {
        return "safe-persistent-actor-id-" + UUID.randomUUID();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg -> {
                    persistAsync(msg, this::updateState);
                })
                .build();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder().match(Message.class, this::updateState).build();
    }

    /**
     * this method is called if persisting failed. The actor will be stooped.
     * BEst practice is to start the actor again after a while and use Backoff supervisor.
     */
    @Override
    public void onPersistFailure(Throwable cause, Object event, long seqNr) {
        log().error("fail to persist $event because of: {}", cause);
        super.onPersistFailure(cause, event, seqNr);
    }

    /**
     * Called if the JOURNAl fails to persist the event. The actor is RESUMED.
     */
    @Override
    public void onPersistRejected(Throwable cause, Object event, long seqNr) {
        log().error("persist rejected for {} because of: {}", event, cause);
        super.onPersistRejected(cause, event, seqNr);
    }

    private void updateState(Message event) {
//        if (event instanceof MsgSentEvent) {
//            MsgSentEvent ev = (MsgSentEvent) event;
//            deliver(destination, deliveryId -> new RobustTestMessage(deliveryId, ev.getMessage()));
//        } else if (event instanceof MsgConfirmEvent) {
//            MsgConfirmEvent ev = (MsgConfirmEvent) event;
//            confirmDelivery(ev.getDeliveryId());
//        }
    }

    public static class Message {
        public String msg;

        public Message(String msg) {
            this.msg = msg;
        }
    }
}
