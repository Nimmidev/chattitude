package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.ImageFileMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public class ImageFileMessageController extends MessageController {

    private static final String FIELD_FILE_ID = "fileId";
    private static final String FIELD_FILE_NAME = "filename";

    @FXML
    private VBox messageCell;

    @FXML
    private Label senderLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label timeLabel;
    
    @FXML
    private Pane imagePane;
    
    private DownloadManager downloadManager;
    private FXMLLoader mLLoader;
    
    private MenuItem downloadMenuItem;
    
    public ImageFileMessageController(DownloadManager downloadManager){
        this.downloadManager = downloadManager;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/ImageFileMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading ImageFileMessageCell", e);
        }
        
        createMenuItems();
    }

    private void createMenuItems(){
        downloadMenuItem = createMenuItem("Download", (ActionEvent e) -> {
            System.out.println("Download");

            String fileId = (String) imagePane.getProperties().get(FIELD_FILE_ID);
            
            downloadManager.download(fileId, objData -> Platform.runLater(() -> {
                String filename = (String) imagePane.getProperties().get(FIELD_FILE_NAME);
                downloadManager.saveToFileWizard(filename, objData, imagePane.getScene().getWindow());
            }));
        });
    }
    
    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        ImageFileMessage message = (ImageFileMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());
        contentLabel.setText(message.getText());
        imagePane.getProperties().put(FIELD_FILE_ID, message.getFileId());
        imagePane.getProperties().put(FIELD_FILE_NAME, message.getFilename());

        downloadManager.download(message.getFileId(), objData -> Platform.runLater(() -> {
            byte[] data = downloadManager.objectDataToPrimitive(objData);
            Image image = new Image(new ByteArrayInputStream(data));
            BackgroundImage bgi = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(0, 0, false, false, true, false));
            imagePane.setBackground(new Background(bgi));
        }));

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
        contextMenu.getItems().add(downloadMenuItem);
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
    
}
