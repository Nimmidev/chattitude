package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public interface MessageController {
    
    void update(ChatMessage chatMessage, ContextMenu contextMenu);
    Node getNode();
    
}
