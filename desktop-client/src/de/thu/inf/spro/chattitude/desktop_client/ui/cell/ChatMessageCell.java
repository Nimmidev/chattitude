package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXRippler;
import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.MessageType;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class ChatMessageCell extends JFXListCell<ChatMessage> {

    private static final ColumnConstraints colConst40;
    private static final ColumnConstraints colConst20;
    
    private static final String NODE_STYLE = "-fx-background-color: white; -fx-padding: 5px 5px 10px 10px;" +
            "-fx-background-radius: 4px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 1, 1, 1, 1);" +
            "-fx-border-width: 1; -fx-border-color: rgba(0, 0, 0, 0.3); -fx-border-style: solid; -fx-border-radius: 4px;";
    
    static {
        colConst40 = new ColumnConstraints();
        colConst20 = new ColumnConstraints();
        
        colConst40.setPercentWidth(40);
        colConst20.setPercentWidth(20);
    }
    
    private TextMessageController textMessageController;
    private RawFileMessageController rawFileMessageController;
    private ImageFileMessageController imageFileMessageController;
    private ReplyMessageController replyMessageController;
    private YoutubeVideoMessageController youtubeVideoMessageController;
    private AudioMessageController audioMessageController;
    
    private MainScreenController mainScreenController;
    
    private int sessionuserId;
    private ContextMenu contextMenu;
    
    private MenuItem replyMenuItem;
    private MenuItem copyMenuItem;
    
    private ChatMessage currentMessage;

    public ChatMessageCell(int sessionUserId, DownloadManager downloadManager, MainScreenController mainScreenController) {
        textMessageController = new TextMessageController();
        rawFileMessageController = new RawFileMessageController(downloadManager);
        imageFileMessageController = new ImageFileMessageController(downloadManager);
        replyMessageController = new ReplyMessageController();
        youtubeVideoMessageController = new YoutubeVideoMessageController();
        audioMessageController = new AudioMessageController(downloadManager);
        this.mainScreenController = mainScreenController;
        
        this.sessionuserId = sessionUserId;
        contextMenu = new ContextMenu();
        
        createMenuItems();
        setContextMenu(contextMenu);
    }


    public static MenuItem createMenuItem(String name, EventHandler<ActionEvent> eventHandler){
        Label menuItemLabel = new Label(name);
        menuItemLabel.setPrefWidth(100);
        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(menuItemLabel);
        menuItem.setOnAction(eventHandler);

        return menuItem;
    }
    
    private void createMenuItems(){
        replyMenuItem = createMenuItem("Reply", (ActionEvent e) -> {
            if(!currentMessage.getText().isEmpty()){
                mainScreenController.setReplyMessage(currentMessage);
            }
        });
        copyMenuItem = createMenuItem("Copy", (ActionEvent e) ->{
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(currentMessage.getText());
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        });

    }
    
    private void setDefaultContextMenuItems(){
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(replyMenuItem,copyMenuItem);
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
        currentMessage = message;
        
        if(message.getType() == MessageType.TEXT) controller = textMessageController;
        else if(message.getType() == MessageType.RAW_FILE) controller = rawFileMessageController;
        else if(message.getType() == MessageType.IMAGE_FILE) controller = imageFileMessageController;
        else if(message.getType() == MessageType.REPLY) controller = replyMessageController;
        else if(message.getType() == MessageType.YOUTUBE_VIDEO) controller = youtubeVideoMessageController;
        else if(message.getType() == MessageType.AUDIO) controller = audioMessageController;
        else throw new IllegalStateException("Invalid message type: " + message.getType().name());

        setDefaultContextMenuItems();
        controller.update(message, contextMenu);
        
        Node node = controller.getNode();
        int columnIndex = message.asMessage().getUser().getId() == sessionuserId ? 2 : 0;
        
        node.setStyle(NODE_STYLE);
        setStyle("-fx-padding: 10px 10px 0px 10px; -fx-control-inner-background: transparent;");
        
        GridPane gridPane = wrapNodeInGridPane(node, columnIndex);
        setGraphic(gridPane);
        cellRippler = new JFXRippler();
    }
    
    private GridPane wrapNodeInGridPane(Node node, int columnIndex){
        GridPane gridPane = new GridPane();
        gridPane.setMinWidth(50);
        gridPane.setPrefWidth(50);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.getColumnConstraints().addAll(colConst40, colConst20, colConst40);
        gridPane.add(node, columnIndex, 0);
        return gridPane;
    }
    
}
