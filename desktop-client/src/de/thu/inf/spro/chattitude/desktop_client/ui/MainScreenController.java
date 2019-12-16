package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.packets.CreateConversationPacket;
import de.thu.inf.spro.chattitude.packet.packets.GetConversationsPacket;
import de.thu.inf.spro.chattitude.packet.packets.MessageHistoryPacket;
import de.thu.inf.spro.chattitude.packet.packets.MessagePacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    private static final String CONVERSATION_PROPERTY = "conversation";

    @FXML
    private JFXTextArea messageField;
    @FXML
    private JFXListView<Label> conversationsList;
    @FXML
    private JFXListView<Label> messageHistoryList;
    @FXML
    public JFXTextField txtNewPartner;

    private Client client;
    private Conversation selectedConversation;

    public MainScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        client.setOnMessage(message -> Platform.runLater(() -> {
            int conversationId = message.getConversationId();
            if (selectedConversation != null && conversationId == selectedConversation.getId()) {
                messageHistoryList.getItems().add(createMessageItem(message));
            }
            for (Label cell : conversationsList.getItems()) {
                Conversation conversation = (Conversation) cell.getProperties().get(CONVERSATION_PROPERTY);
                if (conversation.getId() == conversationId) {
                    conversation.setMessage(message);
                    updateConversationItem(conversation, cell);
                    return;
                }
            }
            System.out.println("Warning: Message for unknown conversation " + conversationId);
        }));
        client.setOnConversationUpdated(newConversation -> {
            for (Label cell : conversationsList.getItems()) {
                Conversation oldConversation = (Conversation) cell.getProperties().get(CONVERSATION_PROPERTY);
                if (newConversation.getId() == oldConversation.getId()) {
                    cell.getProperties().put(CONVERSATION_PROPERTY, newConversation);
                    updateConversationItem(newConversation, cell);
                    return;
                }
            }

            Label cell = createConversationItem(newConversation);
            conversationsList.getItems().add(0, cell);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client.setOnConversations(conversations -> Platform.runLater(() -> {
            for (Conversation conversation : conversations) {
                conversationsList.getItems().add(createConversationItem(conversation));
            }
        }));
        client.send(new GetConversationsPacket());
        conversationsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCell) -> {
            selectedConversation = ((Conversation) selectedCell.getProperties().get(CONVERSATION_PROPERTY));
            showMessages();
        });
    }

    private Label createConversationItem(Conversation conversation) {
        Label cell = new Label();
        cell.getProperties().put(CONVERSATION_PROPERTY, conversation);
        updateConversationItem(conversation, cell);
        return cell;
    }

    private void updateConversationItem(Conversation conversation, Label cell) {
        String text = "Id: " + conversation.getId();
        if (conversation.getMessage() != null) {
            text += "\n" + conversation.getMessage().getUser().getName() + ": " + conversation.getMessage().getContent();
        }
        cell.setText(text);
    }

    private Label createMessageItem(Message message) {
        Label cell = new Label(message.getUser().getName() + ": " + message.getContent());
        return cell;
    }

    private void showMessages() {
        System.out.println("Show messages for " + selectedConversation);
        client.setOnMessageHistory(packet -> {
            client.setOnMessageHistory(null);
            Platform.runLater(() -> {
                if (selectedConversation.getId() != packet.getConversationId()) // TOdo offset auch checken
                    return;
                for (Message message : packet.getMessages()) {
                    Label cell = createMessageItem(message);
                    messageHistoryList.getItems().add(0, cell);
                }
            });
        });
        int lastMessageId = -1;
        if (selectedConversation.getMessage() != null)
            lastMessageId = selectedConversation.getMessage().getId();
        client.send(new MessageHistoryPacket(selectedConversation.getId(), lastMessageId));

        messageHistoryList.getItems().clear();

        if (selectedConversation.getMessage() != null) {
            Label cell = createMessageItem(selectedConversation.getMessage());
            messageHistoryList.getItems().add(0, cell);
        }
    }

    public void exitWindow() {
        Platform.exit();
    }

    public void newChat() {
        // TODO

        /*
        String newChatPartner = txtNewPartner.getText();
        SearchUserPacket packet = new SearchUserPacket(newChatPartner);
        User[] name = packet.getResults();
        System.out.println(newChatPartner);
        System.out.println(name.length);
        System.out.println(name[0]);
        System.out.println(name[1]);

        for (int i = 0; i < name.length; i++) {
            if (name[i].getName() == newChatPartner) {
                System.out.println(name[i].getName());
                System.out.println(name[i].getId());
            } else {
                System.out.println("Nix gefunden");
            }
        }
        */



        CreateConversationPacket packet = new CreateConversationPacket(1,2);
        client.send(packet);
        client.setOnConversationCreated(conversationId -> Platform.runLater(() -> {
            Conversation dummyConversation = new Conversation(conversationId, 0, null);
            Label cell = createConversationItem(dummyConversation);
            conversationsList.getItems().add(0, cell);
            conversationsList.getSelectionModel().select(cell);
            selectedConversation = dummyConversation;
            System.out.println("created " + conversationId);
        }));


    }

    public void sendMessage() {
        System.out.println("Send");
        Message message = new Message(selectedConversation.getId(), messageField.getText(), null);

        client.send(new MessagePacket(message));
    }

    public void addToGroup() {

    }

    public void deleteFromGroup() {

    }

}
