package de.thu.inf.spro.chattitude.packet;

public enum PacketType {

    INVALID,
    CONNECTED,
    REGISTER,
    AUTHENTICATE,
    MESSAGE,
    MESSAGE_HISTORY,
    CONVERSATIONS,
    ATTACHMENT;

    private static PacketType[] _values = values();

    public static PacketType from(int i){
        if(i < _values.length) return _values[i];
        return PacketType.INVALID;
    }

}