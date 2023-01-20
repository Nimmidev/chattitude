package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class MessagePacket extends Packet {

    private Message message;

    public MessagePacket(JsonObject packetData) {
        super(packetData);
    }

    public MessagePacket(Message message) {
        super(PacketType.MESSAGE);

        this.message = message;
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.merge(message.asJson());
    }

    @Override
    protected void unpack(){
        super.unpack();

        message = new Message(packetData);
    }

    public Message getMessage(){
        return message;
    }

}
