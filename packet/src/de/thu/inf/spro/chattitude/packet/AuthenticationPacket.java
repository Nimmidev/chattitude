package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class AuthenticationPacket extends RegisterPacket {

    public AuthenticationPacket(JsonObject packetData) {
        super(packetData);
    }

    public AuthenticationPacket(String username, String password) {
        super(username, password);
        type = PacketType.AUTHENTICATE;
    }

    public AuthenticationPacket(String username, String password, boolean successful) {
        super(username, password, successful);
        type = PacketType.AUTHENTICATE;
    }

}
