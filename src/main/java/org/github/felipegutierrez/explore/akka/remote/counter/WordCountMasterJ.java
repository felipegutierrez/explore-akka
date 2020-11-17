package org.github.felipegutierrez.explore.akka.remote.counter;

import akka.actor.*;
import akka.routing.FromConfig;

import java.util.ArrayList;
import java.util.List;

public class WordCountMasterJ extends AbstractLoggingActor {

    private final ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(WordCountWorkerJ.props()), "workerRouter");

    public static Props props() {
        return Props.create(WordCountMasterJ.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // .match(Initialize.class, this::onInitialize)
                .match(Object.class,
                        object -> {
                            getContext().become(onlineWithRouter(0, 0));
                        })
                .build();
    }

    private final AbstractActor.Receive onlineWithRouter(int remainingTasks, int totalCount) {
        return receiveBuilder()
                .match(String.class, text -> {
                    // split into sentences
                    String[] sentences = text.split("\\. ");
                    // send sentences to worker in turn
                    // sentences.foreach(sentence => workerRouter ! WordCountTask(sentence))
                    for (int i = 0; i < sentences.length; i++) {
                        workerRouter.tell(new WordCountTask(sentences[i]), getSelf());
                    }
                    getContext().become(onlineWithRouter(remainingTasks + sentences.length, totalCount));
                })
                .match(WordCountResult.class, wordCountResult -> {
                    if (remainingTasks == 1) {
                        log().info("TOTAL RESULT: " + (totalCount + wordCountResult.count));
                        getContext().stop(getSelf());
                    } else {
                        getContext().become(onlineWithRouter(remainingTasks - 1, totalCount + wordCountResult.count));
                    }
                })
                .build();
    }

    private void onInitialize(Initialize initialize) {
        /** deploying the workers REMOTELY on the worker App using "/wordCountMaster/" at remoteActorsWordCount.conf */
        final List<ActorRef> workers = new ArrayList<ActorRef>();
        for (int id = 1; id <= initialize.nWorkers; id++) {
            workers.add(getContext().actorOf(WordCountWorkerJ.props(), "wordCountWorker" + id));
        }
        getContext().become(online(workers, 0, 0));
    }

    /**
     * @deprecated because we are not using Selection but deploying the workers remotely using the Initialize case.
     */
    private void identifyWorkers(Initialize initialize) {
        log().info("WordCountMaster initializing " + initialize.nWorkers + " workers...");
        /** identify the workers in the remote JVM */
        // 1 - create actor selections for every worker from 1 to nWorkers
        final List<ActorSelection> workers = new ArrayList<ActorSelection>();
        for (int id = 1; id <= initialize.nWorkers; id++) {
            ActorSelection selection = getContext().actorSelection("akka://WorkersSystem@localhost:2552/user/wordCountWorker" + id);
            // 2 - send Identify messages to the actor selections
            selection.tell(new Identify("wordCountId"), getSelf());
        }
        // 3 - get into an initialization state, while you are receiving ActorIdentities
        getContext().become(initializing(new ArrayList<ActorRef>(), initialize.nWorkers));
    }

    private final AbstractActor.Receive initializing(List<ActorRef> workers, int remainingWorkers) {
        return receiveBuilder().match(
                ActorIdentity.class,
                id -> id.getActorRef().isPresent() && "wordCountId".equals(id.correlationId()),
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

    /**
     * @deprecated
     */
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
                        w.tell(new WordCountTask(sentence), w);

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
