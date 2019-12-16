package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.packet.Message;

public class MessageCell extends JFXListCell<Message> {
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty) {
            setText(null);
        } else {
            setText(message.getUser().getName() + ": " + message.getContent());
        }
    }
}
