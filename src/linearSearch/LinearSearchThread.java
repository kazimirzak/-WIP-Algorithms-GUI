package linearSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class LinearSearchThread {

    private Label statusLabel;
    private HBox visualBox;
    private TextField inputField;
    private Button generateButton;
    private int[] array;
    private static boolean encounteredError = false;
    private int maxValue, minValue;

    public LinearSearchThread(String input, Label statusLabel, HBox visualBox, TextField inputField, Button generateButton) {
        this.statusLabel = statusLabel;
        this.visualBox = visualBox;
        this.inputField = inputField;
        this.generateButton = generateButton;
        visualBox.getChildren().clear();
        if (encounteredError) {
            statusLabel.getStyleClass().remove(statusLabel.getStyleClass().size() - 1);
            encounteredError = false;
        }
        statusLabel.setText("Validating Input...");
        validateInput(input.trim());
        if (!encounteredError) {
            visualizeArray();
            statusLabel.setText("Ready to search!");
        }

    }

    private void validateInput(String input) {
        if (input.length() == 0) {
            showError("Input is empty!");

        } else {
            try {
                array = Arrays.stream(input.split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                minValue = getMinValue();
                maxValue = getMaxValue();
                statusLabel.setText("Validating Input... Done!");
            } catch (NumberFormatException e) {
                String[] errorMessage = e.toString().split(" ");
                String wrongChar = errorMessage[errorMessage.length - 1];
                showError(wrongChar + " is not allowed in input");
            }
        }
    }

    private int getMinValue() {
        return Arrays.stream(array).min().getAsInt();
    }

    private int getMaxValue() {
        return Arrays.stream(array).max().getAsInt();
    }

    private void visualizeArray() {
        statusLabel.setText("Generating Array...");
        List<Integer> scaleArray = getScaleArray();
        for (int i = 0; i < array.length; i++) {
            StackPane arrayIndex = getVisualIndex(array[i], i);
            //((double) (scaleArray.indexOf(array[i]) + 1) / (double) scaleArray.size())
            double multiplier = relativeSizeToMaxMin(array[i]);
            Rectangle rec = (Rectangle) arrayIndex.getChildren().get(0);
            rec.heightProperty().bind(visualBox.heightProperty().subtract(30).multiply(multiplier));
            visualBox.getChildren().add(arrayIndex);
        }
        statusLabel.setText("Generating Array... Done!");
    }

    private StackPane getVisualIndex(int number, int index) {
        int rectangleWidth = 30;
        Rectangle rec = new Rectangle(rectangleWidth, 1);
        rec.getStyleClass().add("rectangle-standard");
        VBox labelContainer = new VBox(5);
        labelContainer.setAlignment(Pos.BOTTOM_CENTER);
        Label num = new Label(number + "");
        Label ind = new Label("(" + index + ")");
        labelContainer.getChildren().addAll(num, ind);
        StackPane result = new StackPane(rec, labelContainer);
        result.setAlignment(Pos.BOTTOM_CENTER);
        return result;
    }

    private double relativeSizeToMaxMin(int number) {
        double result = 0;
        int spectrum = 0;
        if (minValue < 0) {
            spectrum = Math.abs(maxValue) + Math.abs(minValue);
            result = (double) (number + Math.abs(minValue) + 1) / (double) spectrum;
            if (result > 1) {
                result = 1;
            }
        } else {
            spectrum = maxValue;
            result = (double) number / (double) spectrum;
        }
        return result;
    }

    private List<Integer> getScaleArray() {
        Set<Integer> set = new HashSet<>();
        for (int number : array) {
            set.add(number);
        }
        List<Integer> result = new ArrayList<>(set);
        Collections.sort(result);
        return result;
    }

    private void showError(String message) {
        encounteredError = true;
        statusLabel.setText(message);
        statusLabel.getStyleClass().add("error-message");
        Thread t = new Thread(() -> doAnimation());
        t.start();
    }

    private void doAnimation() {
        generateButton.setDisable(true);
        Long duration = 50L;
        TranslateTransition moveInputField = new TranslateTransition(Duration.millis(duration), inputField);
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
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
        generateButton.setDisable(false);
    }
}
