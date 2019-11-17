package de.thu.inf.spro.chattitude.desktop_client.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Window extends Application {

    private OnCloseListener onCloseListener;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/jfx/LoginScreen.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Closes the LoginScreen with click on "X"
    public void exitWindow(){
        if(onCloseListener != null) onCloseListener.onClose();
        System.exit(0);
    }

    public void signIn() {
        // Methode schon mit Button "verlinkt"
        // TODO
    }

    public void signUp() {
        // Methode schon mit Button "verlinkt"
        // TODO
    }

    public void setOnCloseListener(OnCloseListener onCloseListener){
        this.onCloseListener = onCloseListener;
    }

    public interface OnCloseListener {
        void onClose();
    }

}
