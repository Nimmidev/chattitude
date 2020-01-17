package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.FileMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Date;

public class FileMessageController implements MessageController {
    
    private static final String FIELD_FILE_ID = "fileId";
    
    @FXML
    private VBox messageCell;
    
    @FXML
    private Label senderLabel;
    
    @FXML
    private Label contentLabel;
    
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

    public FileMessageController(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/FileMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading FileMessageCell", e);
        }
    }
    
    @FXML
    public void onButtonClicked(){
        String fileId = (String) downloadFileButton.getProperties().get(FIELD_FILE_ID);
        downloadManager.download(fileId, objData -> Platform.runLater(() -> {
            byte[] data = downloadManager.objectDataToPrimitive(objData);
            String downloadDirectory = downloadManager.chooseDirectory(downloadFileButton.getScene().getWindow());
            String filename = filenameLabel.getText();
            downloadManager.saveTo(downloadDirectory, filename, data);
        }));
    }

    @Override
    public void update(ChatMessage chatMessage) {
        FileMessage message = (FileMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());
        contentLabel.setText(message.getText());
        filenameLabel.setText(message.getFilename());
        downloadFileButton.getProperties().put(FIELD_FILE_ID, message.getFileId());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
}
