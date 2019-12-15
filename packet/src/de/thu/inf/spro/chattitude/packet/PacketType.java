package de.thu.inf.spro.chattitude.packet;

public enum PacketType {

    INVALID,
    UNAUTHORIZED,
    CONNECTED,
    REGISTER,
    AUTHENTICATE,
    MESSAGE,
    MESSAGE_HISTORY,
    GET_CONVERSATIONS,
    CREATE_CONVERSATION,
    CONVERSATION_UPDATED,
    MODIFY_CONVERSATION_USER,
    GET_ATTACHMENT,
    SEARCH_USER;

    private static PacketType[] _values = values();

    public static PacketType from(int i){
        if(i < _values.length) return _values[i];
        return PacketType.INVALID;
    }

}