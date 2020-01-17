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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConversationMemberCell extends JFXListCell<User> {
    @FXML
    private HBox conversationMemberCell;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label adminLabel;
    @FXML
    private JFXButton removeButton;
    @FXML
    private JFXButton proDemoteButton;

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
        adminLabel.setVisible(conversation.isAdmin(user.getId()));
        proDemoteButton.setText(conversation.isAdmin(user.getId()) ? "Demote" : "Promote");

        if (user.getId() == client.getCredentials().getUserId()) {
            removeButton.setVisible(false);
            proDemoteButton.setVisible(false);
        } else {
            removeButton.setVisible(true);
            proDemoteButton.setVisible(true);
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

    @FXML
    private void proDemoteClick() {
        if (conversation.isAdmin(user.getId())) {
            client.send(new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.DEMOTE_ADMIN, user.getId(), conversation.getId()));
            conversation.setAdmins(
                    Arrays.stream(conversation.getAdmins())
                    .filter(value -> value != user.getId())
                    .boxed()
                    .collect(Collectors.toList())
            );
        } else {
            client.send(new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.PROMOTE_ADMIN, user.getId(), conversation.getId()));
            conversation.setAdmins(
                    Stream.concat(
                            Arrays.stream(conversation.getAdmins())
                                .filter(value -> value != user.getId())
                                .boxed(),
                            Stream.of(user.getId())
                    ).collect(Collectors.toList())
            );
        }
        this.updateItem(user, false);
    }
}
