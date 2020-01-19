package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class AudioMessage extends FileMessage {
    
    public AudioMessage(int conversationId, String text, String filename, byte[] data) {
        super(MessageType.AUDIO, conversationId, text, filename, data);
    }

    AudioMessage(Message message, JsonObject json) {
        super(MessageType.AUDIO, message, json);
    }

}
