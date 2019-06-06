package mainMenu;

import java.util.Random;
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

    public CustomStage window;

    //Where to get textFiles.
    public static String textDirectory = System.getProperty("user.dir") + "/src/data";

    @Override
    public void start(Stage stage) throws Exception {
        window = new CustomStage(320, 340, true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuFXML.fxml"));
        Parent root = loader.load();
        MainMenuController controller = loader.getController();
        controller.setWindow(window);

        Scene scene = new Scene(root);
        window.setMyScene(scene);
        window.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random random = new Random();
        for(int i = 1; i <= 15; i++) {
            int number = random.nextInt(999);
            //if(random.nextBoolean())
                //number = number  * (-1);
            System.out.print(number + " ");
        }
        System.out.println();
        launch(args);
    }

}
