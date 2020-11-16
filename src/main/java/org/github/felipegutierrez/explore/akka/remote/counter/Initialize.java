package org.github.felipegutierrez.explore.akka.remote.counter;

import java.io.Serializable;

public class Initialize implements Serializable {
    public final int nWorkers;

    public Initialize(int nWorkers) {
        this.nWorkers = nWorkers;
    }
}
