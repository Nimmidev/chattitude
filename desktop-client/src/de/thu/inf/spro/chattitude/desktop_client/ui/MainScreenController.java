package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
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

    private static final String CONVERSATION_ID_PROPERTY = "conversatioId";

    @FXML
    private JFXTextArea messageField;

    @FXML
    private JFXListView<Label> conversationsList;

    @FXML
    private JFXListView<Label> messageHistoryList;

    private Client client;
    private int selectedConversation;

    public MainScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        client.setOnMessage(System.out::println);
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
            selectedConversation = (int) selectedCell.getProperties().get(CONVERSATION_ID_PROPERTY);
            showMessages();
        });
    }

    private Label createConversationItem(Conversation conversation) {
        Label cell = new Label();
        cell.getProperties().put(CONVERSATION_ID_PROPERTY, conversation.getId());
        String text = "Id: " + conversation.getId();
        if (conversation.getMessage() != null) {
            text += "\n" + conversation.getMessage().getUser().getName() + ": " + conversation.getMessage().getContent();
        }
        cell.setText(text);
        return cell;
    }

    private void showMessages() {
        System.out.println("Show messages for " + selectedConversation);
        client.setOnMessageHistory(messages -> Platform.runLater(() -> {
            for (Message message : messages) {
                Label cell = new Label(message.getUser().getName() + ": " + message.getContent());
                messageHistoryList.getItems().add(0, cell);
            }
        }));
        client.send(new MessageHistoryPacket(selectedConversation, 0));

        messageHistoryList.getItems().clear();
        /*if () TODO letzte Message gleich anzeigen
        messageHistoryList.getItems().add()*/
    }

    public void exitWindow() {
        Platform.exit();
    }

    public void newChat() {
        CreateConversationPacket packet = new CreateConversationPacket(1);
        client.send(packet);
        client.setOnConversationCreated(conversationId -> Platform.runLater(() -> {
            Label cell = createConversationItem(new Conversation(conversationId, 0, null));
            conversationsList.getItems().add(0, cell);
            conversationsList.getSelectionModel().select(cell);
            selectedConversation = conversationId;
            System.out.println("created " + conversationId);
        }));
    }

    public void sendMessage() {
        System.out.println("Send");
        Message message = new Message(selectedConversation, messageField.getText(), null);

        client.send(new MessagePacket(message));
    }

}
