package org.github.felipegutierrez.explore.akka.clustering.controller;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TaskManagerApp {

    public TaskManagerApp(int[] adComOperatorPorts) {
        for (int port : adComOperatorPorts) {
            // 1 - configure the Actor System
            Config config = ConfigFactory
                    .parseString("akka.cluster.roles = [worker]" +
                            ",akka.remote.artery.canonical.port = " + port)
                    .withFallback(ConfigFactory.load("clustering/controller.conf"));

            final ActorSystem system = ActorSystem.create("JobManagerActorSystem", config);

            // 2 - instantiate the monitor actors for each AdCom operator
            system.actorOf(AdComMonitorActor.props(), "adComMonitor");
        }
    }

    public static void main(String[] args) {
        new TaskManagerApp(new int[]{2552, 2553});
    }
}
