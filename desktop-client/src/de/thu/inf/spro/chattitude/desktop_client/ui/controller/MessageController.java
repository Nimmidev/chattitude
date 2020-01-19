package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public abstract class MessageController {
    
    public abstract void update(ChatMessage chatMessage, ContextMenu contextMenu);
    public abstract Node getNode();
    
    protected MenuItem createMenuItem(String name, EventHandler<ActionEvent> eventHandler){
        Label menuItemLabel = new Label(name);
        menuItemLabel.setPrefWidth(100);
        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(menuItemLabel);
        menuItem.setOnAction(eventHandler);
        
        return menuItem;
    }
    
}
