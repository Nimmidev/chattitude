package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;

public class TextMessage extends ChatMessage {
    
    public TextMessage(int conversationId, String text) {
        super(MessageType.TEXT, conversationId, text);
    }
    
    TextMessage(Message message, JsonObject json){
        super(MessageType.TEXT, message, json);
    }

    @Override
    public String getPreview() {
        return text;
    }
}
