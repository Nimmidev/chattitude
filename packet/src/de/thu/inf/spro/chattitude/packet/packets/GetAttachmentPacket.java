package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.Base64;

public class GetAttachmentPacket extends Packet {

    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_DATA = "data";

    private String fieldId;
    private byte[] data;

    public GetAttachmentPacket(JsonObject packetData) {
        super(packetData);
    }

    public GetAttachmentPacket(String uuid){
        super(PacketType.GET_ATTACHMENT);

        this.fieldId = uuid;
        data = new byte[]{};
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_UUID, fieldId);
        packetData.add(FIELD_DATA, Base64.getEncoder().encodeToString(data));
    }

    @Override
    protected void unpack() {
        super.unpack();

        fieldId = packetData.get(FIELD_UUID).asString();
        data = Base64.getDecoder().decode(packetData.get(FIELD_DATA).asString());
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public String getFieldId(){
        return fieldId;
    }

    public byte[] getData(){
        return data;
    }

}
