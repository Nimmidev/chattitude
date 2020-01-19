package de.thu.inf.spro.chattitude.desktop_client.ui.popup;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.desktop_client.ui.cell.GroupChatMemberCell;
import de.thu.inf.spro.chattitude.desktop_client.ui.cell.GroupChatSearchResultCell;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.CreateConversationPacket;
import de.thu.inf.spro.chattitude.packet.packets.SearchUserPacket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateGroupChatPopUp extends StackPane implements Initializable {

        @FXML
        private Pane parent;
        @FXML
        private JFXTextField conversationNameField;
        @FXML
        private JFXListView<User> usersInConversationList;
        @FXML
        private JFXTextField searchField;
        @FXML
        private JFXListView<User> searchResultList;
        @FXML
        private Label labelError;

        private FXMLLoader mLLoader;
        private ArrayList <User> userList;
        private ObservableList<User> usersInConversation;
        private Client client;
        private Conversation conversation;

        public CreateGroupChatPopUp(Client client) {
            this.client = client;
            mLLoader = new FXMLLoader(getClass().getResource("/jfx/CreateGroupChat.fxml"));
            mLLoader.setController(this);

            setMaxHeight(Double.MAX_VALUE);
            setMaxWidth(Double.MAX_VALUE);
            setPrefHeight(Double.MAX_VALUE);
            setPrefWidth(Double.MAX_VALUE);
            setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            setOnMouseClicked(event -> {
                if (event.getTarget() == this)
                    closePopUpClick();
            });

            usersInConversation = FXCollections.observableArrayList();

            client.setOnSearchUser(packet -> Platform.runLater(() -> {
                if (packet.getQuery().equals(searchField.getText())) {
                    searchResultList.getItems().setAll(packet.getResults());
                } else {
                    searchResultList.getItems().clear();
                }
            }));

            try {
                mLLoader.load();
                getChildren().add(parent);
            } catch (IOException e) {
                throw new RuntimeException("Error loading CreateGroupChatPopUp", e);
            }
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {

            usersInConversationList.setItems(usersInConversation);
            usersInConversationList.setCellFactory(param -> new GroupChatMemberCell(client, usersInConversation));
            labelError.setVisible(false);

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals("")) {
                    searchResultList.getItems().clear();
                    return;
                }
                client.send(new SearchUserPacket(newValue));
            });

            searchResultList.setCellFactory(param -> new GroupChatSearchResultCell(client, usersInConversation));

        }

        @FXML
        private void closePopUpClick() {
            ((Pane) getParent()).getChildren().remove(this);
        }

        @FXML
        private void createGroupChatClick() {
            createGroupChat();
        }

        private void createGroupChat() {
            User[] userArray = usersInConversation.toArray(new User[0]);

            if (userArray.length == 0) {
                System.out.println("UserArray LEER");
                labelError.setText("You need to choose atleast 1 user!");
                labelError.setVisible(true);
            } else if (conversationNameField.getText().length() < 1) {
                System.out.println("Gruppename LEER");
                labelError.setText("You need to name your group!");
                labelError.setVisible(true);
            } else {
                client.send(new CreateConversationPacket(conversation = new Conversation(conversationNameField.getText(), userArray)));
                closePopUpClick();
            }
        }
    }
