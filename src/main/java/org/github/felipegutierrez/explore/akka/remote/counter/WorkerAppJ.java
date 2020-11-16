package org.github.felipegutierrez.explore.akka.remote.counter;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class WorkerAppJ {
    public WorkerAppJ(int nWorkers) throws InterruptedException, IOException {
        Config config = ConfigFactory.parseString("akka.remote.artery.canonical.port = 2552");
        ActorSystem system = ActorSystem.create("WorkersSystem", config);

        for (int i = 1; i <= nWorkers; i++) {
            system.actorOf(WordCountWorkerJ.props(), "wordCountWorker" + i);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new WorkerAppJ(5);
    }
}
