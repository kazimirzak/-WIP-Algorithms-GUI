package linearSearch;

import java.util.Arrays;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import templates.VisualizeArray;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class LinearSearchThread {

    private Label statusLabel;
    private VBox visualBox;
    private TextField inputField;
    private Button generateButton;
    private int[] array;
    private static boolean encounteredError = false;

    public LinearSearchThread(String input, Label statusLabel, VBox visualBox, TextField inputField, Button generateButton) {
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
            VisualizeArray visuailizer = new VisualizeArray(visualBox);
            visuailizer.initArray(array);
            visuailizer.show();
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
                statusLabel.setText("Validating Input... Done!");
            } catch (NumberFormatException e) {
                System.out.println(e);
                String[] errorMessage = e.toString().split(" ");
                String wrongChar = errorMessage[errorMessage.length - 1];
                showError(wrongChar + " is not allowed in input");
            }
        }
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
