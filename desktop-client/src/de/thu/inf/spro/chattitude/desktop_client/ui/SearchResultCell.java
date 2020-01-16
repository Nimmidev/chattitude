package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.ConversationUpdatedPacket;
import de.thu.inf.spro.chattitude.packet.packets.ModifyConversationUserPacket;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultCell extends JFXListCell<User> {
    @FXML
    private HBox conversationMemberCell;
    @FXML
    private Label usernameLabel;
    @FXML
    private JFXButton addUserButton;

    private FXMLLoader mLLoader;
    private Client client;
    private Conversation conversation;
    private User user;
    private ObservableList<User> members;

    public SearchResultCell(Client client, Conversation conversation, ObservableList<User> members) {
        this.client = client;
        this.conversation = conversation;
        this.members = members;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/SearchResultCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading SearchResultCell", e);
        }
    }

    private boolean isUserAlreadyInConversation() {
        for (User conversationUser : conversation.getUsers()) {
            if (conversationUser.getId() == user.getId())
                return true;
        }
        return false;
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        this.user = user;
        setText(null);

        if (empty) {
            setGraphic(null);
            return;
        }

        usernameLabel.setText(user.getName());

        if (isUserAlreadyInConversation())
            addUserButton.setVisible(false);
        else
            addUserButton.setVisible(true);

        setGraphic(conversationMemberCell);
    }

    @FXML
    private void addClick() {
        client.send(new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.ADD, user.getId(), conversation.getId()));

        List<User> users = new ArrayList<>(Arrays.asList(conversation.getUsers()));
        users.add(user);
        conversation.setUsers(users);

        members.add(user);
        this.updateItem(user, false);
    }
}
