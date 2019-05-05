package quickSort;

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
 * Creates and handles all action in the QuickSort scene.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class QuickSortAlgorithm {

    private Label statusLabel;
    private VBox visualBox;
    private TextField arrayInputField;
    private Button generateButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private EventQueue visuals;
    private VisualizeArray initialVisualization;
    private Thread currentThread;
    private int[] array;
    private int comparisons, swaps;
    private static boolean encounteredError = false;

    public QuickSortAlgorithm(String input, Label statusLabel, VBox visualBox, TextField inputField, Button generateButton,
            Button toStart, Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider) {
        this.statusLabel = statusLabel;
        this.visualBox = visualBox;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        comparisons = 0;
        swaps = 0;
        //To clear any animation already in the visualbox.
        visualBox.getChildren().clear();

        //Removes the style for showing error if the statuslabel has already shown an error.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }

        //Checks the user input.
        validateInput(input.trim());

        //If not errors were found in the input it gets ready for a input to search on.
        if (!encounteredError) {
            visualizeArray();
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
        if(currentThread != null)
            currentThread.interrupt();
        if(visuals != null)
            visuals.pause();
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

    private void visualizeArray() {
        statusLabel.setText("Initializing...");
        initialVisualization = new VisualizeArray(visualBox);
        initialVisualization.initArray(array);
        initialVisualization.show();
        statusLabel.setText("Initializing... Done!");
    }

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */

    private void createEventQueue() {
        statusLabel.setText("Generating animations...");
        visuals = new EventQueue(speedSlider.valueProperty());
        VisualizeArray vis = new VisualizeArray(visualBox);
        vis.initArray(array);
        visuals.addEvent(vis);
        doQuickSort();
        vis = new VisualizeArray(visualBox);
        vis.initArray(array);
        visuals.addEvent(vis);
        currentThread = new Thread(() -> visuals.play());
        buttonSetup(visuals);
        statusLabel.setText("Generating animations... Done!");
        currentThread.start();
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
                    Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons and " + swaps + " swaps"));
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
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons and " + swaps + " swaps"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        backward.setOnAction(e -> {
            eq.previous();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons and " + swaps + " swaps"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toStart.setOnAction(e -> {
            eq.toStart();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons and " + swaps + " swaps"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toEnd.setOnAction(e -> {
            eq.toEnd();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished! Did " +  comparisons + " comparisons and " + swaps + " swaps"));
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

    private void doQuickSort() {
        doQuickSort(0, array.length - 1);
    }

    private void doQuickSort(int p, int r) {
        if(p < r) {
            int q = partition(p, r);
            doQuickSort(p, q - 1);
            doQuickSort(q + 1, r);
        }
    }

    private int partition(int p, int r) {
        VisualizeArray vis;
        vis = new VisualizeArray(visualBox);
        vis.initArray(array);
        vis.setPivot(r);
        vis.setDisabled(p, r);
        visuals.addEvent(vis);
        int x = array[r];
        int i = p - 1;
        for(int j = p; j < r; j++) {
            vis = new VisualizeArray(visualBox);
            vis.initArray(array);
            vis.setPivot(r);
            vis.setDisabled(p, r);
            vis.setInFocus(j);
            visuals.addEvent(vis);
            comparisons++;
            if(array[j] <= x) {
                swaps++;
                i++;
                vis = new VisualizeArray(visualBox);
                vis.initArray(array);
                vis.setPivot(r);
                vis.setInFocus(i);
                vis.setInFocus(j);
                vis.setDisabled(p, r);
                visuals.addEvent(vis);
                swap(i, j);
                vis = new VisualizeArray(visualBox);
                vis.initArray(array);
                vis.setPivot(r);
                vis.setDisabled(p, r);
                vis.setDone(i);
                visuals.addEvent(vis);
            }
        }
        vis = new VisualizeArray(visualBox);
        vis.initArray(array);
        vis.setInFocus(r);
        vis.setDisabled(p, r);
        visuals.addEvent(vis);
        swap(i + 1, r);
        vis = new VisualizeArray(visualBox);
        vis.initArray(array);
        vis.setDisabled(p, r);
        vis.setDone(i + 1);
        visuals.addEvent(vis);
        return i + 1;
    }

    private void swap(int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
}
