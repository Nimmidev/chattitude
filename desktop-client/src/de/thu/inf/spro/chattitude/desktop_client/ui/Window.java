package de.thu.inf.spro.chattitude.desktop_client.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Window extends Application {

    private OnCloseListener onCloseListener;
    private boolean testSignIn = true;

    @FXML
    private AnchorPane LoginScreen;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/jfx/LoginScreen.fxml"));
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Can't load LoginScreen! Location not found - path invalid!");
        }
    }

    // Closes the LoginScreen with click on "X"
    public void exitWindow() {
        if (onCloseListener != null) onCloseListener.onClose();
        System.exit(0);
    }

    public void signIn() {
        // Methode schon mit Button "verlinkt"
        // TODO

        if (testSignIn) {
            try {
                LoginScreen.getScene().getWindow().hide(); // hide = same as closing
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jfx/ChatClient.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                Stage secondaryStage = new Stage();
                //
                // Workaround for "stage.setMaximized(true) - This really hides the task bar
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                secondaryStage.setX(primaryScreenBounds.getMinX());
                secondaryStage.setY(primaryScreenBounds.getMinY());
                secondaryStage.setWidth(primaryScreenBounds.getWidth());
                secondaryStage.setHeight(primaryScreenBounds.getHeight());
                //
                secondaryStage.setResizable(false);
                secondaryStage.initStyle(StageStyle.UNDECORATED);
                Scene scene = new Scene(root);
                secondaryStage.setScene(scene);
                secondaryStage.show();

            } catch (Exception e) {
                System.out.println("Cant load ChatClient! Location not found - path invalid!");
            }
        }
    }

    public void signUp() {
        // Methode schon mit Button "verlinkt"
        // TODO
    }

    public void sendMessage() {
        // TODO
    }

    public void newChat() {
        // TODO
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public interface OnCloseListener {
        void onClose();
    }
}
