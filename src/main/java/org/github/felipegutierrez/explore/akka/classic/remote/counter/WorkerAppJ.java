package org.github.felipegutierrez.explore.akka.classic.remote.counter;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class WorkerAppJ {
    public WorkerAppJ(int nWorkers) {
        Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port = 2552")
                .withFallback(ConfigFactory.load("remote/remoteActorsWordCount.conf"));
        ActorSystem system = ActorSystem.create("WorkersSystem", config);

        // deploying the worker REMOTELY and not creating then from the WorkerApp
        // for (int i = 1; i <= nWorkers; i++) {
        //     system.actorOf(WordCountWorkerJ.props(), "wordCountWorker" + i);
        // }
    }

    public static void main(String[] args) {
        new WorkerAppJ(5);
    }
}
