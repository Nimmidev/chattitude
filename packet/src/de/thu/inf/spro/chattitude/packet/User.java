package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonObject;

public class User {

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";

    private int id;
    private String name;

    public User(JsonObject json){
        id = json.get(FIELD_ID).asInt();
        name = json.get(FIELD_NAME).asString();
    }

    public User(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public JsonObject asJson(){
        JsonObject json = new JsonObject();

        json.add(FIELD_ID, id);
        json.add(FIELD_NAME, name);

        return json;
    }

}