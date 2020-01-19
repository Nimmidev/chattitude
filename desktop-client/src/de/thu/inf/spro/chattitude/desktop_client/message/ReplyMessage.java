package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class ReplyMessage extends ChatMessage {
    
    private static final String FIELD_REPLY_MSG_TXT = "replayMsgTxt";
    
    private String replyMsgTxt;
    
    public ReplyMessage(int conversationId, String replyMsgTxt, String text) {
        super(MessageType.REPLY, conversationId, text);
        
        this.replyMsgTxt = replyMsgTxt;
        content.add(FIELD_REPLY_MSG_TXT, replyMsgTxt);
    }

    ReplyMessage(Message message, JsonObject json) {
        super(MessageType.REPLY, message, json);
        
        replyMsgTxt = json.getString(FIELD_REPLY_MSG_TXT, "");
    }

    @Override
    public String getPreview() {
        return text;
    }
    
    public String getReplyMsgTxt(){
        return replyMsgTxt;
    }
    
}
