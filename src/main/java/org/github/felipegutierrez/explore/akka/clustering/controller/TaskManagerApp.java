package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class TaskManagerApp {

    private final int MIN_POLL = 0;
    private final int MAX_POLL = 100;
    private final int MIN_THROUGHPUT = 0;
    private final int MAX_THROUGHPUT = 10000;

    public TaskManagerApp(int[] adComOperatorPorts) {
        for (int port : adComOperatorPorts) {
            // 1 - configure the Actor System
            Config config = ConfigFactory
                    .parseString("akka.cluster.roles = [" + Utils.ROLE_ADCOM + "]" +
                            ",akka.remote.artery.canonical.port = " + port)
                    .withFallback(ConfigFactory.load("clustering/controller.conf"));

            final ActorSystem system = ActorSystem.create("JobManagerActorSystem", config);

            // 2 - instantiate the monitor actors for each AdCom operator
            ActorRef adComOperator = system.actorOf(AdComMonitorActor.props(), "adComOperator");

            Cancellable scheduleAdcomSignalsTrigger = system.scheduler().scheduleWithFixedDelay(
                    Duration.ofSeconds(2),
                    Duration.ofSeconds(5),
                    adComOperator,
                    collectSignals(),
                    system.dispatcher(),
                    adComOperator
            );
        }
    }

    public static void main(String[] args) {
        new TaskManagerApp(new int[]{2552, 2553});
    }

    private MessageAdcomSignals collectSignals() {
        int outPollAvg = ThreadLocalRandom.current().nextInt(MIN_POLL, MAX_POLL + 1);
        int outPoll75Qt = ThreadLocalRandom.current().nextInt(MIN_POLL, MAX_POLL + 1);
        int throughputIn = ThreadLocalRandom.current().nextInt(MIN_THROUGHPUT, MAX_THROUGHPUT + 1);
        int throughputOut = ThreadLocalRandom.current().nextInt(MIN_THROUGHPUT, MAX_THROUGHPUT + 1);
        return new MessageAdcomSignals(outPollAvg, outPoll75Qt, throughputIn, throughputOut);
    }
}
