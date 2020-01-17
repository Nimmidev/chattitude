package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
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

public class ConversationMemberCell extends JFXListCell<User> {
    @FXML
    private HBox conversationMemberCell;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label adminLabel;
    @FXML
    private JFXButton removeButton;

    private FXMLLoader mLLoader;
    private Client client;
    private Conversation conversation;
    private User user;
    private ObservableList<User> members;

    public ConversationMemberCell(Client client, Conversation conversation, ObservableList<User> members) {
        this.client = client;
        this.conversation = conversation;
        this.members = members;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/ConversationMemberCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading ConversationMemberCell", e);
        }
    }

    private boolean isAdmin() {
        for (int admin : conversation.getAdmins()) {
            if (admin == user.getId())
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
        adminLabel.setVisible(isAdmin());

        if (user.getId() == client.getCredentials().getUserId()) {
            removeButton.setVisible(false);
        } else {
            removeButton.setVisible(true);
        }


        setGraphic(conversationMemberCell);
    }

    @FXML
    private void removeClick() {
        client.send(new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.REMOVE, user.getId(), conversation.getId()));

        List<User> users = new ArrayList<>(Arrays.asList(conversation.getUsers()));
        users.remove(user);
        conversation.setUsers(users);

        members.remove(user);
    }
}
