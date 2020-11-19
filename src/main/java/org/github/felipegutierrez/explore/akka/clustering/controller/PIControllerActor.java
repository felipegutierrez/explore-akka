package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.*;

public class PIControllerActor extends AbstractLoggingActor {

    private final Cluster cluster = Cluster.get(getContext().getSystem());

    public static Props props() {
        return Props.create(PIControllerActor.class);
    }

    // subscribe to cluster changes
    @Override
    public void preStart() {
        cluster.subscribe(
                getSelf(),
                ClusterEvent.initialStateAsEvents(),
                MemberEvent.class,
                UnreachableMember.class);
    }

    // re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MemberJoined.class, this::receivedMemberJoined)
                .match(MemberUp.class, this::receivedMemberUp)
                .match(UnreachableMember.class, this::receivedUnreachableMember)
                .match(MemberRemoved.class, this::receivedMemberRemoved)
                .match(MemberEvent.class, this::receivedMemberEvent)
                .match(MessageAdcomSignals.class, this::receiveAdcomSignals)
                .match(MessageControllerTrigger.class, this::receiveControllerTrigger)
                .build();
    }

    private void receivedMemberJoined(MemberJoined message) {
        log().info("Member joined: {}", message.member());
    }

    private void receivedMemberUp(MemberUp message) {
        log().info("Member is Up: {}", message.member());
    }

    private void receivedUnreachableMember(UnreachableMember message) {
        log().info("Member detected as unreachable: {}", message.member());
    }

    private void receivedMemberRemoved(MemberRemoved message) {
        log().info("Member is Removed: {}", message.member());
    }

    private void receivedMemberEvent(MemberEvent message) {
        log().info("Member event: {}", message.member());
    }

    private void receiveAdcomSignals(MessageAdcomSignals message) {
        log().info("received AdCom signals: {}", message);
        // TODO: add signals at the global state
    }

    private void receiveControllerTrigger(MessageControllerTrigger message) {
        log().info("received trigger: {}", message);
        // TODO: compute new parameter based on the global state
        // TODO: send new parameter to all AdCom operators
    }
}
