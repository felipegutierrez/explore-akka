package org.github.felipegutierrez.explore.akka.classic.clustering.chat;

import java.io.Serializable;

public class MessageChatJ implements Serializable {
    public String nickname;
    public String contents;

    public MessageChatJ(String nickname, String contents) {
        this.nickname = nickname;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "MessageChatJ{" +
                "nickname='" + nickname + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
