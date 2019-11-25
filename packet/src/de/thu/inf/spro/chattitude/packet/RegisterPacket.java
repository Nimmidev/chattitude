package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class RegisterPacket extends Packet {

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_SUCCESS = "successful";

    private String username;
    private String password;
    private boolean successful;

    public RegisterPacket(JsonObject packetData) {
        super(packetData);
    }

    public RegisterPacket(String username, String password) {
        this(username, password, false);
    }

    public RegisterPacket(String username, String password, boolean successful){
        super(PacketType.REGISTER);

        this.username = username;
        this.password = password;
        this.successful = successful;
    }

    @Override
    protected void pack(){
        super.pack();

        packetData.add(FIELD_USERNAME, username);
        packetData.add(FIELD_PASSWORD, password);
        packetData.add(FIELD_SUCCESS, successful);
    }

    @Override
    protected void unpack(){
        super.unpack();

        username = packetData.get(FIELD_USERNAME).asString();
        password = packetData.get(FIELD_PASSWORD).asString();
        successful = packetData.get(FIELD_SUCCESS).asBoolean();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public boolean isSuccessful(){
        return successful;
    }

    public Credentials asCredentials(){
        return new Credentials(username, password, successful);
    }

}
