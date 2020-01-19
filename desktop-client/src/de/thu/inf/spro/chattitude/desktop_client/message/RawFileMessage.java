package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class RawFileMessage extends FileMessage {
    
    public RawFileMessage(int conversationId, String text, String filename, byte[] data) {
        super(MessageType.RAW_FILE, conversationId, text, filename, data);
    }

    RawFileMessage(Message message, JsonObject json) {
        super(MessageType.RAW_FILE, message, json);
    }
    
}
