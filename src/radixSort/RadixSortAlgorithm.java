package radixSort;

import countingSort.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import templates.EventQueue;
import templates.VisualizeArray;

/**
 * Creates and handles all action in the QuickSort scene.
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class RadixSortAlgorithm {

    private Label statusLabel;
    private VBox visualBox, inputBox, countingBox, outputBox;
    private HBox inputLabelContainer, countingLabelContainer, outputLabelContainer;
    private TextField arrayInputField;
    private Button generateButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private EventQueue visuals;
    private VisualizeArray initialVisualization;
    private Thread currentThread;
    private int[] array, counting, output;
    private int comparisons, maxValue, range, highestCountingValue;
    private static boolean encounteredError = false;

    public RadixSortAlgorithm(String input, Label statusLabel, VBox visualBox, VBox inputBox, VBox countingBox, VBox outputBox, HBox inputLabelContainer,
            HBox countingLabelContainer, HBox outputLabelContainer, TextField inputField, Button generateButton, Button toStart,
            Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider) {
        this.statusLabel = statusLabel;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.visualBox = visualBox;
        this.inputBox = inputBox;
        this.countingBox = countingBox;
        this.outputBox = outputBox;
        this.inputLabelContainer = inputLabelContainer;
        this.countingLabelContainer = countingLabelContainer;
        this.outputLabelContainer = outputLabelContainer;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        comparisons = 0;
        //To clear any animation already in the visualbox.
        inputBox.getChildren().clear();
        countingBox.getChildren().clear();
        outputBox.getChildren().clear();
        inputLabelContainer.getChildren().clear();
        countingLabelContainer.getChildren().clear();
        outputLabelContainer.getChildren().clear();

        //Removes the style for showing error if the statuslabel has already shown an error.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }

        //Checks the user input.
        validateInput(input.trim());

        //If not errors were found in the input it gets ready for a input to search on.
        if (!encounteredError) {
            highestCountingValue = array.length;
            range = Arrays.stream(array).max().getAsInt();
            maxValue = range;
            counting = new int[range + 1];
            output = new int[array.length];
            counting = new int[range + 1];
            createLabels();
            setBoxConstraints();
            createEventQueue();
            statusLabel.setText("Sorting...");
            toStart.setDisable(false);
            toEnd.setDisable(false);
            forward.setDisable(false);
            backward.setDisable(false);
            playPause.setDisable(false);
            speedSlider.setDisable(false);
        }
    }

    public void stopCurrentAnimation() {
        if (visuals != null) {
            visuals.pause();
        }
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    /**
     * Validates the input given by the user.
     *
     * @param input the input given by the user.
     */
    private void validateInput(String input) {
        statusLabel.setText("Validating array Input...");
        if (!encounteredError) {
            if (input.length() == 0) {
                showError("Input is empty!", arrayInputField, generateButton);
            } else {
                try {
                    array = Arrays.stream(input.split("\\s+"))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    boolean negatives = Arrays.stream(array).anyMatch(number -> number < 0);
                    if (negatives) {
                        showError("Negative numbers are not allowed!", arrayInputField, generateButton);
                    } else {
                        statusLabel.setText("Validating array Input... Done!");
                    }
                } catch (NumberFormatException e) {
                    String[] errorMessage = e.toString().split(" ");
                    String wrongChar = errorMessage[errorMessage.length - 1];
                    showError(wrongChar + " is not allowed in array input", arrayInputField, generateButton);
                }
            }
        }
    }

    /**
     * Shows an error message.
     *
     * @param message the error message to show.
     * @param nodeToShake The node to create shake animation on.
     * @param button the button used when calling the method that the error occurred in.
     */
    private void showError(String message, Node nodeToShake, Button button) {
        encounteredError = true;
        statusLabel.setText(message);
        statusLabel.getStyleClass().add("error-message");
        currentThread = new Thread(() -> doAnimation(nodeToShake, button));
        currentThread.start();
    }

    /**
     * Creates the shake animation.
     *
     * @param node the node to shake.
     * @param button the button from which the error was created. It get disabled during the animation to stop overriding of animations.
     */
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

    private void createLabels() {
        Label input = new Label("Input-array");
        input.getStyleClass().add("label-visuals");
        Label left = new Label("Counting-Array");
        left.getStyleClass().add("label-visuals");
        Label right = new Label("Result-Array");
        right.getStyleClass().add("label-visuals");
        inputLabelContainer.getChildren().add(input);
        countingLabelContainer.getChildren().add(left);
        outputLabelContainer.getChildren().add(right);
    }

    private void setBoxConstraints() {
        inputBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        countingBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        outputBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
    }

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */
    private void createEventQueue() {
        statusLabel.setText("Generating animations...");
        visuals = new EventQueue(speedSlider.valueProperty());
        showInitialArray();
        doCountingSort();
        showEndArray();
        currentThread = new Thread(() -> visuals.play());
        buttonSetup(visuals);
        statusLabel.setText("Generating animations... Done!");
        currentThread.start();
    }

    private void showInitialArray() {
        VisualizeArray vis;
        List<VisualizeArray> events = new ArrayList<>();
        vis = new VisualizeArray(inputBox);
        vis.initArray(array);
        vis.show();
        events.add(vis);
        vis = new VisualizeArray(countingBox);
        vis.initArray(counting, highestCountingValue);
        vis.show();
        events.add(vis);
        vis = new VisualizeArray(outputBox);
        vis.initArray(output, maxValue);
        vis.show();
        events.add(vis);
        visuals.addEvent(events);
    }

    private void showEndArray() {
        VisualizeArray vis;
        List<VisualizeArray> events = new ArrayList<>();
        vis = new VisualizeArray(inputBox);
        vis.initArray(array);
        events.add(vis);
        vis = new VisualizeArray(countingBox);
        vis.initArray(counting, highestCountingValue);
        events.add(vis);
        vis = new VisualizeArray(outputBox);
        vis.initArray(output, maxValue);
        events.add(vis);
        visuals.addEvent(events);
    }

    /**
     * Sets up the actions for all button and adds listeners for playing/pausing.
     *
     * @param eq the eventQueue used.
     * @param t the thread that runs the eventQueue.
     */
    private void buttonSetup(EventQueue eq) {
        setSliderInteraction();
        statusLabel.setText("Sorting...");
        eq.isPlaying.addListener((property, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> playPause.setText("⏸"));
                Platform.runLater(() -> statusLabel.setText("Sorting..."));
                playPause.setOnAction(e -> {
                    visuals.pause();
                });
            } else {
                Platform.runLater(() -> playPause.setText("▶"));
                //Checks if the eventQueue is done and if the number was found and sets the statuslabel appropriately.
                if (eq.isDone()) {
                    Platform.runLater(() -> statusLabel.setText("Finished! Did " + comparisons + " comparisons"));
                } else {
                    statusLabel.setText(statusLabel.getText() + " Paused!");
                }
                playPause.setOnAction(e -> {
                    currentThread = new Thread(() -> visuals.play());
                    setSliderInteraction();
                    currentThread.start();
                });
            }
        });
        forward.setOnAction(e -> {
            eq.next();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " + comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        backward.setOnAction(e -> {
            eq.previous();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " + comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toStart.setOnAction(e -> {
            eq.toStart();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " + comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toEnd.setOnAction(e -> {
            eq.toEnd();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " + comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
    }

    /**
     * Adds a listener to the speedSliders value to interrupt the given Thread when it is changed.
     *
     * @param t the thread that is currently running the eventQueue.
     */
    private void setSliderInteraction() {
        speedSlider.valueProperty().addListener((property, oldValue, newValue) -> {
            currentThread.interrupt();
        });
    }

    /**
     * Does QuickSort on this array and creates visuals during this.
     */
    private void doCountingSort() {
        VisualizeArray vis;
        List<VisualizeArray> events = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            //Visuals before count
            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            vis.setInFocus(i);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setInFocus(array[i]);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();

            counting[array[i]]++;

            //Visuals after count
            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            vis.setInFocus(i);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setDone(array[i]);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();
        }
        for (int i = 1; i < counting.length; i++) {

            //Visuals before counting upwards
            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setInFocus(i);
            vis.setInFocus(i - 1);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();

            counting[i] = counting[i] + counting[i - 1];

            //Visuals after counting upwards
            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setDone(i);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();
        }
        for (int i = array.length - 1; i >= 0; i--) {

            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            vis.setInFocus(i);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setInFocus(array[i]);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();

            counting[array[i]]--;
            output[counting[array[i]]] = array[i];

            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            vis.setInFocus(i);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            vis.setInFocus(array[i]);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            vis.setInFocus(counting[array[i]]);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();

            vis = new VisualizeArray(inputBox);
            vis.initArray(array);
            events.add(vis);
            vis = new VisualizeArray(countingBox);
            vis.initArray(counting, highestCountingValue);
            events.add(vis);
            vis = new VisualizeArray(outputBox);
            vis.initArray(output, maxValue);
            vis.setDone(counting[array[i]]);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();
        }
    }
}
