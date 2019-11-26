package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class RegisterPacket extends Packet {

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_AUTHENTICATED = "successful";

    private Credentials credentials;

    public RegisterPacket(JsonObject packetData) {
        super(packetData);
    }

    public RegisterPacket(Credentials credentials){
        super(PacketType.REGISTER);

        this.credentials = credentials;
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.add(FIELD_USER_ID, credentials.getUserId());
        packetData.add(FIELD_USERNAME, credentials.getUsername());
        packetData.add(FIELD_PASSWORD, credentials.getPassword());
        packetData.add(FIELD_AUTHENTICATED, credentials.isAuthenticated());
    }

    @Override
    protected void unpack(){
        super.unpack();

        int userId = packetData.get(FIELD_USER_ID).asInt();
        String username = packetData.get(FIELD_USERNAME).asString();
        String password = packetData.get(FIELD_PASSWORD).asString();
        boolean authenticated = packetData.get(FIELD_AUTHENTICATED).asBoolean();

        credentials = new Credentials(userId, username, password, authenticated);
    }

    public void setCredentials(Credentials credentials){
        this.credentials = credentials;
    }

    public Credentials getCredentials(){
        return credentials;
    }


}
