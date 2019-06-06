package templates;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class VisualizeStringList implements Showable{

    private HBox visualBox;
    private HBox[] array;
    private List<VBox> visual;


    public VisualizeStringList(HBox visualBox) {
        this.visualBox = visualBox;
        visual = new ArrayList<>();
    }

    public void addVisuals(VisualizeStringList vis) {
        visual.addAll(vis.visual);
    }

    public void initArray(String[] strings) {
        array = new HBox[strings.length];
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        for(int i = 0; i < strings.length; i++) {
            HBox node = getListing(strings[i]);
            array[i] = node;
            box.getChildren().add(node);
        }
        visual.add(box);
    }

    private HBox getListing(String string) {
        HBox wrapper = new HBox(0);
        for (int i = 0; i < string.length(); i++) {
            StackPane stack = new StackPane();
            Rectangle rec = new Rectangle();
            rec.getStyleClass().add("rectangle-standard");
            rec.heightProperty().bind(stack.heightProperty());
            rec.widthProperty().bind(stack.widthProperty());
            Label label = new Label("" + string.charAt(i));
            label.setFont(new Font(18));
            stack.getChildren().addAll(rec, label);
            wrapper.getChildren().add(stack);
        }
        return wrapper;
    }

    public void setCurrentDigitInFocus(int digit) {
        //Confusing mess, but it unwraps what the getListing wraps, get a number out using digit to decide which one
        //And colors that whole row of digits orange.
        for(HBox box : array) {
            StackPane stack = (StackPane) box.getChildren().get(box.getChildren().size() - digit - 1);
            stack.getChildren().get(0).getStyleClass().add("rectangle-inFocus");
        }
    }

    public void setCurrentDigitAndIndexInFocus(int digit, int index) {
        //Confusing mess, but it unwraps what the getListing wraps, gets a number out using index for the array and digit to decide
        //What digit to choose. It then colors that number red.
        StackPane stack = (StackPane) array[index].getChildren().get(array[index].getChildren().size() - digit - 1);
        stack.getChildren().get(0).getStyleClass().add("rectangle-red");
    }

    public void setCurrentDigitAndIndexDone(int digit, int index) {
        StackPane stack = (StackPane) array[index].getChildren().get(array[index].getChildren().size() - digit - 1);
        stack.getChildren().get(0).getStyleClass().add("rectangle-done");
    }

    private void drawArrow() {
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[] {
            0.0, 0.0,
            0.0, 40.0,
            20.5, 20.5
        });
        triangle.getStyleClass().add("triangle");
        visualBox.getChildren().add(triangle);
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            visualBox.getChildren().clear();
            for(int i = 0; i < visual.size(); i++) {
                visualBox.getChildren().add(visual.get(i));
                if(i != visual.size() - 1)
                    drawArrow();
            }
        });
    }
}
