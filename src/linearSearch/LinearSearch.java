package linearSearch;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import templates.CustomAlertBox;
import templates.CustomStage;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class LinearSearch {

    private final CustomStage window;

    private final Parent prevScene;

    public LinearSearch(CustomStage window) {
        this.window = window;
        this.prevScene = window.getScene().getRoot();
    }

    public Parent getLinearSearchScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LinearSearchFXML.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            LinearSearchController controller = loader.getController();
            controller.setWindow(window, this);
        } catch (IOException ex) {
            System.out.println(ex);
            CustomAlertBox alertBox = new CustomAlertBox();
            alertBox.showAlertBox("Fatal error occured!");
        }
        return root;
    }
}
