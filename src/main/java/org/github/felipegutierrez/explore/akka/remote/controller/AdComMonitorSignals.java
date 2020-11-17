package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;

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
                .match(Terminated.class, t -> {
                    log().info("Terminated actor: " + t.actor());
                })
                .build();
    }

    private void receiveMessageSignals(MessageSignals messageSignals) {
        log().info("Received message signals: " + messageSignals.toString() + " from " + sender());
        System.out.println("let's sent to : " + globalMonitor);
    }

    private void onString(String message) {
        log().info("Received [" + message + "] from " + sender());
    }

    private void createGlobalMonitorSignals(MessageCreateGlobalMonitorSignals message) throws InterruptedException {
        System.out.println("received create message: " + message.globalID);
        // ActorRef remotelyDeployedActor = localSystem.actorOf(AdComMonitorSignals.props().withDeploy(deploy), "remotelyDeployedActor" + i);
        // PIControllerMonitorSignals


        CompletionStage<ActorRef> globalMonitorAttempt = getContext()
                .actorSelection("akka://JobManagerActorSystem/remote/akka/TaskManagerActorSystem@localhost:2551/user/remotelyDeployedActor*/globalMonitor" + message.globalID)
                .resolveOne(Duration.ofMillis(1000));
        globalMonitorAttempt.whenComplete((actorRef, exception) -> {
            try {
                if (exception != null) {
                    globalMonitor = getContext()
                            .actorOf(PIControllerMonitorSignals.props(), "globalMonitor" + message.globalID);
                } else {
                    globalMonitor = getContext()
                            .actorSelection("akka://JobManagerActorSystem/remote/akka/TaskManagerActorSystem@localhost:2551/user/remotelyDeployedActor*/globalMonitor" + message.globalID)
                            .resolveOne(Duration.ofMillis(1000))
                            .toCompletableFuture()
                            .get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        });

        Thread.sleep(2000);
        System.out.println("print globalMonitor");
        System.out.println(globalMonitor);
    }
}
