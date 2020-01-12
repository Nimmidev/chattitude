package de.thu.inf.spro.chattitude.desktop_client.ui;

import de.thu.inf.spro.chattitude.desktop_client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class App extends Application {
    private static Client client;
    private static Application INSTANCE;

    public App() {

    }

    @Override
    public void start(Stage primaryStage) {
        INSTANCE = this;
        try {
            client = new Client();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/jfx/LoginScreen.fxml"));
            // TODO: Auskommentiert für den klassisches Fensterstyle
            //primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setResizable(false);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Exiting…");
        client.close();
    }

    public static Client getClient() {
        return client;
    }
}
