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
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class VisualizeArray {

    private VBox visualBox;
    private HBox positiveSide, negativeSide;
    private Rectangle[] visualArray;
    private int minValue, maxValue, spread;
    private static final int rectangleWidth = 35;
    private boolean isAllNegative;
    private List<Rectangle> inFocus;
    private Rectangle found;

    public VisualizeArray(VBox visualBox) {
        this.visualBox = visualBox;
        inFocus = new ArrayList<>();
        positiveSide = new HBox(5);
        negativeSide = new HBox(5);
        positiveSide.setAlignment(Pos.BOTTOM_CENTER);
        negativeSide.setAlignment(Pos.TOP_CENTER);
    }

    public void initArray(int[] array) {
        visualArray = new Rectangle[array.length];
        maxValue = Arrays.stream(array).max().getAsInt();
        minValue = Arrays.stream(array).min().getAsInt();
        isAllNegative = Arrays.stream(array).allMatch(num -> num < 0);
        getSpread();
        initVisuals(array);
    }

    private void getSpread() {
        if(maxValue > 0 && minValue > 0) {
            spread = maxValue;
        } else if(maxValue > 0 && minValue < 0) {
            spread = maxValue + Math.abs(minValue);
        } else
            spread = Math.abs(minValue);
    }

    private void initVisuals(int[] array) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] >= 0) {
                addPositive(array[i], i);
            } else {
                addNegative(array[i], i);
            }
        }
    }

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

    public void show() {
        Platform.runLater(() -> {
            visualBox.getChildren().clear();
            visualBox.getChildren().addAll(positiveSide, negativeSide);
        });

    }

    public void setInFocus(int index) {
        visualArray[index].getStyleClass().add("rectangle-inFocus");
        inFocus.add(visualArray[index]);
    }

    public void removeInfocus(int index) {
        visualArray[index].getStyleClass().remove(visualArray[index].getStyleClass().size() - 1);
        inFocus.remove(visualArray[index]);
    }

    public void removeAllInFocus() {
        inFocus.forEach(rec -> rec.getStyleClass().remove(rec.getStyleClass().size() - 1));
        inFocus.clear();
    }

    public void setFound(int index) {
        visualArray[index].getStyleClass().add("rectangle-done");
        found = visualArray[index];
    }

    public void removeFound() {
        found.getStyleClass().remove(found.getStyleClass().size() - 1);
    }
}