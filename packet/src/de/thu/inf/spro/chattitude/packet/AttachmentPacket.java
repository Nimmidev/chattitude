package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class AttachmentPacket extends Packet {

    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_DATA = "data";

    private String uuid;
    private String data;

    public AttachmentPacket(JsonObject packetData) {
        super(packetData);
    }

    public AttachmentPacket(String uuid) {
        this(uuid, null);
    }

    public AttachmentPacket(String uuid, String data){
        super(PacketType.ATTACHMENT);

        this.uuid = uuid;
        this.data = data;
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_UUID, uuid);
        packetData.add(FIELD_DATA, data);
    }

    @Override
    protected void unpack() {
        super.unpack();

        uuid = packetData.get(FIELD_UUID).asString();
        JsonValue dataValue = packetData.get(FIELD_DATA);

        if(dataValue.isNull()) data = null;
        else data = dataValue.asString();
    }

    public String getUuid(){
        return uuid;
    }

    public String getData(){
        return data;
    }

}
