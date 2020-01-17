package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.eclipsesource.json.JsonObject;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.MessageType;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.FileMessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.TextMessageController;

public class ChatMessageCell extends JFXListCell<ChatMessage> {
    
    private TextMessageController textMessageController;
    private FileMessageController fileMessageController;

    public ChatMessageCell(DownloadManager downloadManager) {
        textMessageController = new TextMessageController();
        fileMessageController = new FileMessageController(downloadManager);
    }

    @Override
    protected void updateItem(ChatMessage message, boolean empty) {
        super.updateItem(message, empty);

        setText(null);

        if (empty) {
            setGraphic(null);
            return;
        }

        MessageController controller;
        
        if(message.getType() == MessageType.TEXT) controller = textMessageController;
        else if(message.getType() == MessageType.FILE) controller = fileMessageController;
        else throw new IllegalStateException("Invalid message type: " + message.getType().name());

        controller.update(message);
        setGraphic(controller.getNode());

    }
    
}
