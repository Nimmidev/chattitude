package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.io.IOException;


public class GroupChatMemberCell extends JFXListCell<User> {

        @FXML
        private HBox GroupChatMemberCell;
        @FXML
        private Label usernameLabel;
        @FXML
        private JFXButton removeButton;

        private FXMLLoader mLLoader;
        private Client client;
        private Conversation conversation;
        private User user;
        private ObservableList<User> members;

        public GroupChatMemberCell(Client client, ObservableList<User> members) {
            this.client = client;
            this.members = members;
            mLLoader = new FXMLLoader(getClass().getResource("/jfx/GroupChatMemberCell.fxml"));
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

            if (user.getId() == client.getCredentials().getUserId()) {
                removeButton.setVisible(false);
            } else {
                removeButton.setVisible(true);
            }

            setGraphic(GroupChatMemberCell);
        }

        @FXML
        private void removeClick() {
            System.out.println("Remove Button clicked");
            members.remove(user);
        }
}
