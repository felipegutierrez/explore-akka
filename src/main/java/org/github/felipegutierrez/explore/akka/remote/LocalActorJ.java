package org.github.felipegutierrez.explore.akka.remote;

import akka.actor.*;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class LocalActorJ {
    public LocalActorJ() {
        final ActorSystem localSystem = ActorSystem.create("LocalSystem", ConfigFactory.load("remote/remoteActors.conf"));
        ActorRef localSimpleActor = localSystem.actorOf(SimpleActorJ.props(), "localSimpleActor");
        localSimpleActor.tell("Hello from LOCAL SimpleActorJ", localSimpleActor);

        /** 1 - send a message to the REMOTE simple actor using actor selection */
        ActorSelection remoteActorSelection = localSystem.actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActorJ");
        remoteActorSelection.tell("Hello from the \"LOCAL\" JVM", localSimpleActor);

        /** 2 - using actor ref */
        CompletionStage<ActorRef> remoteActorRefFuture = remoteActorSelection.resolveOne(Duration.ofSeconds(3));
        remoteActorRefFuture.whenComplete((actorRef, exception) -> {
            if (exception != null) {
                System.out.println("failed because: " + exception);
            } else {
                actorRef.tell("Hello message from a FUTURE Actor Ref =)", actorRef);
            }
        });

        /** 3 - resolving the actor reference by asking a message identification */
        ActorRef remoteActorResolver = localSystem.actorOf(ActorResolverJ.props(), "remoteActorResolver");
    }

    public static void main(String[] args) {
        new LocalActorJ();
    }

    private static class ActorResolverJ extends AbstractActor {
        final Integer identifyId = 42;

        static Props props() {
            return Props.create(ActorResolverJ.class, () -> new ActorResolverJ());
        }

        @Override
        public void preStart() throws Exception {
            ActorSelection selection = context().actorSelection("akka://RemoteSystem@localhost:2552/user/remoteSimpleActorJ");
            selection.tell(new Identify(identifyId), self());
        }

        public Receive createReceive() {
            // case ActorIdentity(42, Some(actorRef)) => actorRef ! "thank you for identifying yourself :-)"
            return receiveBuilder().match(
                    ActorIdentity.class,
                    id -> id.getActorRef() != null, // id -> id.getActorRef().isPresent(),
                    id -> {
                        ActorRef actorRef = id.getActorRef().get();
                        actorRef.tell("thank you for identifying yourself :-)", actorRef);
                    }).build();
        }
    }
}
