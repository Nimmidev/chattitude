package de.thu.inf.spro.chattitude.desktop_client.message;

public enum MessageType {
    
    INVALID,
    TEXT,
    RAW_FILE,
    IMAGE_FILE,
    REPLY,
    YOUTUBE_VIDEO,
    AUDIO;


    private static MessageType[] _values = values();

    public static MessageType from(int i){
        if(i < _values.length) return _values[i];
        return MessageType.INVALID;
    }
    
}
