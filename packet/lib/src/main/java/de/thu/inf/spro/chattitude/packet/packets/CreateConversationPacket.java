package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.Iterator;

public class CreateConversationPacket extends Packet {

    private Conversation conversation;

    public CreateConversationPacket(JsonObject packetData){
        super(packetData);
    }

    public CreateConversationPacket(Conversation conversation){
        super(PacketType.CREATE_CONVERSATION);
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
