package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXListCell;
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
import java.util.Locale;

public class ConversationCell extends JFXListCell<Conversation> {
    @FXML
    private VBox conversationCell;
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Label timeLabel;

    private FXMLLoader mLLoader;

    public ConversationCell() {
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/ConversationCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading ConversationCell", e);
        }
    }

    private void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);

        titleLabel.setText(null);
        messageLabel.setText(null);
        timeLabel.setText(null);


        if (!empty) {
            titleLabel.setText(conversation.getName());
            Message message = conversation.getMessage();
            if (message != null) {
                messageLabel.setText(message.getUser().getName() + ": " + conversation.getMessage().getContent());

                Date date = new Date(message.getTimestamp());

                Calendar today = Calendar.getInstance();
                setToStartOfDay(today);

                DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                if (!date.before(today.getTime())) {
                    format = DateFormat.getTimeInstance(DateFormat.SHORT);
                }

                timeLabel.setText(format.format(date));
            }
        }

        setText(null);
        setGraphic(conversationCell);
    }
}
