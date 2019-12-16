package de.thu.inf.spro.chattitude.desktop_client.ui;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.packets.AuthenticationPacket;
import de.thu.inf.spro.chattitude.packet.packets.RegisterPacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginScreenController {

    private boolean testSignIn = true;

    private Client client;

    @FXML
    private AnchorPane loginScreen;

    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword;

    public LoginScreenController() {
        System.out.println("LoginScreenController");
        client = App.getClient();
        client.setOnLoginSuccessful(this::onLoginSuccessful);
        client.setOnLoginFailed(this::onLoginFailed);
    }

    private void cleanup() {
        client.setOnLoginSuccessful(null);
        client.setOnLoginFailed(null);
    }

    // Closes the LoginScreen with click on "X"
    public void exitWindow() {
        Platform.exit();
    }

    public void login() {
        // Methode schon mit Button "verlinkt"
        // Todo
        Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
        client.send(new AuthenticationPacket(credentials));
    }

    public void showMainScreen() {
        cleanup();
        try {
            loginScreen.getScene().getWindow().hide(); // hide = same as closing
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jfx/MainScreen.fxml"));
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

        } catch (IOException e) {
            throw new Error("Cant load ChatClient! Location not found - path invalid!", e);
        }
    }

    public void signUp() {
        // Methode schon mit Button "verlinkt"
        // Todo
        Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
        client.send(new RegisterPacket(credentials));
    }

    private void onLoginSuccessful() {
        Platform.runLater(this::showMainScreen);
        System.out.println("jo");
    }

    private void onLoginFailed() {
        System.out.println("Hier sollte jetzt login Fehler oder so stehen");
    }
}
