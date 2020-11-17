package org.github.felipegutierrez.explore.akka.remote.controller;

import java.io.Serializable;

public class MessageGlobalSignal implements Serializable {
    public final int outPollAvg;
    public final int outPoll75Qt;
    public final int throughputIn;
    public final int throughputOut;

    public MessageGlobalSignal(int outPollAvg, int outPoll75Qt, int throughputIn, int throughputOut) {
        this.outPollAvg = outPollAvg;
        this.outPoll75Qt = outPoll75Qt;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
    }

    public MessageGlobalSignal(MessageSignals messageSignals) {
        this.outPollAvg = messageSignals.outPollAvg;
        this.outPoll75Qt = messageSignals.outPoll75Qt;
        this.throughputIn = messageSignals.throughputIn;
        this.throughputOut = messageSignals.throughputOut;
    }

    @Override
    public String toString() {
        return "MessageGlobalSignal{" +
                "outPollAvg=" + outPollAvg +
                ", outPoll75Qt=" + outPoll75Qt +
                ", throughputIn=" + throughputIn +
                ", throughputOut=" + throughputOut +
                '}';
    }
}
