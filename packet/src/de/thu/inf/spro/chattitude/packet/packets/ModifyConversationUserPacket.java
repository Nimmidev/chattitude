package de.thu.inf.spro.chattitude.packet.packets;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.PacketType;

public class ModifyConversationUserPacket extends Packet {

    private static final String FIELD_CONVERSATION_ID = "conversationId";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_ACTION = "action";

    private Action action;
    private int conversationId;
    private int userId;

    public enum Action {
        ADD, REMOVE, PROMOTE_ADMIN, DEMOTE_ADMIN;

        private static Action[] _values = values();

        public static Action from(int i){
            if(i < _values.length) return _values[i];
            throw new IndexOutOfBoundsException("Invalid Action enum index");
        }
    }

    public ModifyConversationUserPacket(JsonObject packetData) {
        super(packetData);
    }

    public ModifyConversationUserPacket(Action action, int userId, int conversationId) {
        super(PacketType.MODIFY_CONVERSATION_USER);

        this.action = action;
        this.conversationId = conversationId;
        this.userId = userId;
    }

    @Override
    protected void pack() {
        super.pack();

        packetData.add(FIELD_CONVERSATION_ID, conversationId);
        packetData.add(FIELD_USER_ID, userId);
        packetData.add(FIELD_ACTION, action.ordinal());
    }

    @Override
    protected void unpack() {
        super.unpack();

        conversationId = packetData.get(FIELD_CONVERSATION_ID).asInt();
        userId = packetData.get(FIELD_USER_ID).asInt();

        int actionId = packetData.get(FIELD_ACTION).asInt();
        action = Action.from(actionId);
    }

    public int getConversationId(){
        return conversationId;
    }

    public int getUserId(){
        return userId;
    }

    public Action getAction(){
        return action;
    }

}
