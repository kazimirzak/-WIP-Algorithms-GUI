package heapQueue;

import infoWindows.infoWindow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import templates.CustomStage;

/**
 * FXML Controller class
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class HeapQueueController implements Initializable {

    private CustomStage window;
    private Parent prevScene;
    private HeapQueue insertionSort;
    private HeapQueueAlgorithm visualizer;
    private static String defaultHeapType  = "Max Heap";

    @FXML
    private Button resizeButton, generateButton, resetButton, backButton, toStart, toEnd, forward, backward,
            playPause, add, moveUp, moveDown, remove, clearAll;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ToggleSwitch colorMode;

    @FXML
    private VBox heapBox, treeBox, visualBox;

    @FXML
    private HBox heapLabelContainer, treeLabelContainer, gridBox;

    @FXML
    private TextField arrayInputField, numberInputField;

    @FXML
    private Label statusLabel;

    @FXML
    private Slider speedSlider;

    @FXML
    private ComboBox<String> heapType, commands;

    @FXML
    private ListView listView;

    @FXML
    private GridPane arrayInputGrid, commandGrid;

    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colorMode.setSelected(CustomStage.isDarkmode.getValue());
        setSpeedOfScrollPane();
        resizeButton();
        visualBox.getStyleClass().add("vbox-visualBox");
        addEffectToButtons();
        initComboBox();
        initInputArray();
        initListView();
    }

    /**
     * Sets the window, prevScene and the insertionSort used in this controller.
     * @param window
     * @param prevScene
     * @param insertionSort
     */

    public void setWindow(CustomStage window, Parent prevScene, HeapQueue insertionSort) {
        this.window = window;
        this.prevScene = prevScene;
        this.insertionSort = insertionSort;
    }

    /**
     * Adds the clicked effect to all buttons mentioned.
     */

    private void addEffectToButtons() {
        setOnMousePressed(generateButton);
        setOnMousePressed(resetButton);
        setOnMousePressed(backButton);
        setOnMousePressed(toStart);
        setOnMousePressed(toEnd);
        setOnMousePressed(forward);
        setOnMousePressed(backward);
        setOnMousePressed(playPause);
        setOnMousePressed(add);
        setOnMousePressed(moveUp);
        setOnMousePressed(moveDown);
        setOnMousePressed(remove);
        setOnMousePressed(clearAll);

        add.prefWidthProperty().bind(moveDown.widthProperty());
        moveUp.prefWidthProperty().bind(moveDown.widthProperty());
        remove.prefWidthProperty().bind(moveDown.widthProperty());
        clearAll.prefWidthProperty().bind(moveDown.widthProperty());
    }

    private void initComboBox() {
        ObservableList<String> types = FXCollections.observableArrayList("Max Heap", "Min Heap");
        heapType.getItems().addAll(types);
        heapType.getSelectionModel().select(defaultHeapType);

        heapType.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(!oldValue.equals(newValue)) {
                defaultHeapType = newValue;
                resetButton();
            }
        }));

        ObservableList<String> commandsList = FXCollections.observableArrayList("Insert Key", "Extract Min/Max Key", "Increase Key", "Decrease Key");
        commands.getItems().addAll(commandsList);
        commands.getSelectionModel().select("Insert Key");
    }

    private void initInputArray() {
        arrayInputField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue.length() == 0) {
                arrayInputField.setAlignment(Pos.CENTER);
            } else {
                arrayInputField.setAlignment(Pos.TOP_LEFT);
            }
        }));

        arrayInputField.prefHeightProperty().bind(arrayInputGrid.heightProperty().subtract(generateButton.heightProperty()).subtract(12));
    }

    private void initListView() {
        listView.setPrefWidth(Double.MAX_VALUE);
    }

    /**
     * Action for exitButton.
     */

    public void exitButton() {
        Platform.exit();
    }

    /**
     * Action for backButton.
     */

    public void backButton() {
        window.setMyScene(prevScene);
    }

    /**
     * Action for resetButton.
     */

    public void resetButton() {
        window.setMyScene(insertionSort.getScene());
    }

    /**
     * Action for minimizeButton.
     */

    public void minimizeButton() {
        window.setIconified(true);
    }

    /**
     * Creates all actions for the maximize/restore down button.
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
     * Adds the pressed effect to the given button.
     * @param button the button to add the effect to.
     */

    public static void setOnMousePressed(Button button) {
        button.setOnMousePressed(e -> button.translateYProperty().set(2));
        button.setOnMouseReleased(e -> button.translateYProperty().set(0));
    }

    /**
     * Sets the speed of the vertical scrollPane.
     */

    public void setSpeedOfScrollPane() {
        visualBox.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 10;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            // Make the amount it scrolls by equal to screen size.
            scrollPane.setVvalue(vValue + -deltaY / width);
        });
    }

    /**
     * Action for the colorMode selector.
     */

    public void colorMode() {
        if(colorMode.isSelected()) {
            CustomStage.isDarkmode.setValue(true);
        } else {
            CustomStage.isDarkmode.setValue(false);
        }
    }

    /**
     * Actions for the generateButton.
     */

    public void generateButton() {
        if(visualizer != null) {
            visualizer.stopCurrentAnimation();
        }
        String input = arrayInputField.getText();
        visualizer = new HeapQueueAlgorithm(input, statusLabel, visualBox, heapBox, treeBox, heapLabelContainer,
                treeLabelContainer, arrayInputField, generateButton, toStart, toEnd, forward, backward, playPause, speedSlider, heapType);
    }

    /**
     * Action for the helpButton
     */

    public void helpButton() {
        infoWindow info = new infoWindow("Heap Sort");
    }
}
