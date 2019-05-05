package mergeSort;

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
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class MergeSortAlgorithm {

    private Label statusLabel;
    private VBox visualBox, inputBox, leftBox, rightBox;
    private HBox inputLabelContainer, leftLabelContainer, rightLabelContainer;
    private TextField arrayInputField;
    private Button generateButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private EventQueue visuals;
    private VisualizeArray initialVisualization;
    private Thread currentThread;
    private int[] array;
    private int comparisons, maxValue;
    private static boolean encounteredError = false;

    public MergeSortAlgorithm(String input, Label statusLabel, VBox visualBox, VBox inputBox, VBox leftBox, VBox rightBox, HBox inputLabelContainer, HBox leftLabelContainer, HBox rightLabelContainer, TextField inputField, Button generateButton,
            Button toStart, Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider) {
        this.statusLabel = statusLabel;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.visualBox = visualBox;
        this.inputBox = inputBox;
        this.leftBox = leftBox;
        this.rightBox = rightBox;
        this.inputLabelContainer = inputLabelContainer;
        this.leftLabelContainer = leftLabelContainer;
        this.rightLabelContainer = rightLabelContainer;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        comparisons = 0;
        //To clear any animation already in the visualbox.
        inputBox.getChildren().clear();
        leftBox.getChildren().clear();
        rightBox.getChildren().clear();
        inputLabelContainer.getChildren().clear();
        leftLabelContainer.getChildren().clear();
        rightLabelContainer.getChildren().clear();

        //Removes the style for showing error if the statuslabel has already shown an error.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }

        //Checks the user input.
        validateInput(input.trim());

        //If not errors were found in the input it gets ready for a input to search on.
        if (!encounteredError) {
            maxValue = Arrays.stream(array).max().getAsInt();
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
        if(visuals != null)
            visuals.pause();
        if(currentThread != null)
            currentThread.interrupt();
    }

    /**
     * Validates the input given by the user.
     * @param input the input given by the user.
     */

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

    /**
     * Shows an error message.
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
        Label input = new Label("Array");
        input.getStyleClass().add("label-visuals");
        Label left = new Label("Left Sub-Array");
        left.getStyleClass().add("label-visuals");
        Label right = new Label("Right Sub-Array");
        right.getStyleClass().add("label-visuals");
        inputLabelContainer.getChildren().add(input);
        leftLabelContainer.getChildren().add(left);
        rightLabelContainer.getChildren().add(right);
    }

    private void setBoxConstraints() {
        inputBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        leftBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        rightBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
    }

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */

    private void createEventQueue() {
        statusLabel.setText("Generating animations...");
        visuals = new EventQueue(speedSlider.valueProperty());
        showInitialArray();
        doMergeSort();
        showEndArray();
        currentThread = new Thread(() -> visuals.play());
        buttonSetup(visuals);
        statusLabel.setText("Generating animations... Done!");
        currentThread.start();
    }

    private void showInitialArray() {
        VisualizeArray vis;
        int[] dummyArray = new int[0];
        List<VisualizeArray> events = new ArrayList<>();
        vis = new VisualizeArray(inputBox);
        vis.initArray(array);
        vis.show();
        events.add(vis);
        vis = new VisualizeArray(leftBox);
        vis.initArray(dummyArray);
        vis.show();
        events.add(vis);
        vis = new VisualizeArray(rightBox);
        vis.initArray(dummyArray);
        vis.show();
        events.add(vis);
        visuals.addEvent(events);
    }

    private void showEndArray() {
        VisualizeArray vis;
        int[] dummyArray = new int[0];
        List<VisualizeArray> events = new ArrayList<>();
        vis = new VisualizeArray(inputBox);
        vis.initArray(array, maxValue);
        events.add(vis);
        vis = new VisualizeArray(leftBox);
        vis.initArray(dummyArray);
        events.add(vis);
        vis = new VisualizeArray(rightBox);
        vis.initArray(dummyArray);
        events.add(vis);
        visuals.addEvent(events);
    }

    /**
     * Sets up the actions for all button and adds listeners for playing/pausing.
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
                    Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons"));
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
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        backward.setOnAction(e -> {
            eq.previous();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toStart.setOnAction(e -> {
            eq.toStart();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toEnd.setOnAction(e -> {
            eq.toEnd();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
    }

    /**
     * Adds a listener to the speedSliders value to interrupt the given Thread when it is changed.
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

    private void doMergeSort() {
        mergeSortRecursive(0, array.length - 1);
    }

    private void mergeSortRecursive(int p, int r) {
        if(p < r) {
            int q = (p + r) / 2;
            List<VisualizeArray> events = new ArrayList<>();
            int[] dummyArray = new int[0];
            VisualizeArray input = new VisualizeArray(inputBox);
            input.initArray(array, maxValue);
            input.setDisabled(p, q);
            events.add(input);
            VisualizeArray left = new VisualizeArray(leftBox);
            left.initArray(dummyArray);
            events.add(left);
            VisualizeArray right = new VisualizeArray(rightBox);
            right.initArray(dummyArray);
            events.add(right);
            visuals.addEvent(events);
            events.clear();
            mergeSortRecursive(p, q);
            input = new VisualizeArray(inputBox);
            input.initArray(array, maxValue);
            input.setDisabled(q + 1, r);
            events.add(input);
            left = new VisualizeArray(leftBox);
            left.initArray(dummyArray);
            events.add(left);
            right = new VisualizeArray(rightBox);
            right.initArray(dummyArray);
            events.add(right);
            visuals.addEvent(events);
            events.clear();
            mergeSortRecursive(q + 1, r);
            merge(p, q, r);
        }
    }

    private void merge(int p, int q, int r) {
        List<VisualizeArray> events = new ArrayList<>();
        int n1 = q - p + 1;
        int n2 = r - q;

        int[] left = new int[n1];
        int[] right = new int[n2];

        for(int i = 0; i < n1; i++) {
            left[i] = array[p + i];
        }
        for(int i = 0; i < n2; i++) {
            right[i] = array[q + 1 + i];
        }

        int i = 0;
        int j = 0;

        for(int k = p; k <= r; k++) {
            comparisons++;
            events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
            events.add(getAuxArrayVisuals(leftBox, left, i, -1));
            events.add(getAuxArrayVisuals(rightBox, right, j, -1));
            visuals.addEvent(events);
            events.clear();
            if(i == n1) {
                events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
                events.add(getAuxArrayVisuals(leftBox, left, i, -1));
                events.add(getAuxArrayVisuals(rightBox, right, -1, j));
                visuals.addEvent(events);
                events.clear();
                array[k] = right[j];
                j++;
            } else if(j == n2) {
                events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
                events.add(getAuxArrayVisuals(leftBox, left, -1, i));
                events.add(getAuxArrayVisuals(rightBox, right, j, -1));
                visuals.addEvent(events);
                events.clear();
                array[k] = left[i];
                i++;
            } else if(left[i] <= right[j]) {
                events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
                events.add(getAuxArrayVisuals(leftBox, left, -1, i));
                events.add(getAuxArrayVisuals(rightBox, right, j, -1));
                visuals.addEvent(events);
                events.clear();
                array[k] = left[i];
                i++;
            } else {
                events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
                events.add(getAuxArrayVisuals(leftBox, left, i, -1));
                events.add(getAuxArrayVisuals(rightBox, right, -1, j));
                visuals.addEvent(events);
                events.clear();
                array[k] = right[j];
                j++;
            }
            events.add(getInputArrayVisuals(inputBox, array, -1, k, p, r, maxValue));
            events.add(getAuxArrayVisuals(leftBox, left, i, -1));
            events.add(getAuxArrayVisuals(rightBox, right, j, -1));
            visuals.addEvent(events);
            events.clear();
        }
    }

    private static VisualizeArray getAuxArrayVisuals(VBox box, int[] arr, int inFocus, int done) {
        VisualizeArray vis = new VisualizeArray(box);
        vis.initArray(arr);
        if(inFocus < arr.length && inFocus != -1)
            vis.setInFocus(inFocus);
        if(done != - 1)
            vis.setDone(done);
        return vis;
    }

    private static VisualizeArray getInputArrayVisuals(VBox box, int[] arr, int inFocus, int done, int leftIndex, int rightIndex, int maxValue) {
        VisualizeArray vis = new VisualizeArray(box);
        vis.initArray(arr, maxValue);
        if(inFocus != -1)
            vis.setInFocus(inFocus);
        if(done != -1)
            vis.setDone(done);
        vis.setDisabled(leftIndex, rightIndex);
        return vis;
    }
}
