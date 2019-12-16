package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    private static final String CONVERSATION_PROPERTY = "conversation";

    @FXML
    private JFXTextArea messageField;
    @FXML
    private JFXListView<Label> conversationsList;
    @FXML
    private JFXListView<Message> messageHistoryList;
    @FXML
    public ComboBox comboBox;

    private Client client;
    private Conversation selectedConversation;
    private ObservableList<Message> messagesOfSelectedConversation;
    private boolean allMessagesOfCurrentConversationLoaded = false;
    private boolean loadingHistory = false;

    public MainScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        messagesOfSelectedConversation = FXCollections.observableArrayList();

        client.setOnMessage(message -> Platform.runLater(() -> {
            int conversationId = message.getConversationId();
            if (selectedConversation != null && conversationId == selectedConversation.getId()) {
                messagesOfSelectedConversation.add(message);
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


            for (Message message : packet.getMessages()) {
                if (!messagesOfSelectedConversation.contains(message)) {
                    messagesOfSelectedConversation.add(0, message);
                }
            }
            messageHistoryList.scrollTo(messagesOfSelectedConversation.size() - 1);
            checkToLoadHistory();
        }));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client.setOnConversations(conversations -> Platform.runLater(() -> {
            for (Conversation conversation : conversations) {
                Label cell = createConversationItem(conversation);
                cell.visibleProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println(newValue);
                });
                conversationsList.getItems().add(cell);

            }
        }));
        client.send(new GetConversationsPacket());
        conversationsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCell) -> {
            selectedConversation = ((Conversation) selectedCell.getProperties().get(CONVERSATION_PROPERTY));
            messagesOfSelectedConversation.clear();
            allMessagesOfCurrentConversationLoaded = false;
            loadingHistory = false;

            if (selectedConversation.getMessage() != null) {
                messagesOfSelectedConversation.add(selectedConversation.getMessage());

                ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
                VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
                vf.positionProperty().addListener((observable2, oldValue2, newValue) -> {
                    int first = vf.getFirstVisibleCell().getIndex();
                    System.out.println("First: " + first);
                    checkToLoadHistory();
                });

                loadMoreMessages();
            } else {
                allMessagesOfCurrentConversationLoaded = true;
            }
        });

        messageHistoryList.setCellFactory(param -> new MessageCell());
        messageHistoryList.setItems(messagesOfSelectedConversation);

        /*messageHistoryList.onScrollProperty().set(event -> {
            System.out.println("Scroll Prop " + event.getTotalDeltaX());
        });
        messageHistoryList.setOnScroll(event -> {
            System.out.println("On Scrollt " + event.getTotalDeltaX());
            ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            int first = vf.getFirstVisibleCell().getIndex();
            System.out.println("First: " + first);
        });

        messageHistoryList.setOnScrollTo(event -> {
            System.out.println("On ScrollTo " + event.getScrollTarget());
        });*/

        /*javafx.scene.control.ScrollBar timelineBar = getScrollbarComponent(messageHistoryList, javafx.geometry.Orientation.HORIZONTAL);
        timelineBar.addEventFilter(ScrollToEvent.ANY, event -> {
            ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            int first = vf.getFirstVisibleCell().getIndex();
            System.out.println("First: " + first);
        });*/

        /*ListViewSkin<?> ts = (ListViewSkin<?>) messageHistoryList.getSkin();
        VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
        int first = vf.getFirstVisibleCell().getIndex();*/
        // TODO hm

    }

    public static javafx.scene.control.ScrollBar getScrollbarComponent(javafx.scene.control.Control control, javafx.geometry.Orientation orientation) {
        javafx.scene.Node n = control.lookup(".scroll-bar");
        if (n instanceof javafx.scene.control.ScrollBar) {
            final javafx.scene.control.ScrollBar bar = (javafx.scene.control.ScrollBar) n;
            if (bar.getOrientation().equals(orientation)) {
                return bar;
            }
        }
        return null;
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
        int first = vf.getFirstVisibleCell().getIndex();
        System.out.println("Hier: " + first);
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

        User[] userArray = new User[]{new User(1,"testUser1"), new User(2, "testUser2")};

        Conversation dummyConversation = new Conversation(userArray); // Users have to exist
        CreateConversationPacket packet = new CreateConversationPacket(dummyConversation);
        client.send(packet);
        client.setOnConversationCreated(conversation -> Platform.runLater(() -> {

            Label cell = createConversationItem(conversation);
            conversationsList.getItems().add(0, cell);
            conversationsList.getSelectionModel().select(cell);
            selectedConversation = conversation;
            System.out.println("created " + conversation.getId());
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
