package binarySearch;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import templates.CustomAlertBox;
import templates.CustomStage;

/**
 * Creates an object from which a binary search scene can be retrieved.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class BinarySearch {

    private final CustomStage window;

    private final Parent prevScene;

    /**
     * Constructor.
     * @param window the window from which the scene should be shown.
     */

    public BinarySearch(CustomStage window) {
        this.window = window;
        this.prevScene = window.getScene().getRoot();
    }

    /**
     * Returns a new binary search scene.
     * @return
     */

    public Parent getScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BinarySearchFXML.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            BinarySearchController controller = loader.getController();
            controller.setWindow(window, prevScene, this);
        } catch (IOException e) {
            System.out.println(e);
            CustomAlertBox alertBox = new CustomAlertBox();
            alertBox.showAlertBox("Fatal error occured!");
        }
        return root;
    }
}
