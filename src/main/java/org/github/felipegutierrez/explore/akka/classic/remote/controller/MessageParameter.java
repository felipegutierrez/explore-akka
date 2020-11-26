package org.github.felipegutierrez.explore.akka.classic.remote.controller;

import java.io.Serializable;

public class MessageParameter implements Serializable {
    public final int parameter;

    public MessageParameter(int parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "MessageParameter{" +
                "parameter=" + parameter +
                '}';
    }
}
