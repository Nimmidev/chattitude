package de.thu.inf.spro.chattitude.desktop_client.message;

import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.Message;

public class ReplyMessage extends ChatMessage {

    private static final String FIELD_REPLY_MSG_TXT = "replayMsgTxt";
    private static final String FIELD_REPLY_MSG_SENDER = "replayMsgSender";

    private String replyMsgTxt;
    private String replyMsgSender;
    
    public ReplyMessage(int conversationId, String replyMsgSender, String replyMsgTxt, String text) {
        super(MessageType.REPLY, conversationId, text);
        
        this.replyMsgTxt = replyMsgTxt;
        this.replyMsgSender = replyMsgSender;
        content.add(FIELD_REPLY_MSG_TXT, replyMsgTxt);
        content.add(FIELD_REPLY_MSG_SENDER, replyMsgSender);
    }

    ReplyMessage(Message message, JsonObject json) {
        super(MessageType.REPLY, message, json);
        
        replyMsgTxt = json.getString(FIELD_REPLY_MSG_TXT, "");
        replyMsgSender = json.getString(FIELD_REPLY_MSG_SENDER, "");
    }

    @Override
    public String getPreview() {
        return stripPreview(text);
    }
    
    public String getReplyMsgTxt(){
        return replyMsgTxt;
    }
    
    public String getReplyMsgSender(){
        return replyMsgSender;
    }
    
}
