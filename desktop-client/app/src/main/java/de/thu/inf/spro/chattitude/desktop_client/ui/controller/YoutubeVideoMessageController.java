package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.TextMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.YoutubeVideoMessage;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

import javax.script.SimpleBindings;
import java.io.IOException;
import java.util.Date;

public class YoutubeVideoMessageController extends MessageController {

    @FXML
    private VBox messageCell;

    @FXML
    private Label senderLabel;

    @FXML
    private TextFlow contentLabel;

    @FXML
    private Label timeLabel;
    
    @FXML
    private WebView webView;
    
    private FXMLLoader mLLoader;
    
    public YoutubeVideoMessageController(){
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/YoutubeVideoMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading TextMessageCell", e);
        }
    }
    
    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        YoutubeVideoMessage message = (YoutubeVideoMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());
        webView.getEngine().load(((YoutubeVideoMessage) chatMessage).getUrl());
        webView.prefHeightProperty().bind(Bindings.divide(webView.widthProperty(), 16.0 / 9));
        setText(contentLabel, message.getText());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
    
}
