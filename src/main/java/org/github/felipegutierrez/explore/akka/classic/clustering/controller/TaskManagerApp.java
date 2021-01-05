package org.github.felipegutierrez.explore.akka.classic.clustering.controller;

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
        int id = 1;
        for (int port : adComOperatorPorts) {
            // Configure the Actor System
            Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port = " + port)
                    .withFallback(ConfigFactory.load("clustering/controller.conf").getConfig("taskManagerAdComOperators"));
            final ActorSystem system = ActorSystem.create("JobManagerActorSystem", config);

            // Instantiate the monitor actors for each AdCom operator
            ActorRef adComOperator = system.actorOf(AdComMonitorActor.props(id), "adComOperator");

            Cancellable cancellable = system.scheduler().scheduleWithFixedDelay(
                    Duration.ofSeconds(2),
                    Duration.ofSeconds(5),
                    adComOperator,
                    collectSignals(),
                    system.dispatcher(),
                    adComOperator
            );

            id++;
        }
    }

    // public static void main(String[] args) {
    public static void run(String[] args) {
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
