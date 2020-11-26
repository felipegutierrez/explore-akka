package org.github.felipegutierrez.explore.akka.classic.remote.counter;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Props;

public class WordCountWorkerJ extends AbstractLoggingActor {
    public static Props props() {
        return Props.create(WordCountWorkerJ.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorIdentity.class,
                        id -> id.getActorRef().isPresent(),
                        id -> {
                            log().info("received ActorIdentity message: " + id.correlationId());
                            ActorRef actorRef = id.getActorRef().get();
                            actorRef.tell("thank you for identifying yourself work actor: " + actorRef, ActorRef.noSender());
                        })
                // .match(ActorIdentity.class, this::onIdentity) // id -> id.getActorRef() != null
                .match(WordCountTask.class, this::onTask)
                .build();
    }

    private void onIdentity(ActorIdentity actorIdentity) {
        actorIdentity.correlationId();
        ActorRef actorRef = actorIdentity.getActorRef().get();
        log().info("received ActorIdentity message: ");
        actorRef.tell("thank you for identifying yourself work actor: " + actorRef, ActorRef.noSender());
    }

    private void onTask(WordCountTask wordCountTask) {
        log().info("I am processing: " + wordCountTask.text);
        sender().tell(new WordCountResult(wordCountTask.text.split(" ").length), ActorRef.noSender());
    }
}
