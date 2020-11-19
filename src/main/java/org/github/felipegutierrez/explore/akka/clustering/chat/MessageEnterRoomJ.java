package org.github.felipegutierrez.explore.akka.clustering.chat;

import java.io.Serializable;

public class MessageEnterRoomJ implements Serializable {

    public String fullAddress;
    public String nickname;

    public MessageEnterRoomJ(String fullAddress, String nickname) {
        this.fullAddress = fullAddress;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "MessageEnterRoomJ{" +
                "fullAddress='" + fullAddress + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
