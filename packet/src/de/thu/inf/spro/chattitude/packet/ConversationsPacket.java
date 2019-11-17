package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ConversationsPacket extends Packet {

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_CONVERSATIONS = "conversations";

    private int userId;

    //Later on this would be replaced with a conversation array
    private String[] conversations;

    public ConversationsPacket(JsonObject packetData) {
        super(packetData);
    }

    public ConversationsPacket(int userId) {
        this(userId, new String[]{});
    }

    public ConversationsPacket(int userId, String[] conversations){
        super(PacketType.CONVERSATIONS);

        this.userId = userId;
        this.conversations = conversations;
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_USER_ID, userId);
        packetData.add(FIELD_CONVERSATIONS, Json.array(conversations));
    }

    @Override
    protected void unpack() {
        super.unpack();

        List<String> conversationList = new ArrayList<>();

        userId = packetData.get(FIELD_USER_ID).asInt();

        packetData.get(FIELD_CONVERSATIONS).asArray().iterator().forEachRemaining(v -> conversationList.add(v.asString()));
        conversations = new String[conversationList.size()];
        conversations = conversationList.toArray(conversations);
    }

    public int getUserId(){
        return userId;
    }

    public String[] getConversations(){
        return conversations;
    }

}
