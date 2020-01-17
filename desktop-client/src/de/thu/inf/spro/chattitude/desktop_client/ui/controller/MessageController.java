package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import javafx.scene.Node;

public interface MessageController {
    
    void update(ChatMessage message);
    Node getNode();
    
}
