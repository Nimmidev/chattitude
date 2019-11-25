package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class Packet {

    private static final String FIELD_TYPE = "type";

    PacketType type;
    JsonObject packetData;

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
        else if(type == PacketType.REGISTER) return new RegisterPacket(packetData);
        else if(type == PacketType.AUTHENTICATE) return new AuthenticationPacket(packetData);
        else if(type == PacketType.MESSAGE) return new MessagePacket(packetData);
        else if(type == PacketType.MESSAGE_HISTORY) return new MessageHistoryPacket(packetData);
        else if(type == PacketType.CONVERSATIONS) return new ConversationsPacket(packetData);
        else if(type == PacketType.ATTACHMENT) return new AttachmentPacket(packetData);
        else throw new IllegalStateException("Invalid package type: " + type.name());
    }

    protected void pack(){
        packetData = new JsonObject();
        packetData.add(FIELD_TYPE, type.ordinal());
    }

    protected void unpack(){
        int typeId = packetData.get("type").asInt();
        type = PacketType.from(typeId);
    }

    public PacketType getType(){
        return type;
    }

    @Override
    public String toString(){
        pack();
        return packetData.toString();
    }

}
