package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.PacketType;
import de.thu.inf.spro.chattitude.packet.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchUserPacket extends Packet {

    private static final String FIELD_QUERY = "query";
    private static final String FIELD_RESULTS = "results";

    private String query;
    private User[] users;

    public SearchUserPacket(JsonObject packetData) {
        super(packetData);
    }

    public SearchUserPacket(String query) {
        super(PacketType.SEARCH_USER);

        this.query = query;
        users = new User[]{};
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_QUERY, query);
        JsonArray usersArray = new JsonArray();
        Arrays.stream(users).map(User::asJson).forEach(v -> usersArray.add(v));
        packetData.add(FIELD_RESULTS, usersArray);
    }

    @Override
    protected void unpack() {
        super.unpack();

        List<User> userList = new ArrayList<>();

        query = packetData.get(FIELD_QUERY).asString();

        packetData.get(FIELD_RESULTS).asArray().iterator().forEachRemaining(v -> userList.add(new User(v.asObject())));
        users = new User[userList.size()];
        users = userList.toArray(users);
    }

    public void setResult(User[] users){
        this.users = users;
    }

    public String getQuery(){
        return query;
    }

    public User[] getResults(){
        return users;
    }

}
