package de.thu.inf.spro.chattitude.desktop_client.ui;

import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.User;
import de.thu.inf.spro.chattitude.packet.packets.SearchUserPacket;
import de.thu.inf.spro.chattitude.packet.util.Callback;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SearchUserPopUp implements Callback {
    private SearchUserPacket packet;
    private ListView<User> list;
    private ObservableList<User> oList;

    public SearchUserPopUp(Client client) {

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        dialog.setTitle("Find Chattitude-Users");
        Text txtText = new Text("Search for other Chattitude-Users here");
        TextField txtField = new TextField();

        txtField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                System.out.println("ENTER RELEASED");
                packet = new SearchUserPacket(txtField.getText());
                client.send(packet);

                oList = FXCollections.observableArrayList(packet.getResults());
                list = new ListView();
                list.setItems(oList);
                list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
                    @Override
                    public void changed(ObservableValue<? extends User> observableValue, User user, User t1) {
                        System.out.println("Test observable List");
                    }
                });
                dialogVbox.getChildren().add(list);
            }
        });


        dialogVbox.getChildren().addAll(txtText, txtField);
        Scene dialogScene = new Scene(dialogVbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void call(Object parameter) {

    }
}
