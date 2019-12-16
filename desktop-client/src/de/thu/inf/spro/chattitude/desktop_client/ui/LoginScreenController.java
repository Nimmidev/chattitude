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
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class LoginScreenController {

    private Client client;
    private int minimalLength = 3; //Password and username minimal length

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

    public void signIn() {
        Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
        client.send(new AuthenticationPacket(credentials));
    }

    public void signUp() {
        String userName = txtUsername.getText();
        String userPassword = txtPassword.getText();


        if (userName.length() < minimalLength || userPassword.length() < minimalLength) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Error");
            alert.setHeaderText("Username or password too short.");
            alert.setContentText("Sorry, your username and password needs at least 4 characters.");
            alert.show();
        } else {
            client.setOnRegister(credentials -> Platform.runLater(() ->{
                if (credentials.isAuthenticated()) {
                    showMainScreen();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration failed");
                    alert.setHeaderText("Username already taken.");
                    alert.setContentText("XXX");
                    alert.show();
                }
            }));
            Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
            client.send(new RegisterPacket(credentials));
        }
    }

    public void showMainScreen() {
        cleanup();
        try {
            loginScreen.getScene().getWindow().hide(); // hide = same as closing
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jfx/MainScreen.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage secondaryStage = new Stage();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            secondaryStage.setTitle("Chattitude");
            secondaryStage.setX(primaryScreenBounds.getMinX());
            secondaryStage.setY(primaryScreenBounds.getMinY());
            secondaryStage.setWidth(primaryScreenBounds.getWidth());
            secondaryStage.setHeight(primaryScreenBounds.getHeight());
            secondaryStage.setResizable(true);
            Scene scene = new Scene(root);
            secondaryStage.setScene(scene);
            secondaryStage.show();

        } catch (IOException e) {
            throw new Error("Can't load ChatClient! Location not found - path invalid!", e);
        }
    }

    private void onLoginSuccessful() {
        Platform.runLater(this::showMainScreen);
        System.out.println("Login was successful!");
    }

    private void onLoginFailed() {
        System.out.println("Login failed.");
    }
}
