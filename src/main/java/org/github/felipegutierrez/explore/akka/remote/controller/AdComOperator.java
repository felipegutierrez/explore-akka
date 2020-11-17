package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.*;
import akka.remote.RemoteScope;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AdComOperator {
    private final int MIN_POLL = 0;
    private final int MAX_POLL = 100;
    private final int MIN_THROUGHPUT = 0;
    private final int MAX_THROUGHPUT = 10000;

    private final List<ActorRef> monitorActors = new ArrayList<ActorRef>();
    private final List<Cancellable> monitorActorCancellers = new ArrayList<Cancellable>();

    public AdComOperator(int nAdComPhysicalOperators) throws InterruptedException {
        final ActorSystem localSystem = ActorSystem.create("TaskManagerActorSystem",
                ConfigFactory.load("remote/controller.conf").getConfig("adcomApp"));

        // ID of the global monitor
        int globalID = ThreadLocalRandom.current().nextInt(1, 100001);

        /** Remote deployment programmatically on the JobManager that handles the PIController */
        Address remoteSystemAddress = AddressFromURIString.parse("akka://JobManagerActorSystem@localhost:2552");
        Deploy deploy = new Deploy(new RemoteScope(remoteSystemAddress));

        for (int i = 1; i <= nAdComPhysicalOperators; i++) {
            ActorRef remotelyDeployedActor = localSystem.actorOf(AdComMonitorSignals.props().withDeploy(deploy), "remotelyDeployedActor" + i);
            monitorActors.add(remotelyDeployedActor);
            remotelyDeployedActor.tell("Hi [" + i + "] remotely deployed actor programmatically!", remotelyDeployedActor);

            // create only one global monitor actor
            remotelyDeployedActor.tell(new MessageCreateGlobalMonitorSignals(globalID), remotelyDeployedActor);
            Thread.sleep(1000);

            Cancellable cancellable = localSystem.scheduler()
                    .scheduleWithFixedDelay(
                            Duration.ZERO,
                            Duration.ofSeconds(2),
                            remotelyDeployedActor,
                            collectSignals(),
                            localSystem.dispatcher(),
                            remotelyDeployedActor);
            monitorActorCancellers.add(cancellable);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new AdComOperator(3);
    }

    private Object collectSignals() {
        int outPollAvg = ThreadLocalRandom.current().nextInt(MIN_POLL, MAX_POLL + 1);
        int outPoll75Qt = ThreadLocalRandom.current().nextInt(MIN_POLL, MAX_POLL + 1);
        int throughputIn = ThreadLocalRandom.current().nextInt(MIN_THROUGHPUT, MAX_THROUGHPUT + 1);
        int throughputOut = ThreadLocalRandom.current().nextInt(MIN_THROUGHPUT, MAX_THROUGHPUT + 1);
        return new MessageSignals(outPollAvg, outPoll75Qt, throughputIn, throughputOut);
    }

    private void cancelSchedulers() {
        /** This cancels further MessageSignals to be sent */
        for (Cancellable cancellable : monitorActorCancellers) {
            cancellable.cancel();
        }
    }
}
