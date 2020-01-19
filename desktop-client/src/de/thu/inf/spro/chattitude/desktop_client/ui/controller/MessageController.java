package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.ui.App;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageController {

    private static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    private static final Pattern REGEX_PATTERN = Pattern.compile(URL_REGEX);
    
    private String lastText = "";
    
    public abstract void update(ChatMessage chatMessage, ContextMenu contextMenu);
    public abstract Node getNode();
    
    protected void setText(TextFlow textFlow, String text){
        if(!lastText.equals(text)){
            List<Node> nodes = parse(text);
            textFlow.getChildren().clear();
            textFlow.getChildren().addAll(nodes);
            lastText = text;
        }
    }

    public List<Node> parse(String text){
        Matcher matcher = REGEX_PATTERN.matcher(text);
        List<Node> nodes = new ArrayList<>();
        int lastIndex = 0;

        while(matcher.find()){
            Text label = createText(text.substring(lastIndex, matcher.start()));
            Text link = createHyperlink(text.substring(matcher.start(), matcher.end()));
            nodes.add(label);
            nodes.add(link);
            lastIndex = matcher.end();
        }

        if(nodes.size() == 0) nodes.add(createText(text));
        else nodes.add(createText(text.substring(lastIndex)));

        return nodes;
    }

    private Text createText(String str){
        Text text = new Text(str);
        return text;
    }

    private Text createHyperlink(String url){
        Text hyperlink = new Text(url);
        hyperlink.setStyle("-fx-fill: #0096c9; -fx-cursor: hand;");
        hyperlink.setOnMouseClicked((MouseEvent e) -> {
            App.getInstance().getHostServices().showDocument(hyperlink.getText());
        });

        return hyperlink;
    }


}
