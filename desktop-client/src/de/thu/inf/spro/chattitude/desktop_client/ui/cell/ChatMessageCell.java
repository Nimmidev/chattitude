package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.eclipsesource.json.JsonObject;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.MessageType;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.FileMessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.TextMessageController;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class ChatMessageCell extends JFXListCell<ChatMessage> {
    
    private TextMessageController textMessageController;
    private FileMessageController fileMessageController;
    
    private ContextMenu contextMenu;

    public ChatMessageCell(DownloadManager downloadManager) {
        textMessageController = new TextMessageController();
        fileMessageController = new FileMessageController(downloadManager);
        contextMenu = new ContextMenu();

        Label replyMenuItemLabel = new Label("Reply");
        replyMenuItemLabel.setPrefWidth(100);
        MenuItem replyMenuItem = new MenuItem();
        replyMenuItem.setGraphic(replyMenuItemLabel);
        replyMenuItem.setOnAction((ActionEvent e) -> {
            System.out.println("reply");
        });
        
        contextMenu.getItems().addAll(replyMenuItem);
        setContextMenu(contextMenu);
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
