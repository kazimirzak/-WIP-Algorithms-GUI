/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infoWindows;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import templates.CustomAlertBox;
import templates.CustomStage;

/**
 * FXML Controller class
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class MainMenuInfoController implements Initializable {

    private CustomStage window;

    @FXML
    private BorderPane layout;

    @FXML
    private VBox menu;

    @FXML
    private Button backButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text textArea;

    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnMousePressed(backButton);
        setText();
    }

    /**
     * Gets the info for the infoWindow.
     */

    private void setText() {
        textArea.setText(getInfo());
        textArea.setWrappingWidth(CustomStage.screenSizeWidth * 0.35);
        layout.getStyleClass().add("infoText");
        textArea.getStyleClass().add("infoText");
    }

    /**
     * Gets the info from the mainMenuInfo.txt.
     * @return Returns a string with the text from the .txt file.
     */

    private String getInfo() {
        String result = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\mainMenuInfo.txt"));
            result = lines.stream().map((line) -> line + "\n").reduce(result, String::concat);
        } catch (IOException e) {
            System.out.println(e);
            result = "Error reading info file!";
        }
        return result;
    }

    /**
     * Sets the window of this controller.
     * @param window
     */

    public void setWindow(CustomStage window) {
        this.window = window;
    }

    /**
     * Action for the exitButton.
     */

    public void exitButton() {
        window.close();
    }

    /**
     * Action for the closeButton.
     */

    public void closeButton() {
        window.close();
    }

    /**
     * Action for the minimizeButton.
     */

    public void minimizeButton() {
        window.setIconified(true);
    }

    /**
     * Sets the speed of the scrollPane.
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
     * Adds a clicked effect to the given button.
     * @param button the button to add the effect to.
     */

    public static void setOnMousePressed(Button button) {
        button.setOnMousePressed(e -> button.translateYProperty().set(2));
        button.setOnMouseReleased(e -> button.translateYProperty().set(0));
    }

    /**
     * Action for when the hyperlink is clicked.
     */

    public void hyperlink() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/kazimirzak/Algorithms2.0"));
        } catch (URISyntaxException | IOException e) {
            System.out.println(e);
            CustomAlertBox alert = new CustomAlertBox();
            alert.showAlertBox("Error opening browser!");
        }
    }
}
