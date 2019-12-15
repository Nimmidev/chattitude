package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class Conversation {

    private static final String FIELD_ID = "id";
    private static final String FIELD_LAST_ACTIVITY = "lastActivity";
    private static final String FIELD_MESSAGE = "message";

    private int id;
    private long lastActivity;
    private Message message;

    public Conversation(JsonObject json){
        id = json.get(FIELD_ID).asInt();
        lastActivity = json.get(FIELD_LAST_ACTIVITY).asLong();
        message = new Message(json.get(FIELD_MESSAGE).asObject());
    }

    public Conversation(int id, long lastActivity, Message message){
        this.id = id;
        this.lastActivity = lastActivity;
        this.message = message;
    }

    public int getId(){
        return id;
    }

    public long getLastActivity(){
        return lastActivity;
    }

    public Message getMessage(){
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public JsonObject asJson(){
        JsonObject json = new JsonObject();

        json.add(FIELD_ID, id);
        json.add(FIELD_LAST_ACTIVITY, lastActivity);
        json.add(FIELD_MESSAGE, message.asJson());

        return json;
    }

}
