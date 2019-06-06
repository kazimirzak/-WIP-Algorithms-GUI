package radixSort;

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
import templates.Showable;
import templates.VisualizeArray;
import templates.VisualizeStringList;

/**
 * Creates and handles all action in the QuickSort scene.
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class RadixSortAlgorithm {

    private Label statusLabel;
    private VBox visualBox, arrayBox;
    private HBox arrayLabelContainer, numberLabelContainer, numberBox;
    private TextField arrayInputField;
    private Button generateButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private EventQueue visuals;
    private Thread currentThread;
    private int[] array;
    private String[] stringArray;
    private VisualizeStringList earlierVisual;
    private int maxDigits;
    private static boolean encounteredError = false;

    public RadixSortAlgorithm(String input, Label statusLabel, VBox visualBox, VBox arrayBox, HBox numberBox, HBox arrayLabelContainer,
            HBox numberLabelContainer, TextField inputField, Button generateButton, Button toStart,
            Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider) {
        this.statusLabel = statusLabel;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.visualBox = visualBox;
        this.arrayBox = arrayBox;
        this.numberBox = numberBox;
        this.arrayLabelContainer = arrayLabelContainer;
        this.numberLabelContainer = numberLabelContainer;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        //To clear any animation already in the visualbox.
        arrayBox.getChildren().clear();
        numberBox.getChildren().clear();
        arrayLabelContainer.getChildren().clear();
        numberLabelContainer.getChildren().clear();

        //Removes the style for showing error if the statuslabel has already shown an error.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }

        //Checks the user input.
        validateInput(input.trim());

        //If not errors were found in the input it gets ready for a input to search on.
        if (!encounteredError) {
            maxDigits = getDigits(Arrays.stream(array).max().getAsInt());
            padStringArrayWithZeros();
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
                    stringArray = input.split("\\s+");
                    array = Arrays.stream(stringArray)
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

    private void padStringArrayWithZeros() {
        for(int i = 0; i < array.length; i++) {
            int digits = getDigits(array[i]);
            if(digits < maxDigits) {
                String string = "";
                for(int j = digits; j < maxDigits; j++) {
                    string += "0";
                }
                string += array[i];
                stringArray[i] = string;
            }
        }
    }

    private void createLabels() {
        Label arrayLabel = new Label("Array-View");
        arrayLabel.getStyleClass().add("label-visuals");
        Label numberLabel = new Label("Number-View");
        numberLabel.getStyleClass().add("label-visuals");
        arrayLabelContainer.getChildren().add(arrayLabel);
        numberLabelContainer.getChildren().add(numberLabel);
    }

    private void setBoxConstraints() {
        arrayBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        numberBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.66666));
    }

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */
    private void createEventQueue() {
        statusLabel.setText("Generating animations...");
        visuals = new EventQueue(speedSlider.valueProperty());
        showInitialArray();
        doRadixSort();
        showEndArray();
        currentThread = new Thread(() -> visuals.play());
        buttonSetup(visuals);
        statusLabel.setText("Generating animations... Done!");
        currentThread.start();
    }

    private void showInitialArray() {
        VisualizeStringList stringVis;
        VisualizeArray vis;
        List<Showable> events = new ArrayList<>();
        stringVis = new VisualizeStringList(numberBox);
        stringVis.initArray(stringArray);
        stringVis.show();
        earlierVisual = stringVis;
        events.add(stringVis);
        vis = new VisualizeArray(arrayBox);
        vis.initArray(array);
        vis.show();
        events.add(vis);
        visuals.addEvent(events);
    }

    private void showEndArray() {
        VisualizeStringList stringVis;
        VisualizeArray vis;
        List<Showable> events = new ArrayList<>();
        stringVis = new VisualizeStringList(numberBox);
        stringVis.addVisuals(earlierVisual);
        stringVis.initArray(stringArray);
        earlierVisual = stringVis;
        events.add(stringVis);
        vis = new VisualizeArray(arrayBox);
        vis.initArray(array);
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
                    Platform.runLater(() -> statusLabel.setText("Finished!"));
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
                Platform.runLater(() -> statusLabel.setText("Finished!"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        backward.setOnAction(e -> {
            eq.previous();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished!"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toStart.setOnAction(e -> {
            eq.toStart();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished!"));
            } else {
                Platform.runLater(() -> statusLabel.setText("Sorting... Paused!"));
            }
        });
        toEnd.setOnAction(e -> {
            eq.toEnd();
            if (eq.isDone()) {
                Platform.runLater(() -> statusLabel.setText("Finished!"));
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

    private void doRadixSort() {
        for(int i = 0; i < maxDigits; i++) {
            int[] digits = new int[stringArray.length];
            for(int j = 0; j < stringArray.length; j++) {
                String string = stringArray[j];
                digits[j] = Integer.parseInt(string.substring(string.length() - 1 - i, string.length() - i));
            }
            bubbleSort(digits, i);
            //if statement to make it not show the end.
            if(i != maxDigits - 1) {
                List<Showable> events = new ArrayList<>();
                VisualizeStringList stringVis = new VisualizeStringList(numberBox);
                stringVis.addVisuals(earlierVisual);
                stringVis.initArray(stringArray);
                earlierVisual = stringVis;
                events.add(stringVis);
                VisualizeArray vis = new VisualizeArray(arrayBox);
                vis.initArray(array);
                events.add(vis);
                visuals.addEvent(events);
            }
        }
    }

    private void bubbleSort(int[] digits, int currentDigit) {
        VisualizeArray vis;
        VisualizeStringList stringVis;
        List<Showable> events = new ArrayList<>();
        for (int i = 0; i < digits.length; i++) {
            for (int j = digits.length - 1; j > i; j--) {
                stringVis = new VisualizeStringList(numberBox);
                stringVis.addVisuals(earlierVisual);
                stringVis.initArray(stringArray);
                stringVis.setCurrentDigitInFocus(currentDigit);
                stringVis.setCurrentDigitAndIndexInFocus(currentDigit, j);
                stringVis.setCurrentDigitAndIndexInFocus(currentDigit, j - 1);
                events.add(stringVis);
                vis = new VisualizeArray(arrayBox);
                vis.initArray(array);
                vis.setInFocus(j);
                vis.setInFocus(j - 1);
                events.add(vis);
                visuals.addEvent(events);
                events.clear();
                if (digits[j] < digits[j - 1]) {
                    swap(digits ,j, j - 1);
                    stringVis = new VisualizeStringList(numberBox);
                    stringVis.addVisuals(earlierVisual);
                    stringVis.initArray(stringArray);
                    stringVis.setCurrentDigitInFocus(currentDigit);
                    stringVis.setCurrentDigitAndIndexInFocus(currentDigit, j);
                    stringVis.setCurrentDigitAndIndexInFocus(currentDigit, j - 1);
                    events.add(stringVis);
                    vis = new VisualizeArray(arrayBox);
                    vis.initArray(array);
                    vis.setInFocus(j);
                    vis.setInFocus(j - 1);
                    events.add(vis);
                    visuals.addEvent(events);
                    events.clear();
                }
            }
            stringVis = new VisualizeStringList(numberBox);
            stringVis.addVisuals(earlierVisual);
            stringVis.initArray(stringArray);
            stringVis.setCurrentDigitInFocus(currentDigit);
            stringVis.setCurrentDigitAndIndexDone(currentDigit, i);
            events.add(stringVis);
            vis = new VisualizeArray(arrayBox);
            vis.initArray(array);
            vis.setDone(i);
            events.add(vis);
            visuals.addEvent(events);
            events.clear();
        }
    }

    private void swap(int[] digits, int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
        temp = digits[index1];
        digits[index1] = digits[index2];
        digits[index2] = temp;
        String aux = stringArray[index1];
        stringArray[index1] = stringArray[index2];
        stringArray[index2] = aux;
    }

    private static int getDigits(int number) {
        int count = 0;
        while(number > 0) {
            number = number / 10;
            count++;
        }
        return count;
    }
}
