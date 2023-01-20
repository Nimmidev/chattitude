package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class ConversationUpdatedPacket extends Packet {

    private Conversation conversation;

    public ConversationUpdatedPacket(JsonObject packetData) {
        super(packetData);
    }

    public ConversationUpdatedPacket(Conversation conversation) {
        super(PacketType.CONVERSATION_UPDATED);

        this.conversation = conversation;
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.merge(conversation.asJson());
    }

    @Override
    protected void unpack() {
        super.unpack();

        conversation = new Conversation(packetData);
    }

    public Conversation getConversation(){
        return conversation;
    }

}
