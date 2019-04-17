package templates;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class CustomAlertBoxController implements Initializable{

    private Stage window;

    @FXML
    private Button exit;

    @FXML
    private Label text;

    @FXML
    private BorderPane layout;

    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOnMousePressed(exit);
    }

    /**
     * Adds the pressed effect to a button.
     * @param button the button to add the effect to.
     */

    public static void setOnMousePressed(Button button) {
        button.setOnMousePressed(e -> button.translateYProperty().set(2));
        button.setOnMouseReleased(e -> button.translateYProperty().set(0));
    }

    /**
     * Action for the closeButton.
     */

    public void closeButton() {
        window.close();
    }

    /**
     * Sets the window from which this scene is shown.
     * @param window
     */

    public void setWindow(Stage window) {
        this.window = window;
    }

    /**
     * The text to show in this AlertBox.
     * @param message
     */

    public void initText(String message) {
        text.setText(message);
    }
}
