package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.*;
import akka.cluster.Member;

import java.util.HashSet;
import java.util.Set;

public class PIControllerActor extends AbstractLoggingActor {

    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final Set<Address> adComOperators = new HashSet<Address>();
    private int newParameter = 0;

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
        // add new member on the data structure of AdCom operators
        Member newMember = message.member();
        if (newMember.hasRole(Utils.ROLE_ADCOM)) {
            adComOperators.add(newMember.address());
        }
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

    private void receiveControllerTrigger(MessageControllerTrigger message) {
        log().info("received trigger: {}", message);
        // send new parameter to all AdCom operators
        for (Address adComAddress : adComOperators) {
            ActorSelection adComOp = getContext().actorSelection("akka://" + adComAddress.hostPort() + "/user/adComOperator");
            adComOp.tell(new MessageAdComParameter(newParameter), getSelf());
        }
    }

    private void receiveAdcomSignals(MessageAdcomSignals message) {
        log().info("received AdCom signals: {}. Now let's merge it in the global state.", message);
        // add signals to the global state
    }

    private void computeGlobalSignal(MessageAdcomSignals message) {
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
}
