package de.thu.inf.spro.chattitude.packet;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

public class Conversation {

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_USERS = "users";
    private static final String FIELD_ADMINS = "admins";

    private int id;
    private String name;
    private Message message;
    private User[] users;
    private int[] admins;

    public Conversation(JsonObject json){
        id = json.get(FIELD_ID).asInt();

        if(json.get(FIELD_NAME) != null) name = json.get(FIELD_NAME).asString();
        else name = null;

        if(json.get(FIELD_MESSAGE) != null) message = new Message(json.get(FIELD_MESSAGE).asObject());
        else message = null;

        List<User> userList = new ArrayList<>();
        json.get(FIELD_USERS).asArray().iterator().forEachRemaining(v -> userList.add(new User(v.asObject())));
        users = new User[userList.size()];
        users = userList.toArray(users);

        admins = StreamSupport.stream(json.get(FIELD_ADMINS).asArray().spliterator(), false)
                .mapToInt(JsonValue::asInt)
                .toArray();
    }

    public Conversation(String name, User[] users){
        this(-1, name, null, users, new int[]{});
    }

    public Conversation(User user){
        this(-1, null, null, new User[]{user}, new int[]{user.getId()});
    }

    public Conversation(int id, String name, Message message){
        this(id, name, message, new User[]{}, new int[]{});
    }

    public Conversation(int id, String name, Message message, User[] users, int[] admins){
        this.id = id;
        this.name = name;
        this.message = message;
        this.users = users;
        this.admins = admins;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Message getMessage(){
        return message;
    }

    public User[] getUsers(){
        return users;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setUsers(List<User> userList){
        users = new User[userList.size()];
        users = userList.toArray(users);
    }
    public void setMessage(Message message) {
        this.message = message;
    }

    public int[] getAdmins() {
        return admins;
    }

    public void setAdmins(List<Integer> admins) {
        this.admins = admins.stream().mapToInt(value -> value).toArray();
    }

    public boolean isAdmin(int userId) {
        for (int admin : getAdmins()) {
            if (admin == userId)
                return true;
        }
        return false;
    }

    public JsonObject asJson(){
        JsonObject json = new JsonObject();

        json.add(FIELD_ID, id);
        if(name != null) json.add(FIELD_NAME, name);
        if(message != null) json.add(FIELD_MESSAGE, message.asJson());
        JsonArray usersArray = new JsonArray();
        Arrays.stream(users).map(User::asJson).forEach(usersArray::add);
        json.add(FIELD_USERS, usersArray);

        JsonArray adminsArray = new JsonArray();
        Arrays.stream(admins).forEach(adminsArray::add);
        json.add(FIELD_ADMINS, adminsArray);

        return json;
    }

}
