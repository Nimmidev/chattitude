package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class MessagePacket extends Packet {

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_MESSAGE = "message";

    private int userId;
    private int conversationId;
    private String message;

    public MessagePacket(JsonObject packetData) {
        super(packetData);
    }

    public MessagePacket(int userId, int conversationId, String message) {
        super(PacketType.MESSAGE);

        this.userId = userId;
        this.conversationId = conversationId;
        this.message = message;
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.add(FIELD_USER_ID, userId);
        packetData.add(FIELD_CONVERSATION_ID, conversationId);
        packetData.add(FIELD_MESSAGE, message);
    }

    @Override
    protected void unpack(){
        super.unpack();

        userId = packetData.get(FIELD_USER_ID).asInt();
        conversationId = packetData.get(FIELD_CONVERSATION_ID).asInt();
        message = packetData.get(FIELD_MESSAGE).asString();
    }

    public int getUserId(){
        return userId;
    }

    public int getConversationId(){
        return conversationId;
    }

    public String getMessage(){
        return message;
    }

}
