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
    public JFXTextField txtUsername;
    public JFXPasswordField txtPassword;

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
            Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
            client.send(new RegisterPacket(credentials));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration successful.");
            alert.setHeaderText("Welcome to Chattitude!");
            alert.setContentText("Your account has been successfully created. You can now sign in.");
            alert.show();
        }
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
            throw new Error("Can't load ChatClient! Location not found - path invalid!", e);
        }
    }

    private void onLoginSuccessful() {
        Platform.runLater(this::showMainScreen);
        System.out.println("Login was successful!");
    }

    private void onLoginFailed() {
        System.out.println("Login failed.");
       //Alert alert2 = new Alert(Alert.AlertType.ERROR);
       //alert2.setTitle("Authentication Error");
       //alert2.setHeaderText("Login failed.");
       //alert2.setContentText("Sorry, your username or password is incorrect.");
       //alert2.show();
    }
}
