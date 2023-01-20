package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import de.thu.inf.spro.chattitude.packet.PacketType;

import java.util.Base64;

public class GetAttachmentPacket extends Packet {

    private static final String FIELD_FILE_ID = "fileId";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_REQUEST_IDENTIFIER = "requestIdentifier";

    private String fileId;
    private String requestIdentifier;
    private byte[] data;

    public GetAttachmentPacket(JsonObject packetData) {
        super(packetData);
    }

    public GetAttachmentPacket(String fileId, String requestIdentifier){
        super(PacketType.GET_ATTACHMENT);

        this.fileId = fileId;
        this.requestIdentifier = requestIdentifier;
        
        data = new byte[]{};
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_FILE_ID, fileId);
        packetData.add(FIELD_DATA, Base64.getEncoder().encodeToString(data));
        packetData.add(FIELD_REQUEST_IDENTIFIER, requestIdentifier);
    }

    @Override
    protected void unpack() {
        super.unpack();

        fileId = packetData.get(FIELD_FILE_ID).asString();
        requestIdentifier = packetData.get(FIELD_REQUEST_IDENTIFIER).asString();
        data = Base64.getDecoder().decode(packetData.get(FIELD_DATA).asString());
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public String getFieldId(){
        return fileId;
    }

    public String getRequestIdentifier(){
        return requestIdentifier;
    }
    
    public byte[] getData(){
        return data;
    }

}
