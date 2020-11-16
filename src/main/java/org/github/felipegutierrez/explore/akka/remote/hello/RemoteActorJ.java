package org.github.felipegutierrez.explore.akka.remote.hello;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class RemoteActorJ {
    public RemoteActorJ() {
        final ActorSystem remoteSystem = ActorSystem.create("RemoteSystem", ConfigFactory.load("remote/remoteActors.conf").getConfig("remoteSystem"));
        ActorRef remoteSimpleActor = remoteSystem.actorOf(SimpleActorJ.props(), "remoteSimpleActorJ");
        remoteSimpleActor.tell(new SimpleMessage("Hello from REMOTE SimpleActorJ"), remoteSimpleActor);
    }

    public static void main(String[] args) {
        new RemoteActorJ();
    }
}
