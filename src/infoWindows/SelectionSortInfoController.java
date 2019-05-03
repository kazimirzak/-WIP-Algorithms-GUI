/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infoWindows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import templates.CustomStage;

/**
 * FXML Controller class
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class SelectionSortInfoController implements Initializable {

    private CustomStage window;
    private List<Text> textList;
    private List<String> pathForTexts;

    @FXML
    private BorderPane layout;

    @FXML
    private VBox menu;

    @FXML
    private Button backButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text textArea, arrayInputFieldText, generateText, resetText, playPauseText, backwardText, forwardText, toStartText, toEndText;

    @FXML
    private TextField arrayInputField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnMousePressed(backButton);
        initTextList();
        initPathList();
        setText();
    }

    private void initTextList() {
        textList = new ArrayList<>();
        textList.add(textArea);
        textList.add(arrayInputFieldText);
        textList.add(generateText);
        textList.add(resetText);
        textList.add(playPauseText);
        textList.add(backwardText);
        textList.add(forwardText);
        textList.add(toStartText);
        textList.add(toEndText);
    }

    private void initPathList() {
        pathForTexts = new ArrayList<>();
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\selectionSort.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\arrayInputFieldText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\generateText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\resetText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\playPauseText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\backwardText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\forwardText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\toStartText.txt");
        pathForTexts.add(System.getProperty("user.dir") + "\\src\\infoWindows\\info\\selectionSort\\toEndText.txt");
    }

    /**
     * Gets the info for the infoWindow.
     */

    private void setText() {
        for(int i = 0; i < pathForTexts.size(); i++) {
            textList.get(i).setText(getInfo(pathForTexts.get(i)));
        }
        css();
    }

    private void css() {
        //css for the window.
        layout.getStyleClass().add("infoText");

        //css for the inputFields.
        arrayInputField.getStyleClass().add("infoText-text-field");

        //css for the texts.
        textList.forEach(text -> text.getStyleClass().add("infoText"));

        //Wrapping width for all texts.
        textList.forEach(text -> text.setWrappingWidth(CustomStage.screenSizeWidth * 0.35));
    }

    /**
     * Gets the info from the mainMenuInfo.txt.
     * @return Returns a string with the text from the .txt file.
     */

    private String getInfo(String path) {
        String result = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
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


}
