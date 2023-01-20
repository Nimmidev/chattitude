package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import de.thu.inf.spro.chattitude.desktop_client.Cache;
import de.thu.inf.spro.chattitude.desktop_client.DownloadManager;
import de.thu.inf.spro.chattitude.desktop_client.Util;
import de.thu.inf.spro.chattitude.desktop_client.message.AudioMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.ChatMessage;
import de.thu.inf.spro.chattitude.desktop_client.message.RawFileMessage;
import de.thu.inf.spro.chattitude.packet.util.Pair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AudioMessageController extends MessageController {
    
    private static final String FIELD_FILE_ID = "fileId";

    private static final Image PLAY_IMAGE = new Image("/icons/play.png");
    private static final Image PAUSE_IMAGE = new Image("/icons/pause.png"); 

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
    private Button playButton;
    
    @FXML
    private ImageView playButtonImage;

    private DownloadManager downloadManager;
    private FXMLLoader mLLoader;

    private static MediaPlayer player;
    private static String source;
    private static ImageView lastPlayIcon;

    public AudioMessageController(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
        mLLoader = new FXMLLoader(getClass().getResource("/jfx/AudioMessageCell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading RawFileMessageCell", e);
        }
    }
    
    @Override
    public void update(ChatMessage chatMessage, ContextMenu contextMenu) {
        AudioMessage message = (AudioMessage) chatMessage;
        senderLabel.setText(message.asMessage().getUser().getName());
        filenameLabel.setText(message.getFilename());
        playButton.getProperties().put(FIELD_FILE_ID, message.getFileId());
        setText(contentLabel, message.getText());

        timeLabel.setText(Util.getRelativeDateTime(new Date(message.asMessage().getTimestamp())));
    }
    
    @FXML
    private void onPlayClicked(){
        String fileId = (String) playButton.getProperties().get(FIELD_FILE_ID);
        
        if(!fileId.equals(source) && player!= null) reset();
        
        if(player != null){
            if(player.getStatus() == MediaPlayer.Status.PLAYING) player.pause();
            else if(player.getStatus() == MediaPlayer.Status.PAUSED) player.play();
            return;
        }
        
        byte[] audioBytes = Cache.get(fileId);

        if(audioBytes != null){
            playAudio(fileId);
        } else {
            Cache.cache(fileId, downloadManager, (Pair<String, Byte[]> pair) -> {
                String currentFileId = (String) playButton.getProperties().get(FIELD_FILE_ID);
                if(currentFileId.equals(pair.getKey())){
                    playAudio(pair.getKey());
                }
            });
        }
    }
    
    public static void reset(){
        if(player != null){
            if(player.getStatus() == MediaPlayer.Status.PLAYING) player.stop();
            player = null;
        }
        if(lastPlayIcon != null) lastPlayIcon.setImage(PLAY_IMAGE);
        source = null;
    }
    
    private void playAudio(String fileId){
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        Media media = new Media(new File(tmpDir, fileId).toURI().toString());
        
        source = fileId;
        player = new MediaPlayer(media);
        player.setVolume(0.5);
        player.play();

        lastPlayIcon = playButtonImage;
        
        player.setOnPlaying(() -> {
            lastPlayIcon.setImage(PAUSE_IMAGE);
        });
        
        player.setOnPaused(() -> {
            lastPlayIcon.setImage(PLAY_IMAGE);
        });

        player.setOnEndOfMedia(() -> {
            player = null;
            lastPlayIcon.setImage(PLAY_IMAGE);
        });
    }

    @Override
    public Node getNode() {
        return messageCell;
    }
    
}
