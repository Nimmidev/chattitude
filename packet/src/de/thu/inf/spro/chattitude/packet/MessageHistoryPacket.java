package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MessageHistoryPacket extends Packet {

    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_OFFSET = "offset";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_MESSAGES = "messages";

    private int conversationId;
    private int offset;
    private int amount;

    //Later on this would be replaced with a message array
    private String[] messages;

    public MessageHistoryPacket(JsonObject packetData) {
        super(packetData);
    }

    public MessageHistoryPacket(int conversationId, int offset, int amount) {
        this(conversationId, offset, amount, new String[]{});
    }

    public MessageHistoryPacket(int conversationId, int offset, int amount, String[] messages) {
        super(PacketType.MESSAGE_HISTORY);

        this.conversationId = conversationId;
        this.offset = offset;
        this.amount = amount;
        this.messages = messages;
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.add(FIELD_CONVERSATION_ID, conversationId);
        packetData.add(FIELD_OFFSET, offset);
        packetData.add(FIELD_AMOUNT, amount);
        packetData.add(FIELD_MESSAGES, Json.array(messages));
    }

    @Override
    protected void unpack(){
        super.unpack();

        List<String> messageList = new ArrayList<>();

        conversationId = packetData.get(FIELD_CONVERSATION_ID).asInt();
        offset = packetData.get(FIELD_OFFSET).asInt();
        amount = packetData.get(FIELD_AMOUNT).asInt();

        packetData.get(FIELD_MESSAGES).asArray().iterator().forEachRemaining(v -> messageList.add(v.asString()));
        messages = new String[messageList.size()];
        messages = messageList.toArray(messages);
    }

    public int getConversationId(){
        return conversationId;
    }

    public int getOffset(){
        return offset;
    }

    public int getAmount(){
        return amount;
    }

    public String[] getMessages(){
        return messages;
    }

}
