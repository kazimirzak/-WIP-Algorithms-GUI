package mainMenu;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import templates.CustomStage;


/**
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class MainMenuController implements Initializable {

    private final CustomStage window = MainMenu.window;

    @FXML
    private VBox menu;

    @FXML
    private Button exit, resizeButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ToggleSwitch colorMode;

    @FXML
    private BorderPane layout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createMenu();
        setOnMousePressed(exit);
        setSpeedOfScrollPane();
        resizeButton();
    }

    /**
     * Creates the menu using the text files found in /data.
     */

    public void createMenu() {
        Path pathForCatagories = Paths.get(MainMenu.textDirectory + "/Categories.txt");
        try {
            //Gets the catagories for main menu.
            Files.lines(pathForCatagories).forEach(category -> {
                menu.getChildren().add(getLabel(category));
                Path pathForAlgorithms = Paths.get(MainMenu.textDirectory + "/" + category + ".txt");
                try {
                    //Gets for each catagory the associated algorithms.
                    Files.lines(pathForAlgorithms).forEach(algorithm -> {
                        menu.getChildren().add(getButton(algorithm));
                    });
                } catch (IOException ex) {
                    menu.getChildren().add(new Label("Error fetching algorithms"));
                }
            });
        } catch (IOException ex) {
            menu.getChildren().add(new Label("Error fetching catagories"));
        }

        //Removes the exit button and then puts it last.
        menu.getChildren().add(menu.getChildren().remove(0));

    }

    /**
     * Returns a label with the given category.
     * @param category
     * @return
     */

    private static Label getLabel(String category) {
        Label label = new Label(category);
        label.getStyleClass().add("label-catagory");
        return label;
    }

    /**
     * Returns a button with the onclick button fitting to the given algorithm.
     * @param algorithm
     * @return
     */

    private static Button getButton(String algorithm) {
        Button button = new Button(algorithm);
        setOnMousePressed(button);
        button.setPrefWidth(300);
        button.getStyleClass().add("button-algorithm");
        switch(algorithm) {
            default:
                //do nothing
                break;
        }
        return button;
    }

    /**
     * Method for close button.
     */

    public void closeButton() {
        window.close();
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
