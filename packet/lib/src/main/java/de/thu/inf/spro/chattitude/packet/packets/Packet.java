package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class Packet {

    private static final String FIELD_TYPE = "type";
    private static final String FIELD_SUCCESSFUL = "successful";

    protected PacketType type;
    protected JsonObject packetData;
    private boolean successful;

    public Packet(PacketType type){
        this.type = type;
    }

    public Packet(JsonObject packetData){
        this.packetData = packetData;
        unpack();
    }

    public static Packet of(JsonObject packetData){
        Packet packet = new Packet(packetData);
        PacketType type = packet.getType();

        if(type == PacketType.CONNECTED) return packet;
        else if(type == PacketType.UNAUTHORIZED) return packet;
        else if(type == PacketType.REGISTER) return new RegisterPacket(packetData);
        else if(type == PacketType.AUTHENTICATE) return new AuthenticationPacket(packetData);
        else if(type == PacketType.MESSAGE) return new MessagePacket(packetData);
        else if(type == PacketType.MESSAGE_HISTORY) return new MessageHistoryPacket(packetData);
        else if(type == PacketType.GET_CONVERSATIONS) return new GetConversationsPacket(packetData);
        else if(type == PacketType.CREATE_CONVERSATION) return new CreateConversationPacket(packetData);
        else if(type == PacketType.CONVERSATION_UPDATED) return new ConversationUpdatedPacket(packetData);
        else if(type == PacketType.MODIFY_CONVERSATION_USER) return new ModifyConversationUserPacket(packetData);
        else if(type == PacketType.GET_ATTACHMENT) return new GetAttachmentPacket(packetData);
        else if(type == PacketType.SEARCH_USER) return new SearchUserPacket(packetData);
        else throw new IllegalStateException("Invalid package type: " + type.name());
    }

    protected void pack(){
        packetData = new JsonObject();
        packetData.add(FIELD_TYPE, type.ordinal());
        packetData.add(FIELD_SUCCESSFUL, successful);
    }

    protected void unpack(){
        int typeId = packetData.get(FIELD_TYPE).asInt();
        type = PacketType.from(typeId);
        successful = packetData.get(FIELD_SUCCESSFUL).asBoolean();
    }

    public void setSuccessful(boolean successful){
        this.successful = successful;
    }

    public PacketType getType(){
        return type;
    }

    public boolean wasSuccessful(){
        return successful;
    }

    @Override
    public String toString(){
        pack();
        return packetData.toString();
    }

}
