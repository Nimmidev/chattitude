package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;

public abstract class ChatMessage {
    
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_TYPE = "type";
    
    private MessageType type;
    private int conversationId;
    
    protected Message message;
    protected byte[] data;
    protected String text;
    protected JsonObject content;
    
    ChatMessage(MessageType type, int conversationId, String text){
        this(type, conversationId, text, null);
    }

    ChatMessage(MessageType type, int conversationId, String text, byte[] data){
        this.type = type;
        this.conversationId = conversationId;
        this.data = data;

        setContent(text);
    }
    
    ChatMessage(MessageType type, Message message, JsonObject json){
        this.type = type;
        this.conversationId = message.getConversationId();
        this.message = message;
        this.data = message.getData();
        
        String text = json.getString(FIELD_TEXT, "");
        setContent(text);
    }
    
    private void setContent(String text){
        content = new JsonObject();
        content.add(FIELD_TEXT, text);
        content.add(FIELD_TYPE, type.ordinal());
        this.text = text;
    }

    public static ChatMessage of(Message message){
        JsonObject json = Json.parse(message.getContent()).asObject();
        MessageType type = MessageType.from(json.getInt(FIELD_TYPE, 0));
        
        if(type == MessageType.TEXT) return new TextMessage(message, json);
        else if(type == MessageType.FILE) return new FileMessage(message, json);
        else throw new IllegalStateException("Invalid message type: " + type.name());
    }
    
    public abstract String getPreview();
    
    public String getText(){
        return text;
    }
    
    public MessageType getType(){
        return type;
    }
    
    public Message asMessage(){
        if(message != null) return message;
        message = new Message(conversationId, content.toString(), null, data);
        return message;
    }
    
}
