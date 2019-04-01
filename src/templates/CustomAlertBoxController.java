package templates;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
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

    public static void setOnMousePressed(Button button) {
        button.setOnMousePressed(e -> button.translateYProperty().set(2));
        button.setOnMouseReleased(e -> button.translateYProperty().set(0));
    }

    public void closeButton() {
        window.close();
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public void initText(String message) {
        text.setText(message);
    }
}
