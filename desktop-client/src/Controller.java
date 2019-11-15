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


// Controls all FXML-Actions
public class Controller {
    public boolean authentication = true;

    /*
    FXML components
     */
    @FXML
    private AnchorPane LoginScreen;

    // Closes the LoginScreen with click on "X"
    @FXML
    public void exitWindow() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void signIn() throws IOException {
        // Methode schon mit Button "verlinkt"
        // TODO

        // If authentication was successful, then open up the client as maximized window
        if (authentication) {
            try {
                LoginScreen.getScene().getWindow().hide(); // hide = same as closing
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/res/ChatClient.fxml"));
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
                System.out.println("Cant load ChatClient!!");
            }
        }
    }

    @FXML
    public void signUp() {
        // Methode schon mit Button "verlinkt"
        // TODO
    }
}
