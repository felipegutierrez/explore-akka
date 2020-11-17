package org.github.felipegutierrez.explore.akka.remote.controller;

import akka.actor.*;
import akka.remote.RemoteScope;
import com.typesafe.config.ConfigFactory;

public class AdComOperator {

    public AdComOperator(int nAdComPhysicalOperators) {
        final ActorSystem localSystem = ActorSystem.create("TaskManagerActorSystem",
                ConfigFactory.load("remote/controller.conf").getConfig("adcomApp"));

        /** Remote deployment programmatically on the JobManager that handles the PIController */
        Address remoteSystemAddress = AddressFromURIString.parse("akka://JobManagerActorSystem@localhost:2552");
        Deploy deploy = new Deploy(new RemoteScope(remoteSystemAddress));
        ActorRef remotelyDeployedActor = localSystem.actorOf(AdComMonitorSignals.props().withDeploy(deploy));

        remotelyDeployedActor.tell("Hi remotely deployed actor programmatically!", remotelyDeployedActor);


    }

    public static void main(String[] args) {
        new AdComOperator(3);
    }

    private Object collectSignals() {
        return new MessageSignals(50, 75, 10000, 4000);
    }
}
