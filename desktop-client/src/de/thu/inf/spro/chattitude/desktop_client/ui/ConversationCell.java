package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.packet.Conversation;

public class ConversationCell extends JFXListCell<Conversation> {

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);

        if (empty) {
            setText(null);
        } else {
            String text = conversation.getName();
            if (conversation.getMessage() != null) {
                text += "\n" + conversation.getMessage().getUser().getName() + ": " + conversation.getMessage().getContent();
            }
            setText(text);
        }

    }
}
