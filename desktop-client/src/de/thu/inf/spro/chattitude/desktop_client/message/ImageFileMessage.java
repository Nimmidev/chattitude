package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class ImageFileMessage extends FileMessage {

    public ImageFileMessage(int conversationId, String text, String filename, byte[] data) {
        super(MessageType.IMAGE_FILE, conversationId, text, filename, data);
    }

    ImageFileMessage(Message message, JsonObject json) {
        super(MessageType.IMAGE_FILE, message, json);
    }
    
}
