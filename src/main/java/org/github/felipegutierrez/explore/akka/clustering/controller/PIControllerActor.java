package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberJoined;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

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
                .match(MemberJoined.class, mJoin -> {
                    log().info("Member joined: {}", mJoin.member());
                })
                .match(MemberUp.class, mUp -> {
                    log().info("Member is Up: {}", mUp.member());
                })
                .match(UnreachableMember.class, mUnreachable -> {
                    log().info("Member detected as unreachable: {}", mUnreachable.member());
                })
                .match(MemberRemoved.class, mRemoved -> {
                    log().info("Member is Removed: {}", mRemoved.member());
                })
                .match(MemberEvent.class, message -> {
                    log().info("Member unhandled: {}", message.member());
                })
                .build();
    }
}
