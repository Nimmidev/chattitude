package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.RawFileMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Date;

public class RawFileMessageController extends MessageController {
    
    private static final String FIELD_FILE_ID = "fileId";
    
    @FXML
    private VBox messageCell;
    
    @FXML
    private Label senderLabel;
    
    @FXML
    private TextFlow contentLabel;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private HBox fileInfoCell;
    
    @FXML
    private Label filenameLabel;
    
    @FXML
    private Button downloadFileButton;

    private DownloadManager downloadManager;
    private FXMLLoader mLLoader;

    public RawFileMessageController(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/RawFileMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading RawFileMessageCell", e);
        }
    }
    
    @FXML
    public void onButtonClicked(){
        String fileId = (String) downloadFileButton.getProperties().get(FIELD_FILE_ID);
        
        downloadManager.download(fileId, objData -> Platform.runLater(() -> {
            String filename = filenameLabel.getText();
            downloadManager.saveToFileWizard(filename, objData, downloadFileButton.getScene().getWindow());
        }));
    }

    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        RawFileMessage message = (RawFileMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());
        filenameLabel.setText(message.getFilename());
        downloadFileButton.getProperties().put(FIELD_FILE_ID, message.getFileId());
        setText(contentLabel, message.getText());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
}
