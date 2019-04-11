package linearSearch;

import java.util.Arrays;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import templates.EventQueue;
import templates.VisualizeArray;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class LinearSearchAlgorithm {

    private Label statusLabel;
    private VBox visualBox;
    private TextField arrayInputField, searchInputField;
    private Button generateButton, searchButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private VisualizeArray initialVisualization;
    private EventQueue visuals;
    private int[] array;
    private int numberToSearch;
    private static boolean encounteredError = false;

    public LinearSearchAlgorithm(String input, Label statusLabel, VBox visualBox, TextField inputField, Button generateButton,
            Button searchButton, TextField searchInputField, Button toStart, Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider) {
        this.statusLabel = statusLabel;
        this.visualBox = visualBox;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.searchInputField = searchInputField;
        this.searchButton = searchButton;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        visualBox.getChildren().clear();
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }
        validateInput(input.trim());
        if (!encounteredError) {
            visualizeArray();
            statusLabel.setText("Ready to search!");
            searchButton.setDisable(false);
            searchInputField.setDisable(false);
        }
    }

    private void validateInput(String input) {
        statusLabel.setText("Validating array Input...");
        if (input.length() == 0) {
            showError("Input is empty!", arrayInputField, generateButton);
        } else {
            try {
                array = Arrays.stream(input.split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                statusLabel.setText("Validating array Input... Done!");
            } catch (NumberFormatException e) {
                String[] errorMessage = e.toString().split(" ");
                String wrongChar = errorMessage[errorMessage.length - 1];
                showError(wrongChar + " is not allowed in array input", arrayInputField, generateButton);
            }
        }
    }

    private void showError(String message, Node nodeToShake, Button button) {
        encounteredError = true;
        statusLabel.setText(message);
        statusLabel.getStyleClass().add("error-message");
        Thread t = new Thread(() -> doAnimation(nodeToShake, button));
        t.start();
    }

    private void doAnimation(Node node, Button button) {
        button.setDisable(true);
        long duration = 50L;
        TranslateTransition moveInputField = new TranslateTransition(Duration.millis(duration), node);
        moveInputField.setAutoReverse(true);
        moveInputField.setCycleCount(6);
        synchronized (moveInputField) {
            moveInputField.setFromX(0);
            moveInputField.setFromY(0);
            moveInputField.setByX(5);
            moveInputField.setByY(5);
            moveInputField.setToX(-5);
            moveInputField.setToY(-5);
            moveInputField.playFromStart();
            while (moveInputField.getStatus() == Animation.Status.RUNNING) {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }
        button.setDisable(false);
    }

    private void visualizeArray() {
        statusLabel.setText("Generating Visuals...");
        initialVisualization = new VisualizeArray(visualBox);
        initialVisualization.initArray(array);
        initialVisualization.show();
        statusLabel.setText("Generating Visuals... Done!");
    }

    public void search(String input) {
        statusLabel.setText("Validating search input...");
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }
        if(input.length() == 0) {
            showError("The search input is empty!", searchInputField, searchButton);
        } else {
            try {
                numberToSearch = Integer.parseInt(input.trim());
                System.out.println(numberToSearch);
                statusLabel.setText("Validating search input... Done!");
                createEventQueue();
                toStart.setDisable(false);
                toEnd.setDisable(false);
                forward.setDisable(false);
                backward.setDisable(false);
                playPause.setDisable(false);
                speedSlider.setDisable(false);
            } catch(NumberFormatException e) {
                String[] errorMessage = e.toString().split(" ");
                System.out.println(Arrays.toString(errorMessage));
                String wrongChar = "";
                for(int i = 4; i < errorMessage.length; i++) {
                    wrongChar+= " " + errorMessage[i];
                }
                showError(wrongChar + " is not allowed in search input", searchInputField, searchButton);
            }
        }
    }

    private void createEventQueue() {
        visuals = new EventQueue();
        visuals.addEvent(initialVisualization);
        doLinearSearch();
        buttonSetup(visuals);
        Thread t = new Thread(() -> visuals.play());
        t.start();
    }

    private void buttonSetup(EventQueue eq) {
        eq.isPlaying.addListener((property, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> playPause.setText("⏸"));
                playPause.setOnAction(e -> {
                    visuals.pause();
                });
            } else {
                playPause.setText("▶");
                playPause.setOnAction(e -> {
                    Thread t = new Thread(() -> visuals.play());
                    t.start();
                });
            }
        });
        forward.setOnAction(e -> eq.next());
        backward.setOnAction(e -> eq.previous());
        toStart.setOnAction(e -> eq.toStart());
        toEnd.setOnAction(e -> eq.toEnd());
    }

    private void doLinearSearch() {
        boolean found = false;
        for(int i = 0; i < array.length && !found; i++) {
            VisualizeArray vis = new VisualizeArray(visualBox);
            vis.initArray(array);
            if(array[i] == numberToSearch) {
                vis.setFound(i);
                found = true;
            } else
                vis.setInFocus(i);
            visuals.addEvent(vis);
        }
    }
}
