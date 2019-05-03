package linearSearch;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import templates.CustomAlertBox;
import templates.CustomStage;

/**
 * Creates an object from which a linearSearch scene can be retrieved.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class LinearSearch {

    private final CustomStage window;

    private final Parent prevScene;

    /**
     * Constructor.
     * @param window the window from which the scene should be shown.
     */

    public LinearSearch(CustomStage window) {
        this.window = window;
        this.prevScene = window.getScene().getRoot();
    }

    /**
     * Returns a new linearSearch scene.
     * @return
     */

    public Parent getScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LinearSearchFXML.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            LinearSearchController controller = loader.getController();
            controller.setWindow(window, prevScene, this);
        } catch (IOException e) {
            System.out.println(e);
            CustomAlertBox alertBox = new CustomAlertBox();
            alertBox.showAlertBox("Fatal error occured!");
        }
        return root;
    }
}
