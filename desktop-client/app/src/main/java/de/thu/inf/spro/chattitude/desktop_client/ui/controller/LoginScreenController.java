package de.thu.inf.spro.chattitude.desktop_client.ui.controller;

import com.jfoenix.controls.*;
import de.thu.inf.spro.chattitude.desktop_client.Client;
import de.thu.inf.spro.chattitude.desktop_client.ui.App;
import de.thu.inf.spro.chattitude.packet.Credentials;
import de.thu.inf.spro.chattitude.packet.packets.AuthenticationPacket;
import de.thu.inf.spro.chattitude.packet.packets.RegisterPacket;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreenController implements Initializable {

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    
    private Client client;
    private int minimalLength = 3; //Password and username minimal length
    private boolean toggleStatus;

    @FXML
    private AnchorPane loginScreen;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXPasswordField txtPassword2;
    @FXML
    private JFXToggleButton btnToggle;
    @FXML
    private JFXButton btnSignIn;
    @FXML
    private Label errorLabel;
    @FXML
    private Label labelLogin;


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

    public void signIn() {
        errorLabel.setText("");
        if (toggleStatus) {
            // Registration
            String userName = txtUsername.getText();
            String userPassword = txtPassword.getText();
            String userPassword2 = txtPassword2.getText();

            if (!userPassword.equals(userPassword2)) {
                errorLabel.setVisible(true);
                errorLabel.setText("Sorry, passwords don't match!");
            } else {
                if (userName.length() < minimalLength || userPassword.length() < minimalLength) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Username or password too short!");
                } else {
                    client.setOnRegister(credentials -> Platform.runLater(() -> {
                        if (credentials.isAuthenticated()) {
                            showMainScreen();
                        } else {
                            errorLabel.setVisible(true);
                            errorLabel.setText("Username already taken!");
                        }
                    }));
                    Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
                    client.send(new RegisterPacket(credentials));
                }
            }
        } else {
            // Login
            Credentials credentials = new Credentials(txtUsername.getText(), txtPassword.getText());
            client.send(new AuthenticationPacket(credentials));
        }
    }

    @FXML
    private void FieldsKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)  {
            signIn();
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
            if (!System.getProperty("os.name").toLowerCase().contains("mac"))
                secondaryStage.getIcons().add(new Image("/logoTitleBar.PNG"));
            setStageLayout(secondaryStage);
            secondaryStage.focusedProperty().addListener((observableValue, notFocused, t1) -> {
                MainScreenController.IS_FOCUSED = !notFocused;
            });
            secondaryStage.show();

        } catch (IOException e) {
            throw new Error("Can't load ChatClient! Location not found - path invalid!", e);
        }
    }
    
    private void setStageLayout(Stage stage){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        double x = (d.getWidth() - WINDOW_WIDTH) / 2;
        double y = (d.getHeight() - WINDOW_HEIGHT) / 2;
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
    }

    private void onLoginSuccessful() {
        Platform.runLater(this::showMainScreen);
        System.out.println("Login was successful!");
    }

    private void onLoginFailed() {
        System.out.println("Login failed.");
        Platform.runLater(() -> errorLabel.setText("Wrong username or password!"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPassword2.setVisible(false);
        labelLogin.setOnMouseClicked(event -> {
                btnToggle.setSelected(false);
        });
        btnToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean arg1, Boolean arg2) {
                if (btnToggle.isSelected() == true) {
                    toggleStatus = true;
                    txtPassword2.setVisible(true);
                    btnSignIn.setText("Register");
                } else {
                    errorLabel.setVisible(false);
                    toggleStatus = false;
                    txtPassword2.setVisible(false);
                    btnSignIn.setText("Login");
                }
            }
        });
    }
}
