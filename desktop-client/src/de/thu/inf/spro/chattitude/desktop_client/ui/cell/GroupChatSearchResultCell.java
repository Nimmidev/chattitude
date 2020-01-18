package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

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

public class GroupChatSearchResultCell extends JFXListCell<User> {

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

        public GroupChatSearchResultCell(Client client,  ObservableList<User> members) {
            this.client = client;
            this.members = members;
            mLLoader = new FXMLLoader(getClass().getResource("/jfx/SearchResultCell.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                throw new RuntimeException("Error loading SearchResultCell", e);
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
            addUserButton.setVisible(true);
            setGraphic(conversationMemberCell);
        }

        @FXML
        private void addClick() {
            members.add(user);
            this.updateItem(user, false);
            System.out.println(user.getName() + " added to group!");
        }
    }

