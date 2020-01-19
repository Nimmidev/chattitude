package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import de.thu.inf.spro.chattitude.desktop_client.FileType;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;

public abstract class ChatMessage {
    
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_TYPE = "type";
    
    private MessageType type;
    private int conversationId;
    
    protected JsonObject content;
    protected Message message;
    protected String text;
    
    protected byte[] data;
    
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
        try {
            JsonObject json = Json.parse(message.getContent()).asObject();
            MessageType type = MessageType.from(json.getInt(FIELD_TYPE, 0));

            if (type == MessageType.TEXT) return new TextMessage(message, json);
            else if (type == MessageType.RAW_FILE) return new RawFileMessage(message, json);
            else if(type == MessageType.IMAGE_FILE) return new ImageFileMessage(message, json);
            else if(type == MessageType.REPLY) return new ReplyMessage(message, json);
            else if(type == MessageType.YOUTUBE_VIDEO) return new YoutubeVideoMessage(message, json);
            else throw new IllegalStateException("Invalid message type: " + type.name());
        } catch(ParseException | IllegalStateException exception) {
            System.err.println("Error parsing message");
            exception.printStackTrace();
            return new TextMessage(message, new JsonObject());
        }
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
