package de.thu.inf.spro.chattitude.desktop_client.ui;

import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.ModifyConversationUserPacket;
import de.thu.inf.spro.chattitude.packet.packets.SearchUserPacket;
import de.thu.inf.spro.chattitude.packet.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SearchUserPopUp implements Callback <User[]>{
    private SearchUserPacket packet;
    private ListView<String> list = new ListView();
    private ObservableList<String> oList = FXCollections.observableArrayList();
    private User[] tmpArray;
    private String tmpString;
    private int tmpUserID;
    private int conversationID;


    public SearchUserPopUp(Client client, int id) {
        client.setOnSearchUser(this);
        this.conversationID = id;

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        dialog.setTitle("Find Chattitude-Users");
        Text txtText = new Text("Search for other Chattitude-Users here");
        TextField txtField = new TextField();
        Label txtLabel = new Label();

        txtField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                System.out.println("ENTER RELEASED");
                packet = new SearchUserPacket(txtField.getText());
                client.send(packet);
            }
        });

        list.setOnMouseClicked(mouseEvent -> {
            if (list.getSelectionModel() != null) {
                tmpString = list.getSelectionModel().getSelectedItem();
                for (int i = 0; i < tmpArray.length; i++) {
                    if (tmpArray[i].getName() == tmpString) {
                        tmpUserID = tmpArray[i].getId();
                    }
                }
                ModifyConversationUserPacket packet = new ModifyConversationUserPacket(ModifyConversationUserPacket.Action.ADD, tmpUserID, conversationID);
                client.send(packet);
                dialog.close();
            }
        });

        list.setItems(oList);
        dialogVbox.getChildren().addAll(txtText, txtField, txtLabel, list);
        Scene dialogScene = new Scene(dialogVbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.show();
        dialog.setOnCloseRequest(windowEvent -> {
            client.setOnSearchUser(null);
        });
    }

    @Override
    public void call(User[] users) {
        oList.clear();
        for(User user : users) oList.add(user.getName());
        tmpArray = users;
    }
}
