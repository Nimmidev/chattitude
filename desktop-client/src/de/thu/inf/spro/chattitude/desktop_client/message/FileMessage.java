package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public abstract class FileMessage extends ChatMessage {
    
    private static final String FIELD_FILENAME = "filename";
    
    private String filename;
    
    public FileMessage(MessageType messageType,  int conversationId, String text, String filename, byte[] data) {
        super(messageType, conversationId, text, data);
        
        this.filename = filename;
        content.add(FIELD_FILENAME, filename);
    }

    FileMessage(MessageType messageType, Message message, JsonObject json) {
        super(messageType, message, json);
        filename = json.getString(FIELD_FILENAME, "FILE");
    }
    
    @Override
    public String getPreview() {
        if(text.isEmpty()) return filename;
        return text;
    }
    
    public String getFileId(){
        if(message == null) throw new IllegalStateException("This is a local message it doesn't have a message object");
        return message.getFileId();
    }
    
    public String getFilename(){
        return filename;
    }
    
}
