package templates;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class TreeNode {

    public TreeNode left, right, parent;
    public Integer number;
    public StackPane visual;
    public DoubleProperty x, y;

    public TreeNode() {
        left = null;
        right = null;
        parent = null;
        number = null;
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();
    }

    public TreeNode(int number) {
        this();
        this.number = number;
    }

    public void setInFocus() {
        visual.getChildren().get(0).getStyleClass().add("circle-inFocus");
    }

    public void setDone() {
        visual.getChildren().get(0).getStyleClass().add("circle-done");
    }
}
