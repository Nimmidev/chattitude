package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.*;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    @FXML
    private JFXTextArea messageField;
    @FXML
    private JFXListView<Conversation> conversationsList;
    @FXML
    private JFXListView<Message> messageHistoryList;
    @FXML
    public ComboBox comboBox;

    private Client client;
    private Conversation selectedConversation;
    private ObservableList<Conversation> conversations;
    private ObservableList<Message> messagesOfSelectedConversation;
    private boolean allMessagesOfCurrentConversationLoaded = false;
    private boolean loadingHistory = false;

    public MainScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        messagesOfSelectedConversation = FXCollections.observableArrayList();
        conversations = FXCollections.observableArrayList();

        client.setOnMessage(message -> Platform.runLater(() -> {
            int conversationId = message.getConversationId();
            if (selectedConversation != null && conversationId == selectedConversation.getId()) {
                messagesOfSelectedConversation.add(message);
            }
            Conversation conversation = getConversation(conversationId);
            if (conversation == null) {
                System.out.println("Warning: Received message for unknown conversation " + conversationId);
                return;
            }
            conversation.setMessage(message); // TODO irgendwie ein Update triggern?

        }));

        client.setOnConversationUpdated(newConversation -> Platform.runLater(() -> {
            Conversation oldConversation = getConversation(newConversation.getId());
            if (oldConversation == null) { // new conversation
                conversations.add(newConversation);
            } else {
                int index = conversations.indexOf(oldConversation);
                conversations.set(index, newConversation); // Replace with new conversation object
            }
        }));

        client.setOnMessageHistory(packet -> Platform.runLater(() -> {
            loadingHistory = false;
            if (selectedConversation.getId() != packet.getConversationId())
                return;

            if (packet.getLastMessageId() != messagesOfSelectedConversation.get(0).getId()) {
                System.out.println("Warning hä das wollt ich doch gar nicht");
                return;
            }

            if (packet.getMessages().length == 0) {
                allMessagesOfCurrentConversationLoaded = true;
                return;
            }

            ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            Message topMost = messagesOfSelectedConversation.get(vf.getFirstVisibleCell().getIndex());

            for (Message message : packet.getMessages()) {
                if (!messagesOfSelectedConversation.contains(message)) {
                    messagesOfSelectedConversation.add(0, message);
                }
            }
            messageHistoryList.scrollTo(topMost);
            checkToLoadHistory();
        }));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client.setOnConversations(newConversations -> Platform.runLater(() -> {
            conversations.clear();
            conversations.addAll(newConversations);
        }));
        client.send(new GetConversationsPacket());

        conversationsList.setCellFactory(param -> new ConversationCell());
        conversationsList.setItems(conversations);
        conversationsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newSelectedConversation) -> {
            selectedConversation = newSelectedConversation;
            messagesOfSelectedConversation.clear();
            allMessagesOfCurrentConversationLoaded = false;
            loadingHistory = false;

            if (selectedConversation.getMessage() != null) {
                messagesOfSelectedConversation.add(selectedConversation.getMessage());

                ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
                VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
                vf.positionProperty().addListener((observable2, oldValue2, newValue) -> checkToLoadHistory());

                loadMoreMessages();
            } else {
                allMessagesOfCurrentConversationLoaded = true;
            }
        });

        messageHistoryList.setCellFactory(param -> new MessageCell());
        messageHistoryList.setItems(messagesOfSelectedConversation);
    }

    private void loadMoreMessages() {
        System.out.println("Load more messages for " + selectedConversation);
        if (selectedConversation.getMessage() == null)
            return;

        loadingHistory = true;
        int lastMessageId = messagesOfSelectedConversation.get(0).getId();

        client.send(new MessageHistoryPacket(selectedConversation.getId(), lastMessageId));
    }

    private void checkToLoadHistory() {
        ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
        VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
        var firstVisible = vf.getFirstVisibleCell();
        if (firstVisible == null)
            return;
        int first = firstVisible.getIndex();
        if (first == 0) {
            if (!allMessagesOfCurrentConversationLoaded && !loadingHistory) {
                loadMoreMessages();
            }
        }
    }

    public void exitWindow() {
        Platform.exit();
    }

    public void createDialog() {
        SearchUserPopUp popUp = new SearchUserPopUp(client, selectedConversation.getId());

    }

    public void newChat() {
        // TODO

        String groupName = "Group " + new SimpleDateFormat("HH:mm:ss").format(new Date());
        User[] userArray = new User[]{new User(1,"testUser1"), new User(2, "testUser2")};

        Conversation dummyConversation = new Conversation(userArray[1]);
        CreateConversationPacket packet = new CreateConversationPacket(dummyConversation);
        client.send(packet);
        client.setOnConversationCreated(conversation -> Platform.runLater(() -> {
            conversations.add(conversation);
            conversationsList.getSelectionModel().select(conversation);
        }));

    }

    public void sendMessage() {
        System.out.println("Send");
        Message message = new Message(selectedConversation.getId(), messageField.getText(), null);

        client.send(new MessagePacket(message));
    }

    private Conversation getConversation(int id) {
        for (Conversation conversation : conversations) {
            if (conversation.getId() == id)
                return conversation;
        }
        return null;
    }

    public void addToGroup() {

    }

    public void deleteFromGroup() {

    }

}
