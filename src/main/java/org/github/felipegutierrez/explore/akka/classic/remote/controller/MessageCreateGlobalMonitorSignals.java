package org.github.felipegutierrez.explore.akka.classic.remote.controller;

import java.io.Serializable;

public class MessageCreateGlobalMonitorSignals implements Serializable {
    public final int globalID;

    public MessageCreateGlobalMonitorSignals(int globalID) {
        this.globalID = globalID;
    }
}
