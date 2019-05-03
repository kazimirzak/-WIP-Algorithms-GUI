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
 * Creates and handles all action in the LinearSearch scene.
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
    private int numberToSearch, comparisons;
    private static boolean encounteredError = false;
    private boolean found;

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
        comparisons = 0;
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
            statusLabel.setText("Ready to search!");
            searchButton.setDisable(false);
            searchInputField.setDisable(false);
        }
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
        Thread t = new Thread(() -> doAnimation(nodeToShake, button));
        t.start();
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

    /**
     * Visualizes the array and shows it in the visual box.
     */

    private void visualizeArray() {
        statusLabel.setText("Generating Visuals...");
        initialVisualization = new VisualizeArray(visualBox);
        initialVisualization.initArray(array);
        initialVisualization.show();
        statusLabel.setText("Generating Visuals... Done!");
    }

    /**
     * Validates the input given by the user to search on, if no error are found it creates a eventQueue that is used to search on.
     * @param input
     */

    public void search(String input) {
        statusLabel.setText("Validating search input...");
        //Removes the style from the label if an error was already encountered.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }
        //If input is empty.
        if(input.length() == 0) {
            showError("The search input is empty!", searchInputField, searchButton);
        } else {
            try {
                //Takes the input and parses it to int. If no errors was found in doing this it will create the event queue and set all animation buttons to active.
                numberToSearch = Integer.parseInt(input.trim());
                statusLabel.setText("Validating search input... Done!");
                createEventQueue();
                toStart.setDisable(false);
                toEnd.setDisable(false);
                forward.setDisable(false);
                backward.setDisable(false);
                playPause.setDisable(false);
                speedSlider.setDisable(false);
            } catch(NumberFormatException e) {
                //Displays an error in the statuslabel and shakes the input field.
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

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */

    private void createEventQueue() {
        visuals = new EventQueue(speedSlider.valueProperty());
        doLinearSearch();
        Thread t = new Thread(() -> visuals.play());
        buttonSetup(visuals, t);
        t.start();
    }

    /**
     * Sets up the actions for all button and adds listeners for playing/pausing.
     * @param eq the eventQueue used.
     * @param t the thread that runs the eventQueue.
     */

    private void buttonSetup(EventQueue eq, Thread t) {
        setSliderInteraction(t);
        statusLabel.setText("Searching for " + numberToSearch + "...");
        eq.isPlaying.addListener((property, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> playPause.setText("⏸"));
                Platform.runLater(() -> statusLabel.setText("Searching for " + numberToSearch + "..."));
                playPause.setOnAction(e -> {
                    visuals.pause();
                });
            } else {
                Platform.runLater(() -> playPause.setText("▶"));
                //Checks if the eventQueue is done and if the number was found and sets the statuslabel appropriately.
               if(eq.isDone()) {
                    if(found) {
                        Platform.runLater(() -> statusLabel.setText("Found the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Did not find the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                    }
                } else {
                    statusLabel.setText(statusLabel.getText() + " Paused!");
                }
                playPause.setOnAction(e -> {
                    Thread t1 = new Thread(() -> visuals.play());
                    setSliderInteraction(t1);
                    t1.start();
                });
            }
        });
        forward.setOnAction(e -> {
            eq.next();
            if (eq.isDone()) {
                if (found) {
                    Platform.runLater(() -> statusLabel.setText("Found the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Did not find the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                }
            } else {
                Platform.runLater(() -> statusLabel.setText("Searching for " + numberToSearch + "... Paused!"));
            }
        });
        backward.setOnAction(e -> {
            eq.previous();
            if (eq.isDone()) {
                if (found) {
                    Platform.runLater(() -> statusLabel.setText("Found the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Did not find the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                }
            } else {
                Platform.runLater(() -> statusLabel.setText("Searching for " + numberToSearch + "... Paused!"));
            }
        });
        toStart.setOnAction(e -> {
            eq.toStart();
            if (eq.isDone()) {
                if (found) {
                    Platform.runLater(() -> statusLabel.setText("Found the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Did not find the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                }
            } else {
                Platform.runLater(() -> statusLabel.setText("Searching for " + numberToSearch + "... Paused!"));
            }
        });
        toEnd.setOnAction(e -> {
            eq.toEnd();
            if (eq.isDone()) {
                if (found) {
                    Platform.runLater(() -> statusLabel.setText("Found the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Did not find the number " + numberToSearch + ". Number of comparisons: " + comparisons));
                }
            } else {
                Platform.runLater(() -> statusLabel.setText("Searching for " + numberToSearch + "... Paused!"));
            }
        });
    }

    /**
     * Adds a listener to the speedSliders value to interrupt the given Thread when it is changed.
     * @param t the thread that is currently running the eventQueue.
     */

    private void setSliderInteraction(Thread t) {
        speedSlider.valueProperty().addListener((property, oldValue, newValue) -> {
            t.interrupt();
        });
    }

    /**
     * Does linearSearch on this array and creates visuals during this.
     */

    private void doLinearSearch() {
        found = false;
        for(int i = 0; i < array.length && !found; i++) {
            comparisons++;
            VisualizeArray vis = new VisualizeArray(visualBox);
            vis.initArray(array);
            if(array[i] == numberToSearch) {
                vis.setDone(i);
                found = true;
            } else
                vis.setInFocus(i);
            visuals.addEvent(vis);
        }
    }
}
