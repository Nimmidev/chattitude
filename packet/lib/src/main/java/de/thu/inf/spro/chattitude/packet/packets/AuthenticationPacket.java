package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class AuthenticationPacket extends RegisterPacket {

    public AuthenticationPacket(JsonObject packetData) {
        super(packetData);
    }

    public AuthenticationPacket(Credentials credentials){
        super(credentials);
        type = PacketType.AUTHENTICATE;
    }

}
