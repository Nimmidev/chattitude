package de.thu.inf.spro.chattitude.desktop_client.ui.cell;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.desktop_client.ui.CreateSingleChatPopUp;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.CreateConversationPacket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.io.IOException;


public class SingleChatSearchResultCell extends JFXListCell<User>{

        @FXML
        private HBox conversationMemberCell;
        @FXML
        private Label usernameLabel;
        @FXML
        private JFXButton addUserButton;


        private FXMLLoader mLLoader;
        private Client client;
        private User user;
        private Conversation conversation;
        private CreateSingleChatPopUp popUp;

        public SingleChatSearchResultCell (Client client, CreateSingleChatPopUp singleChatPopUp) {
            this.client = client;
            this.popUp = singleChatPopUp;
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

            if (user.getId() == client.getCredentials().getUserId()) {
                addUserButton.setVisible(false);
            } else {
                addUserButton.setVisible(true);
            }
            usernameLabel.setText(user.getName());

            setGraphic(conversationMemberCell);
        }

        @FXML
        private void addClick() {
            if (client.getCredentials().getUsername().equals(user.getName())) {
                System.out.println("You can´t start a chat with yourself!");
                popUp.displayError();
            } else {
                client.send(new CreateConversationPacket(conversation = new Conversation(user)));
                popUp.closePopUpClick();
            }
        }
}
