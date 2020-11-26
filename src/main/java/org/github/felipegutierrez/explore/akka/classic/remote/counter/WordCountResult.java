package org.github.felipegutierrez.explore.akka.classic.remote.counter;

import java.io.Serializable;

public class WordCountResult implements Serializable {
    public final int count;

    public WordCountResult(int count) {
        this.count = count;
    }
}
