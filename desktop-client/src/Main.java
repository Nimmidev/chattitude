import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) {
            launch(args);
            System.out.println("Hello World!");
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/res/LoginScreen.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Closes the LoginScreen with click on "X"
    public void exitLoginScreen(){
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
}
