package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXRippler;
import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.MessageType;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.ImageFileMessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.RawFileMessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.MessageController;
import de.thu.inf.spro.chattitude.desktop_client.ui.controller.TextMessageController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
    
    private int sessionuserId;
    private ContextMenu contextMenu;
    
    private MenuItem replyMenuItem;

    public ChatMessageCell(int sessionUserId, DownloadManager downloadManager) {
        textMessageController = new TextMessageController();
        rawFileMessageController = new RawFileMessageController(downloadManager);
        imageFileMessageController = new ImageFileMessageController(downloadManager);
        
        this.sessionuserId = sessionUserId;
        contextMenu = new ContextMenu();
        
        createMenuItems();
        setContextMenu(contextMenu);
    }
    
    private void createMenuItems(){
        Label replyMenuItemLabel = new Label("Reply");
        replyMenuItemLabel.setPrefWidth(100);
        replyMenuItem = new MenuItem();
        replyMenuItem.setGraphic(replyMenuItemLabel);
        replyMenuItem.setOnAction((ActionEvent e) -> {
            System.out.println("reply");
        });
    }
    
    private void setDefaultContextMenuItems(){
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(replyMenuItem);
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
        else if(message.getType() == MessageType.RAW_FILE) controller = rawFileMessageController;
        else if(message.getType() == MessageType.IMAGE_FILE) controller = imageFileMessageController;
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
        gridPane.getColumnConstraints().addAll(colConst40, colConst20, colConst40);
        gridPane.add(node, columnIndex, 0);
        return gridPane;
    }
    
}
