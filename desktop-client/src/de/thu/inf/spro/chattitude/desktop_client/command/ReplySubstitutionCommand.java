package de.thu.inf.spro.chattitude.desktop_client.command;

import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MainScreenController;
import javafx.collections.ObservableList;

public class ReplySubstitutionCommand extends Command {

    private String url;
    
    public ReplySubstitutionCommand(String cmd, String url, String description) {
        super(cmd, description);
        this.url = url;
    }

    @Override
    public String exec(MainScreenController controller, String text) {
        ObservableList<ChatMessage> messages = controller.getMessagesOfSelectedConversation();
        ChatMessage message = messages.get(messages.size() - 1);
        controller.setReplyMessage(message);
        if(text.isEmpty()) text = message.getText();
        return url + text.replace(" ", "+");
    }
}
