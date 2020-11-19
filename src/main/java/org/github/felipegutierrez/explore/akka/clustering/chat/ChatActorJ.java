package org.github.felipegutierrez.explore.akka.clustering.chat;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

import java.util.HashMap;
import java.util.Map;

public class ChatActorJ extends AbstractLoggingActor {

    private final String nickname;
    private final int port;
    private final boolean authorized;
    private final Cluster cluster = Cluster.get(getContext().getSystem());


    public ChatActorJ(String nickname, int port, boolean authorized) {
        this.nickname = nickname;
        this.port = port;
        this.authorized = authorized;
    }

    public static Props props(String nickname, int port, boolean authorized) {
        return Props.create(ChatActorJ.class, nickname, port, authorized);
    }

    // subscribe to cluster changes
    @Override
    public void preStart() {
        cluster.subscribe(
                getSelf(),
                ClusterEvent.initialStateAsEvents(),
                MemberEvent.class);
    }

    // re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Object.class, o -> getContext().become(online(new HashMap<String, String>())))
                .build();
    }

    public final AbstractActor.Receive online(Map<String, String> dataStructure) {
        return receiveBuilder()
                .match(MemberUp.class, mUp -> {
                    // 4: send a special EnterRoom message to the chatActor deployed on a new node (hint: use Actor selection)
                    log().info("User " + nickname + " enter in the cluster new node: " + mUp.member().address());
                    ActorSelection actorSel = getContext().actorSelection(mUp.member().address() + "/user/chatActor");
                    actorSel.tell(new MessageEnterRoomJ(getSelf().path().address() + "@localhost:" + port, nickname), getSelf());
                })
                .match(UnreachableMember.class, mUnreachable -> {
                    log().info("Member detected as unreachable: {}", mUnreachable.member());
                })
                .match(MemberRemoved.class, mRemoved -> {
                    // 5: remove the member from your data structure
                    String remoteNickname = dataStructure.get(mRemoved.member().address().toString());
                    log().info("user " + remoteNickname + " left the room");
                    dataStructure.remove(mRemoved.member().address().toString());
                    getContext().become(online(dataStructure));
                })
                .match(MessageEnterRoomJ.class, m -> {
                    // 6: add the member to your data structure
                    if (m.nickname != nickname) {
                        log().info(m.nickname + " entered the room");
                    }
                    dataStructure.put(m.fullAddress, m.nickname);
                    getContext().become(online(dataStructure));
                })
                .match(MessageUserJ.class, m -> {
                    // 7: broadcast the content (as ChatMessage) to the rest of the cluster members
                    for (Map.Entry<String, String> entry : dataStructure.entrySet()) {
                        String address = entry.getKey();
                        ActorSelection actorSel = getContext().actorSelection(address + "/user/chatActor");
                        actorSel.tell(new MessageChatJ(nickname, m.content), getSelf());
                    }
                })
                .match(MessageChatJ.class, m -> {
                    log().info(m.nickname + " said: " + m.contents);
                })
                .match(MessageQuitJ.class, m -> {
                    log().info("message: {}", m);
                })
                .match(MemberEvent.class, memberEvent -> {
                    log().info("Member event ignored: {}", memberEvent.member());
                })
                .build();
    }
}
