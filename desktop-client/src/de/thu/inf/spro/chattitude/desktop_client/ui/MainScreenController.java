package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXTextArea;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.CreateConversationPacket;
import de.thu.inf.spro.chattitude.packet.packets.GetConversationsPacket;
import de.thu.inf.spro.chattitude.packet.packets.MessagePacket;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.util.Arrays;

public class MainScreenController {

    @FXML
    private JFXTextArea messageField;

    private Client client;
    private int selectedConversation;

    public MainScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        client.setOnMessage(System.out::println);
        client.setOnConversations(conversations -> {
            System.out.println(Arrays.toString(conversations));
        });
        client.send(new GetConversationsPacket());
    }

    public void exitWindow() {
        Platform.exit();
    }

    public void newChat() {
        CreateConversationPacket packet = new CreateConversationPacket(1);
        client.send(packet);
        client.setOnConversationCreated(conversationId -> {
            selectedConversation = conversationId;
            System.out.println("created " + conversationId);
        });
    }

    public void sendMessage() {
        System.out.println("Send");
        Message message = new Message(selectedConversation, messageField.getText(), null);

        client.send(new MessagePacket(message));
    }

}
