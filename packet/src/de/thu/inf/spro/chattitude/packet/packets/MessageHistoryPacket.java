package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHistoryPacket extends Packet {

    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_OFFSET = "offset";
    private static final String FIELD_MESSAGES = "messages";

    private int conversationId;
    private int offset;

    private Message[] messages;

    public MessageHistoryPacket(JsonObject packetData) {
        super(packetData);
    }

    public MessageHistoryPacket(int conversationId, int offset) {
        super(PacketType.MESSAGE_HISTORY);

        this.conversationId = conversationId;
        this.offset = offset;
        this.messages = new Message[]{};
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.add(FIELD_CONVERSATION_ID, conversationId);
        packetData.add(FIELD_OFFSET, offset);

        JsonArray messagesArray = new JsonArray();
        Arrays.stream(messages).map(Message::asJson).forEach(messagesArray::add);
        packetData.add(FIELD_MESSAGES, messagesArray);
    }

    @Override
    protected void unpack(){
        super.unpack();

        List<Message> messageList = new ArrayList<>();

        conversationId = packetData.get(FIELD_CONVERSATION_ID).asInt();
        offset = packetData.get(FIELD_OFFSET).asInt();

        packetData.get(FIELD_MESSAGES).asArray().iterator().forEachRemaining(v -> messageList.add(new Message(v.asObject())));
        messages = new Message[messageList.size()];
        messages = messageList.toArray(messages);
    }

    public void setMessages(Message[] messages){
        this.messages = messages;
    }

    public int getConversationId(){
        return conversationId;
    }

    public int getOffset(){
        return offset;
    }

    public Message[] getMessages(){
        return messages;
    }

}
