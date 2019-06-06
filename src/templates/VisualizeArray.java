package templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Used to visualize an array.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class VisualizeArray implements Showable{

    private VBox visualBox;
    private HBox positiveSide, negativeSide;
    private Rectangle[] visualArray;
    private int minValue, maxValue, spread;
    private static final int rectangleWidth = 35;
    private boolean isAllNegative;
    private List<Rectangle> inFocus, disabled;
    private Rectangle found, pivot;

    /**
     * Constructor.
     * @param visualBox the VBox from which the array should be shown.
     */

    public VisualizeArray(VBox visualBox) {
        this.visualBox = visualBox;
        inFocus = new ArrayList<>();
        disabled = new ArrayList<>();
        positiveSide = new HBox(5);
        negativeSide = new HBox(5);
        positiveSide.setAlignment(Pos.BOTTOM_CENTER);
        negativeSide.setAlignment(Pos.TOP_CENTER);
    }


    /**
     * Initializes the array for this VisualizeArray.
     * @param array
     */

    public void initArray(int[] array) {
        if(array.length != 0) {
            visualArray = new Rectangle[array.length];
            maxValue = Arrays.stream(array).max().getAsInt();
            minValue = Arrays.stream(array).min().getAsInt();
            isAllNegative = Arrays.stream(array).allMatch(num -> num <= 0);
            getSpread();
            initVisuals(array);
        }
    }

    public void initArray(int[] array, int maxValue) {
        if(array.length != 0) {
            visualArray = new Rectangle[array.length];
            this.maxValue = maxValue;
            minValue = Arrays.stream(array).min().getAsInt();
            isAllNegative = Arrays.stream(array).allMatch(num -> num <= 0);
            getSpread();
            initVisuals(array);
        }
    }

    /**
     * Gets the spread of the array.
     */

    private void getSpread() {
        if(maxValue >= 0 && minValue >= 0) {
            spread = maxValue;
        } else if(maxValue >= 0 && minValue < 0) {
            spread = maxValue + Math.abs(minValue);
        } else {
            spread = Math.abs(minValue);
        }
        if(spread == 0) {
            spread = 1;
        }
    }

    /**
     * Creates all the visuals to show.
     * @param array the array to create the visuals from.
     */

    private void initVisuals(int[] array) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] >= 0) {
                addPositive(array[i], i);
            } else {
                addNegative(array[i], i);
            }
        }
    }

    /**
     * Adds a rectangle to the positive side HBox with the given number and index.
     * @param number the number from the array.
     * @param index the index in the array.
     */

    private void addPositive(int number, int index) {
        //Rectangle that is actually shown.
        Rectangle rec = new Rectangle(rectangleWidth, 1);
        double multiplier = (double) number / (double) spread;
        rec.heightProperty().bind(visualBox.heightProperty().subtract(30).multiply(multiplier));
        rec.getStyleClass().add("rectangle-standard");
        visualArray[index] = rec;

        //Info about the given index and number
        VBox indexInfo = new VBox(5);
        indexInfo.setAlignment(Pos.BOTTOM_CENTER);
        Label num = new Label("" + number);
        Label ind = new Label("(" + index + ")");
        indexInfo.getChildren().addAll(num, ind);

        //Add it to the positive HBox and add a fake rectangle to the negative side.
        StackPane layout = new StackPane(rec, indexInfo);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        positiveSide.getChildren().add(layout);
        Rectangle fakeRec = new Rectangle(rectangleWidth, 0);
        negativeSide.getChildren().add(fakeRec);
    }

     /**
     * Adds a rectangle to the negative side HBox with the given number and index.
     * @param number the number from the array.
     * @param index the index in the array.
     */

    private void addNegative(int number, int index) {
        //Rectangle that is actually shown.
        Rectangle rec = new Rectangle(rectangleWidth, 1);
        double multiplier = (double) Math.abs(number) / (double) spread;
        if(isAllNegative)
            rec.heightProperty().bind(visualBox.heightProperty().subtract(60).multiply(multiplier));
        else
            rec.heightProperty().bind(visualBox.heightProperty().subtract(30).multiply(multiplier));
        rec.getStyleClass().add("rectangle-standard");
        visualArray[index] = rec;

        //Info about the given index and number + fake rectangle
        Rectangle fakeRec = new Rectangle(rectangleWidth, 0);
        VBox indexInfo = new VBox(5);
        indexInfo.setAlignment(Pos.BOTTOM_CENTER);
        Label num = new Label("" + number);
        Label ind = new Label("(" + index + ")");
        indexInfo.getChildren().addAll(num, ind);

        //Stackpane for positive side
        StackPane layout = new StackPane(fakeRec, indexInfo);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        positiveSide.getChildren().add(layout);
        negativeSide.getChildren().add(rec);
    }

    /**
     * Shows the array in the visualBox given in the constructor.
     */

    public void show() {
        Platform.runLater(() -> {
            visualBox.getChildren().clear();
            visualBox.getChildren().addAll(positiveSide, negativeSide);
        });
    }

    /**
     * Sets the given index in the visualarray to focused and adds that to infocus.
     * @param index
     */

    public void setInFocus(int index) {
        visualArray[index].getStyleClass().add("rectangle-inFocus");
        inFocus.add(visualArray[index]);
    }

    /**
     * Removes the infocus effect from the given index.
     * @param index
     */

    public void removeInfocus(int index) {
        visualArray[index].getStyleClass().remove(visualArray[index].getStyleClass().size() - 1);
        inFocus.remove(visualArray[index]);
    }

    /**
     * Removes all infocus.
     */

    public void removeAllInFocus() {
        inFocus.forEach(rec -> rec.getStyleClass().remove(rec.getStyleClass().size() - 1));
        inFocus.clear();
    }

    /**
     * Sets the given index in the visualArray to found.
     * @param index
     */

    public void setDone(int index) {
        visualArray[index].getStyleClass().add("rectangle-done");
        found = visualArray[index];
    }

    /**
     * Removes the found effect from the visualArray.
     */

    public void removeFound() {
        found.getStyleClass().remove(found.getStyleClass().size() - 1);
    }

    public void setPivot(int index) {
        visualArray[index].getStyleClass().add("rectangle-pivot");
        pivot = visualArray[index];
    }

    public void removePivot() {
        pivot.getStyleClass().remove(pivot.getStyleClass().size() - 1);
    }

    public void setDisabled(int left, int right) {
        if(left > 0) {
            for(int i = 0; i < left; i++) {
                visualArray[i].getStyleClass().add("rectangle-disabled");
                disabled.add(visualArray[i]);
            }
        }
        if(right < visualArray.length - 1) {
            for(int i = right + 1; i < visualArray.length; i++) {
                visualArray[i].getStyleClass().add("rectangle-disabled");
                disabled.add(visualArray[i]);
            }
        }
    }

    public void removeAllDisabled() {
        disabled.forEach(rec -> rec.getStyleClass().remove(rec.getStyleClass().size() - 1));
        disabled.clear();
    }
}
