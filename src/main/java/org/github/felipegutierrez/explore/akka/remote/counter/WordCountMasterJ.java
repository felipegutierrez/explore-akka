package org.github.felipegutierrez.explore.akka.remote.counter;

import akka.actor.*;

import java.util.ArrayList;
import java.util.List;

public class WordCountMasterJ extends AbstractLoggingActor {
    final Integer identifyId = 42;

    public static Props props() {
        return Props.create(WordCountMasterJ.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, this::onInitialize)
                .build();
    }

    private void onInitialize(Initialize initialize) {
        log().info("WordCountMaster initializing " + initialize.nWorkers + " workers...");
        /** identify the workers in the remote JVM */
        // 1 - create actor selections for every worker from 1 to nWorkers
        final List<ActorSelection> workers = new ArrayList<ActorSelection>();
        for (int id = 1; id <= initialize.nWorkers; id++) {
            // workers.add(getContext().getSystem().actorSelection("akka://WorkersSystem@localhost:2552/user/wordCountWorker" + id));
            ActorSelection selection = getContext().getSystem().actorSelection("akka://WorkersSystem@localhost:2552/user/wordCountWorker" + id);
            // 2 - send Identify messages to the actor selections
            selection.tell(new Identify(identifyId), self());
        }
        // for (ActorSelection workerSelection : workers) {
        //     workerSelection.tell(new Identify(identifyId), self());
        // }
        // 3 - get into an initialization state, while you are receiving ActorIdentities
        getContext().become(initializing(new ArrayList<ActorRef>(), initialize.nWorkers));
    }

    private final AbstractActor.Receive initializing(List<ActorRef> workers, int remainingWorkers) {
        return receiveBuilder().match(
                ActorIdentity.class,
                id -> id.getActorRef() != null,
                // id -> "word_count_identity".equals(id.correlationId()) && id.getActorRef().isPresent(),
                // id -> id.getActorRef().isPresent(),
                id -> {
                    ActorRef workerRef = id.getActorRef().get();
                    log().info("Worker identified: " + workerRef);
                    if (remainingWorkers == 1) {
                        workers.add(workerRef);
                        getContext().become(online(workers, 0, 0));
                    } else {
                        workers.add(workerRef);
                        getContext().become(initializing(workers, remainingWorkers - 1));
                    }
                }).build();
    }

    private final AbstractActor.Receive online(List<ActorRef> workers, int remainingTasks, int totalCount) {
        return receiveBuilder()
                .match(String.class, text -> {
                    // split into sentences
                    String[] sentences = text.split("\\. ");
                    // send sentences to worker in turn
                    int workerID = 0;
                    for (int i = 0; i < sentences.length; i++) {
                        String sentence = sentences[i];
                        ActorRef w = workers.get(workerID);
                        w.tell(new WordCountTask((sentence)), w);

                        // get next worker ID
                        workerID++;
                        if (workerID >= workers.size()) workerID = 0;
                    }
                    getContext().become(online(workers, remainingTasks + sentences.length, totalCount));
                })
                .match(WordCountResult.class, wordCountResult -> {
                    if (remainingTasks == 1) {
                        log().info("TOTAL RESULT: " + (totalCount + wordCountResult.count));
                        for (ActorRef w : workers) {
                            w.tell(PoisonPill.getInstance(), w);
                        }
                        getContext().stop(getSelf());
                    } else {
                        getContext().become(online(workers, remainingTasks - 1, totalCount + wordCountResult.count));
                    }
                })
                .build();
    }
}
