package heapSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import templates.EventQueue;
import templates.TreeNode;
import templates.VisualizeArray;
import templates.VisualizeTree;

/**
 * Creates and handles all action in the QuickSort scene.
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class HeapSortAlgorithm {

    private Label statusLabel;
    private VBox visualBox, resultBox, heapBox, treeBox;
    private HBox resultLabelContainer, heapLabelContainer, treeLabelContainer;
    private TextField arrayInputField;
    private Button generateButton, toStart, toEnd, forward, backward, playPause;
    private Slider speedSlider;
    private EventQueue visuals;
    private VisualizeArray initialVisualization;
    private Thread currentThread;
    private ComboBox<String> heapType;
    private int[] array, result;
    private TreeNode[] treeArray;
    private int currentSize;
    private TreeNode root;
    private boolean isMinHeap;
    private static boolean encounteredError = false;

    public HeapSortAlgorithm(String input, Label statusLabel, VBox visualBox, VBox resultBox, VBox heapBox, VBox treeBox, HBox resultLabelContainer,
            HBox heapLabelContainer, HBox treeLabelContainer, TextField inputField, Button generateButton, Button toStart,
            Button toEnd, Button forward, Button backward, Button playPause, Slider speedSlider, ComboBox<String> heapType) {
        this.statusLabel = statusLabel;
        this.arrayInputField = inputField;
        this.generateButton = generateButton;
        this.visualBox = visualBox;
        this.resultBox = resultBox;
        this.heapBox = heapBox;
        this.treeBox = treeBox;
        this.resultLabelContainer = resultLabelContainer;
        this.heapLabelContainer = heapLabelContainer;
        this.treeLabelContainer = treeLabelContainer;
        this.toStart = toStart;
        this.toEnd = toEnd;
        this.forward = forward;
        this.backward = backward;
        this.playPause = playPause;
        this.speedSlider = speedSlider;
        this.heapType = heapType;
        //To clear any animation already in the visualbox.
        resultBox.getChildren().clear();
        heapBox.getChildren().clear();
        treeBox.getChildren().clear();
        resultLabelContainer.getChildren().clear();
        heapLabelContainer.getChildren().clear();
        treeLabelContainer.getChildren().clear();

        //Removes the style for showing error if the statuslabel has already shown an error.
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }

        //Checks the user input.
        validateInput(input.trim());

        //If not errors were found in the input it gets ready for a input to search on.
        if (!encounteredError) {
            treeArray  = new TreeNode[array.length];
            isMinHeap = heapType.getSelectionModel().getSelectedItem().equals("MinHeap");
            result = new int[array.length];
            currentSize = array.length - 1;
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
                    statusLabel.setText("Validating array Input... Done!");
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
        Label input = new Label("Result-array");
        input.getStyleClass().add("label-visuals");
        Label left = new Label("Heap-Array");
        left.getStyleClass().add("label-visuals");
        Label right = new Label("Tree-View");
        right.getStyleClass().add("label-visuals");
        resultLabelContainer.getChildren().add(input);
        heapLabelContainer.getChildren().add(left);
        treeLabelContainer.getChildren().add(right);
    }

    private void setBoxConstraints() {
        resultBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        heapBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
        treeBox.prefHeightProperty().bind(visualBox.heightProperty().multiply(0.33333));
    }

    /**
     * Creates an eventQueue based on the array that is current in this object and creates a new thread that then plays the animation.
     */
    private void createEventQueue() {
        statusLabel.setText("Generating animations...");
        visuals = new EventQueue(speedSlider.valueProperty());
        showInitialArray();
        doHeapSort();
        showEndArray();
        currentThread = new Thread(() -> visuals.play());
        buttonSetup(visuals);
        statusLabel.setText("Generating animations... Done!");
        currentThread.start();
    }

    private void showInitialArray() {
        List<Object> events = new ArrayList<>();
        VisualizeArray vis = new VisualizeArray(resultBox);
        vis.initArray(result);
        vis.show();
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        vis.show();
        events.add(vis);
        root = buildTree(array);
        VisualizeTree visualizeTree = new VisualizeTree(root, treeBox);
        visualizeTree.show();
        events.add(visualizeTree);
        visuals.addEvent(events);
    }

    private void showEndArray() {
        List<Object> events = new ArrayList<>();
        VisualizeArray vis = new VisualizeArray(resultBox);
        vis.initArray(result);
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        events.add(vis);
        root = buildTree(array);
        VisualizeTree visualizeTree = new VisualizeTree(root, treeBox);
        events.add(visualizeTree);
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

    private void doHeapSort() {
        for(int i = array.length / 2; i >= 0; i--) {
            if(isMinHeap)
                minHeapify(i);
            else
                maxHeapify(i);
        }
        for(int i = 0; i < array.length; i++) {
            result[i] = extract();

            List<Object> events = new ArrayList<>();
            VisualizeArray vis = new VisualizeArray(resultBox);
            VisualizeTree visualizeTree;
            vis.initArray(result);
            vis.setInFocus(i);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        System.out.println(Arrays.toString(result));
        }
    }

    private void minHeapify(int index) {
        List<Object> events = new ArrayList<>();
        VisualizeArray vis = new VisualizeArray(resultBox);
        VisualizeTree visualizeTree;
        vis.initArray(result);
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        vis.setInFocus(index);
        events.add(vis);
        root = buildTree(array);
        visualizeTree = new VisualizeTree(root, treeBox);
        treeArray[index].setInFocus();
        events.add(visualizeTree);
        visuals.addEvent(events);
        events.clear();

        int left = left(index);
        int right = right(index);
        int smallest;

        if(left <= currentSize && array[left] < array[index]) {
            smallest = left;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            vis.setInFocus(left(index));
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            treeArray[left(index)].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        } else {
            smallest = index;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
        if(right <= currentSize && array[right] < array[smallest]) {
            smallest = right;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            vis.setInFocus(right(index));
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            treeArray[right(index)].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
        if(smallest != index) {
            swap(index, smallest);

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setDone(smallest);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[smallest].setDone();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();

            minHeapify(smallest);
        } else {
            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setDone(index);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setDone();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
    }

    private void maxHeapify(int index) {
        List<Object> events = new ArrayList<>();
        VisualizeArray vis = new VisualizeArray(resultBox);
        VisualizeTree visualizeTree;
        vis.initArray(result);
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        vis.setInFocus(index);
        events.add(vis);
        root = buildTree(array);
        visualizeTree = new VisualizeTree(root, treeBox);
        treeArray[index].setInFocus();
        events.add(visualizeTree);
        visuals.addEvent(events);
        events.clear();

        int left = left(index);
        int right = right(index);
        int highest;

        if(left <= currentSize && array[left] > array[index]) {
            highest = left;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            vis.setInFocus(left(index));
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            treeArray[left(index)].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        } else {
            highest = index;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
        if(right <= currentSize && array[right] > array[highest]) {
            highest = right;

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setInFocus(index);
            vis.setInFocus(right(index));
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setInFocus();
            treeArray[right(index)].setInFocus();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
        if(highest != index) {
            swap(index, highest);

            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setDone(highest);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[highest].setDone();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();

            maxHeapify(highest);
        } else {
            vis = new VisualizeArray(resultBox);
            vis.initArray(result);
            events.add(vis);
            vis = new VisualizeArray(heapBox);
            vis.initArray(array);
            vis.setDone(index);
            events.add(vis);
            root = buildTree(array);
            visualizeTree = new VisualizeTree(root, treeBox);
            treeArray[index].setDone();
            events.add(visualizeTree);
            visuals.addEvent(events);
            events.clear();
        }
    }

    private int extract() {
        List<Object> events = new ArrayList<>();
        VisualizeArray vis = new VisualizeArray(resultBox);
        VisualizeTree visualizeTree;
        vis.initArray(result);
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        vis.setInFocus(0);
        vis.setInFocus(currentSize);
        events.add(vis);
        root = buildTree(array);
        visualizeTree = new VisualizeTree(root, treeBox);
        treeArray[0].setInFocus();
        treeArray[currentSize].setInFocus();
        events.add(visualizeTree);
        visuals.addEvent(events);
        events.clear();

        int number = array[0];
        array[0] = array[currentSize];
        array[currentSize] = 0;
        currentSize--;

        vis = new VisualizeArray(resultBox);
        vis.initArray(result);
        events.add(vis);
        vis = new VisualizeArray(heapBox);
        vis.initArray(array);
        vis.setInFocus(0);
        events.add(vis);
        root = buildTree(array);
        visualizeTree = new VisualizeTree(root, treeBox);
        treeArray[0].setInFocus();
        events.add(visualizeTree);
        visuals.addEvent(events);
        events.clear();

        if(isMinHeap)
            minHeapify(0);
        else
            maxHeapify(0);
        return number;
    }

    private void swap(int index1, int index2) {
        int aux = array[index1];
        array[index1] = array[index2];
        array[index2] = aux;
    }

    private TreeNode buildTree(int[] input) {
        if(currentSize > 0) {
            TreeNode root = new TreeNode(input[0]);
            treeArray[0] = root;
            buildTreeRec(root, input, 0);
            return root;
        }
        return null;
    }

    private  void buildTreeRec(TreeNode currentNode, int[] input, int index) {
        //Check currentExtractIndex to not show nodes when extracting.
        if(left(index) < input.length && currentSize >= left(index)) {
            currentNode.left = new TreeNode(input[left(index)]);
            treeArray[left(index)] = currentNode.left;
            currentNode.left.parent = currentNode;
            buildTreeRec(currentNode.left, input, left(index));
        }
        //Check currentExtractIndex to not show nodes when extracting.
        if(right(index) < input.length && currentSize >= right(index)) {
            currentNode.right = new TreeNode(input[right(index)]);
            treeArray[right(index)] = currentNode.right;
            currentNode.right.parent = currentNode;
            buildTreeRec(currentNode.right, input, right(index));
        }
    }

    private static int left(int index) {
        return 2 * index + 1;
    }

    private static int right(int index) {
        return 2 * index + 2;
    }

    private static int parent(int index) {
        return (index - 1) / 2;
    }
}
