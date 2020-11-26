package org.github.felipegutierrez.explore.akka.classic.clustering.controller;

import java.io.Serializable;

public class MessageAdcomSignals implements Serializable {
    public final int outPollAvg;
    public final int outPoll75Qt;
    public final int throughputIn;
    public final int throughputOut;

    public MessageAdcomSignals(int outPollAvg, int outPoll75Qt, int throughputIn, int throughputOut) {
        this.outPollAvg = outPollAvg;
        this.outPoll75Qt = outPoll75Qt;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
    }

    @Override
    public String toString() {
        return "MessageAdcomSignals{" +
                "outPollAvg=" + outPollAvg +
                ", outPoll75Qt=" + outPoll75Qt +
                ", throughputIn=" + throughputIn +
                ", throughputOut=" + throughputOut +
                '}';
    }
}
