package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.desktop_client.ui.cell.SearchResultCell;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.SearchUserPacket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditConversationPopUp extends StackPane implements Initializable {
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

    private FXMLLoader mLLoader;
    private Conversation conversation;
    private ObservableList<User> usersInConversation;
    private Client client;

    public EditConversationPopUp(Client client, Conversation conversation) {
        this.client = client;
        this.conversation = conversation;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/EditConversationPopUp.fxml"));
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

        usersInConversation = FXCollections.observableArrayList(conversation.getUsers());

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
            throw new RuntimeException("Error loading EditConversationPopUp", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conversationNameField.setText(conversation.getName());

        usersInConversationList.setItems(usersInConversation);
        usersInConversationList.setCellFactory(param -> new ConversationMemberCell(client, conversation, usersInConversation));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                searchResultList.getItems().clear();
                return;
            }
            client.send(new SearchUserPacket(newValue));
        });

        searchResultList.setCellFactory(param -> new SearchResultCell(client, conversation, usersInConversation));
    }

    @FXML
    private void closePopUpClick() {
        ((Pane) getParent()).getChildren().remove(this);
    }

    @FXML
    private void saveConversationNameClick() {
        saveConversationName();
    }

    @FXML
    private void conversationNameFieldKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            saveConversationName();
        }
    }

    private void saveConversationName() {

    }
}
