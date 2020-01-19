package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class YoutubeVideoMessage extends ChatMessage {
    
    private static final String FIELD_URL = "url";
    
    private String url;
    
    public YoutubeVideoMessage(int conversationId, String url, String text) {
        super(MessageType.YOUTUBE_VIDEO, conversationId, text);
        this.url = url;
        content.add(FIELD_URL, url);
    }

    YoutubeVideoMessage(Message message, JsonObject json) {
        super(MessageType.YOUTUBE_VIDEO, message, json);
        url = json.getString(FIELD_URL, "");
    }

    @Override
    public String getPreview() {
        return stripPreview(text);
    }
    
    public String getUrl(){
        return url;
    }
    
}
