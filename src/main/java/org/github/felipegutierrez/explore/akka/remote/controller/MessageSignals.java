package org.github.felipegutierrez.explore.akka.remote.controller;

import java.io.Serializable;

public class MessageSignals implements Serializable {
    public final int outPollAvg;
    public final int outPoll75Qt;
    public final int throughputIn;
    public final int throughputOut;

    public MessageSignals(int outPollAvg, int outPoll75Qt, int throughputIn, int throughputOut) {
        this.outPollAvg = outPollAvg;
        this.outPoll75Qt = outPoll75Qt;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
    }

    @Override
    public String toString() {
        return "MessageSignals{" +
                "outPollAvg=" + outPollAvg +
                ", outPoll75Qt=" + outPoll75Qt +
                ", throughputIn=" + throughputIn +
                ", throughputOut=" + throughputOut +
                '}';
    }
}
