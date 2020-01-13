package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListCell;
import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.packet.Conversation;
import de.thu.inf.spro.chattitude.packet.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MessageCell extends JFXListCell<Message> {
    @FXML
    private VBox messageCell;
    @FXML
    private Label senderLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label timeLabel;

    private FXMLLoader mLLoader;

    public MessageCell() {
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/MessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading MessageCell", e);
        }
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        setText(null);

        if (empty) {
            setGraphic(null);
            return;
        }

        senderLabel.setText(message.getUser().getName());
        contentLabel.setText(message.getContent());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.getTimestamp())));

        setGraphic(messageCell);

    }
}
