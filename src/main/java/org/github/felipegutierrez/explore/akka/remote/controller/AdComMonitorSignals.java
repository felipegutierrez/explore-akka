package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.*;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class AdComMonitorSignals extends AbstractLoggingActor {

    public ActorRef globalMonitor = null;

    public static Props props() {
        return Props.create(AdComMonitorSignals.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::onString)
                .match(MessageCreateGlobalMonitorSignals.class, this::createGlobalMonitorSignals)
                .match(MessageSignals.class, this::receiveMessageSignals)
                .match(MessageParameter.class, this::listenNewParameter)
                .match(Terminated.class, t -> {
                    log().info("Terminated actor: " + t.actor());
                })
                .build();
    }

    private void listenNewParameter(MessageParameter message) {
        log().info("new parameter: " + message + " applied to [" + getSelf() + "] from " + getSender());
    }

    private void receiveMessageSignals(MessageSignals messageSignals) {
        log().info("Received message signals: " + messageSignals.toString() + " from " + sender());
        System.out.println("let's sent to : " + globalMonitor);

        globalMonitor.tell(new MessageGlobalSignal(messageSignals), getSelf());
    }

    private void onString(String message) {
        log().info("Received [" + message + "] from " + sender());
    }

    private void createGlobalMonitorSignals(MessageCreateGlobalMonitorSignals message) throws InterruptedException {
        /** handle message to create only one global actor */
        System.out.println("received create message: " + message.globalID);
        CompletionStage<ActorRef> globalMonitorAttempt = getContext()
                .actorSelection("akka://JobManagerActorSystem/remote/akka/TaskManagerActorSystem@localhost:2551/user/remotelyDeployedActor1/globalMonitor" + message.globalID)
                .resolveOne(Duration.ofMillis(1000));
        globalMonitorAttempt.whenComplete((actorRef, exception) -> {
            try {
                if (exception != null) {
                    globalMonitor = getContext()
                            .actorOf(PIControllerMonitorSignals.props(), "globalMonitor" + message.globalID);

                    Cancellable cancellable = getContext().system()
                            .scheduler()
                            .scheduleWithFixedDelay(
                                    Duration.ofSeconds(2),
                                    Duration.ofSeconds(10),
                                    globalMonitor,
                                    new MessageTrigger(),
                                    getContext().dispatcher(),
                                    globalMonitor);

                } else {
                    globalMonitor = getContext()
                            .actorSelection("akka://JobManagerActorSystem/remote/akka/TaskManagerActorSystem@localhost:2551/user/remotelyDeployedActor1/globalMonitor" + message.globalID)
                            .resolveOne(Duration.ofMillis(1000))
                            .toCompletableFuture()
                            .get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(1000);
    }
}
