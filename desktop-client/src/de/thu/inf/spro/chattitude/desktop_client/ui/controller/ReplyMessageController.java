package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.ReplyMessage;
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

public class ReplyMessageController extends MessageController {
    
    @FXML
    private VBox messageCell;

    @FXML
    private Label senderLabel;

    @FXML
    private TextFlow contentLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label txtRepliedMessage;

    @FXML
    private Label txtRepliedSender;

    private FXMLLoader mLLoader;
    
    public ReplyMessageController(){
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/ReplyMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading TextMessageCell", e);
        }
    }
    
    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        ReplyMessage message = (ReplyMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());;
        setText(contentLabel, message.getText());
        txtRepliedMessage.setText(message.getReplyMsgTxt());
        txtRepliedSender.setText(message.getReplyMsgSender());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
    
}
