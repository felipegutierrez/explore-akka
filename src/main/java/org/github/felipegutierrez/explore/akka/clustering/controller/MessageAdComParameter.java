package org.github.felipegutierrez.explore.akka.clustering.controller;

import java.io.Serializable;

public class MessageAdComParameter implements Serializable {
    public final int parameter;

    public MessageAdComParameter(int parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "MessageAdComParameter{" +
                "parameter=" + parameter +
                '}';
    }
}
