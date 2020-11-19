package org.github.felipegutierrez.explore.akka.clustering.chat;

import java.io.Serializable;

public class MessageUserJ implements Serializable {
    public String content;

    public MessageUserJ(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageUserJ{" +
                "content='" + content + '\'' +
                '}';
    }
}
