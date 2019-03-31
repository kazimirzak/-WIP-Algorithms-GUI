package mainMenu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import templates.CustomStage;

/**
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class MainMenu extends Application {

    public static CustomStage window;
    public static String textDirectory = System.getProperty("user.dir") + "/src/data";

    @Override
    public void start(Stage stage) throws Exception {
        window = new CustomStage(320, 340);
        Parent root = FXMLLoader.load(getClass().getResource("MainMenuFXML.fxml"));

        Scene scene = new Scene(root);
        window.setMyScene(scene);
        window.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
