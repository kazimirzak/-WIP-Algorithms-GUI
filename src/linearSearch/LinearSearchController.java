package linearSearch;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.ToggleSwitch;
import templates.CustomStage;

/**
 * FXML Controller class
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class LinearSearchController implements Initializable {

    private CustomStage window;
    private Parent prevScene;
    private LinearSearch linearSearch;

    @FXML
    private VBox menu;

    @FXML
    private Button resizeButton, runButton, resetButton, backButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ToggleSwitch colorMode;

    @FXML
    private HBox visualBox;

    @FXML
    private TextField inputField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setSpeedOfScrollPane();
        resizeButton();
        visualBox.getStyleClass().add("hbox-visualBox");
        inputField.alignmentProperty().set(Pos.CENTER);
        addEffectToButtons();

    }

    public void setWindow(CustomStage window, LinearSearch linearSearch) {
        this.window = window;
        this.prevScene = window.getScene().getRoot();
        this.linearSearch = linearSearch;
    }

    private void addEffectToButtons() {
        setOnMousePressed(runButton);
        setOnMousePressed(resetButton);
        setOnMousePressed(backButton);
    }

    public void exitButton() {
        Platform.exit();
    }

    public void backButton() {
        window.setMyScene(prevScene);
    }

    public void resetButton() {
        window.setMyScene(linearSearch.getLinearSearchScene());
    }

    /**
     * Method for minimize button.
     */

    public void minimizeButton() {
        window.setIconified(true);
    }

    /**
     * Method for the resize button makes use of customstage.isfullscreen to change from maximized to restore-down.
     */

    public void resizeButton() {
        resizeButton.setOnAction(e -> {
            if (CustomStage.isFullscreen.getValue()) {
                CustomStage.isFullscreen.setValue(false);
            } else {
                CustomStage.isFullscreen.setValue(true);
            }
        });
        CustomStage.isFullscreen.addListener((property, oldValue, newValue) -> {
            if(newValue)
                resizeButton.setText("⧉");
            else
                resizeButton.setText("🗖");
        });
    }

    /**
     * Adds a little animation to the given button to make it look pressed down.
     * @param button
     */

    public static void setOnMousePressed(Button button) {
        button.setOnMousePressed(e -> button.translateYProperty().set(2));
        button.setOnMouseReleased(e -> button.translateYProperty().set(0));
    }

    /**
     * Handles the speed of the scrollPane.
     */

    public void setSpeedOfScrollPane() {
        menu.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 10;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            // Make the amount it scrolls by equal to screen size.
            scrollPane.setVvalue(vValue + -deltaY/width);
        });
    }

    /**
     * Method for the color mode slider.
     */

    public void colorMode() {
        if(colorMode.isSelected()) {
            CustomStage.isDarkmode.setValue(true);
        } else {
            CustomStage.isDarkmode.setValue(false);
        }
    }
}
