package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.TextMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Date;

public class TextMessageController extends MessageController {
        
    @FXML
    private VBox messageCell;
    
    @FXML
    private Label senderLabel;
    
    @FXML
    private TextFlow contentLabel;
    
    @FXML
    private Label timeLabel;

    private FXMLLoader mLLoader;

    public TextMessageController() {
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/TextMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading TextMessageCell", e);
        }
    }

    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        TextMessage message = (TextMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());;
        setText(contentLabel, message.getText());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }

}
