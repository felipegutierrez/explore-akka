package org.github.felipegutierrez.explore.akka.classic.remote.counter;

import java.io.Serializable;

public class WordCountTask implements Serializable {
    public final String text;

    WordCountTask(String text) {
        this.text = text;
    }
}
