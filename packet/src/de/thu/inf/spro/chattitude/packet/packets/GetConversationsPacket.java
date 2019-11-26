package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetConversationsPacket extends Packet {

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_CONVERSATIONS = "conversations";

    private int userId;

    private Conversation[] conversations;

    public GetConversationsPacket(JsonObject packetData) {
        super(packetData);
    }

    public GetConversationsPacket(int userId){
        super(PacketType.GET_CONVERSATIONS);

        this.userId = userId;
        this.conversations =  new Conversation[]{};
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_USER_ID, userId);
        JsonArray conversationsArray = new JsonArray();
        Arrays.stream(conversations).map(Conversation::asJson).forEach(conversationsArray::add);
        packetData.add(FIELD_CONVERSATIONS, conversationsArray);
    }

    @Override
    protected void unpack() {
        super.unpack();

        List<Conversation> conversationList = new ArrayList<>();

        userId = packetData.get(FIELD_USER_ID).asInt();

        packetData.get(FIELD_CONVERSATIONS).asArray().iterator().forEachRemaining(v -> conversationList.add(new Conversation(v.asObject())));
        conversations = new Conversation[conversationList.size()];
        conversations = conversationList.toArray(conversations);
    }

    public void setConversations(Conversation[] conversations){
        this.conversations = conversations;
    }

    public int getUserId(){
        return userId;
    }

    public Conversation[] getConversations(){
        return conversations;
    }

}
