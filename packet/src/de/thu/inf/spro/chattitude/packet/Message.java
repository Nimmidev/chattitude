package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

import java.util.Base64;
import java.util.Date;

public class Message {

    private static final String FIELD_ID = "id";
    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_USER = "user";
    private static final String FIELD_DATA_UUID = "dataUUID";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_DATA = "data";

    private int id;
    private int conversationId;
    private User user;
    private String fileId;
    private String content;
    private long timestamp;
    private byte[] data;

    public Message(JsonObject json){
        id = json.get(FIELD_ID).asInt();
        conversationId = json.get(FIELD_CONVERSATION_ID).asInt();
        user = null;
        fileId = json.get(FIELD_DATA_UUID).asString();
        content = json.get(FIELD_TEXT).asString();
        timestamp = json.get(FIELD_TIMESTAMP).asLong();
        data = Base64.getDecoder().decode(json.get(FIELD_DATA).asString());
    }

    public Message(int conversationId, String content, User user){
        this(conversationId, "", content, user);
    }

    public Message(int conversationId, String fileId, String content, User user){
        this(-1, conversationId, fileId, content, new Date().getTime(), user);
    }

    public Message(int id, int conversationId, String fileId, String content, long timestamp, User user){
        this(id, conversationId, fileId, content, timestamp, user, new byte[]{});
    }

    public Message(int conversationId, String content, User user, byte[] data){
        this(-1, conversationId, "", content, new Date().getTime(), user, data);
    }

    public Message(int id, int conversationId, String fileId, String content, long timestamp, User user, byte[] data){
        this.id = id;
        this.conversationId = conversationId;
        this.user = user;
        this.fileId = fileId;
        this.content = content;
        this.timestamp = timestamp;
        this.data = data;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setFieldId(String fieldId){
        this.fileId = fieldId;
    }

    public int getId(){
        return id;
    }

    public int getConversationId(){
        return conversationId;
    }

    public String getFileId(){
        return fileId;
    }

    public byte[] getData(){
        return data;
    }

    public String getContent(){
        return content;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public JsonObject asJson(){
        JsonObject json = new JsonObject();

        json.add(FIELD_ID, id);
        json.add(FIELD_CONVERSATION_ID, conversationId);
        if (user != null)
            json.add(FIELD_USER, user.asJson());
        json.add(FIELD_DATA_UUID, fileId);
        json.add(FIELD_TEXT, content);
        json.add(FIELD_TIMESTAMP, timestamp);
        json.add(FIELD_DATA, Base64.getEncoder().encodeToString(data));

        return json;
    }

}
