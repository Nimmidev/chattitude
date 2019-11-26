package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.Iterator;

public class CreateConversationPacket extends Packet {

    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_GROUP_NAME = "groupName";
    private static final String FIELD_USER_IDS = "userIds";

    private int conversationId;
    private String groupName;
    private int[] userIds;

    public CreateConversationPacket(JsonObject packetData){
        super(packetData);
    }

    public CreateConversationPacket(int... users){
        this("", users);
    }

    public CreateConversationPacket(String groupName, int... userIds){
        super(PacketType.CREATE_CONVERSATION);

        this.conversationId = -1;
        this.groupName = groupName;
        this.userIds = userIds;
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_CONVERSATION_ID, conversationId);
        packetData.add(FIELD_GROUP_NAME, groupName);
        packetData.add(FIELD_USER_IDS, Json.array(userIds));
    }

    @Override
    protected void unpack() {
        super.unpack();

        conversationId = packetData.get(FIELD_CONVERSATION_ID).asInt();
        groupName = packetData.get(FIELD_GROUP_NAME).asString();
        JsonArray userIdArray = packetData.get(FIELD_USER_IDS).asArray();
        Iterator iterator = userIdArray.iterator();
        userIds = new int[userIdArray.size()];
        for(int i = 0; iterator.hasNext(); i++) userIds[i] = ((JsonValue)iterator.next()).asInt();
    }

    public void setConversationId(int conversationId){
        this.conversationId = conversationId;
    }

    public int getConversationId(){
        return conversationId;
    }

    public String getGroupName(){
        return groupName;
    }

    public int[] getUserIds(){
        return userIds;
    }

}
