package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.SearchUserPacket;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateSingleChatPopUp extends StackPane implements Initializable {

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
    private ObservableList<User> usersInConversation;
    private Client client;
    private CreateSingleChatPopUp popUp;


    public CreateSingleChatPopUp(Client client) {
        this.client = client;
        this.popUp = this;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/CreateSingleChat.fxml"));
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
            throw new RuntimeException("Error loading CreateSingleChatPopUp", e);
        }
    }

    public void displayError() {
        labelError.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        labelError.setVisible(false);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                searchResultList.getItems().clear();
                return;
            }
            client.send(new SearchUserPacket(newValue));
        });

        searchResultList.setCellFactory(param -> new SingleChatSearchResultCell(client, popUp));
    }

    @FXML
    public void closePopUpClick() {
        ((Pane) getParent()).getChildren().remove(this);
    }
}

