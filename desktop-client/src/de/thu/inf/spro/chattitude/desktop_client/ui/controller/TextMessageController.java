package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.TextMessage;
import de.thu.inf.spro.chattitude.desktop_client.ui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMessageController implements MessageController {
    
    private static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    private static final Pattern REGEX_PATTERN = Pattern.compile(URL_REGEX);
    
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
        senderLabel.setText(message.asMessage().getUser().getName());
        //contentLabel.setText(message.getText());
        parseText(message.getText());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }

    private void parseText(String text){
        Matcher matcher = REGEX_PATTERN.matcher(text);
        List<Node> nodes = new ArrayList<>();
        int lastIndex = 0;
        
        while(matcher.find()){
            System.out.println(matcher.group() + ", start: " + matcher.start() + ", end: " + matcher.end());
            Label label = createLabel(text.substring(lastIndex, matcher.start()));
            Hyperlink link = createHyperlink(text.substring(matcher.start(), matcher.end()));
            nodes.add(label);
            nodes.add(link);
            lastIndex = matcher.end();
        }
        
        if(nodes.size() == 0) nodes.add(createLabel(text));
        contentLabel.getChildren().clear();
        contentLabel.getChildren().addAll(nodes);
    }
    
    private Label createLabel(String text){
        Label label = new Label();
        label.setText(text);
        label.setTextFill(Color.BLACK);
        label.setWrapText(true);
        return label;
    }
    
    private Hyperlink createHyperlink(String url){
        Hyperlink hyperlink = new Hyperlink(url);
        hyperlink.setWrapText(true);
        hyperlink.setOnAction((ActionEvent e) -> {
            App.getInstance().getHostServices().showDocument(hyperlink.getText());
        });
        
        return hyperlink;
    }

}
