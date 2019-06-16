package templates;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javax.swing.text.Position;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class VisualizeTree implements Showable {

    private static final double CIRCLE_RADIUS = 25.0;

    private TreeNode root;
    private VBox visualBox;
    private int height, numOfNodes;
    private GridPane grid;
    private StackPane lineBox;

    public VisualizeTree(TreeNode root, VBox visualBox) {
        this.root = root;
        this.visualBox = visualBox;
        height = getHeightAndNumberOfNodes(root);
        grid = new GridPane();
        lineBox = new StackPane();
        lineBox.prefHeightProperty().bind(grid.heightProperty());
        lineBox.prefWidthProperty().bind(grid.widthProperty());
        lineBox.setAlignment(Pos.TOP_CENTER);
        grid.setAlignment(Pos.CENTER);
        if(root != null) {
            initVisuals();
        }
        lineBox.getChildren().add(grid);
    }

    private int getHeightAndNumberOfNodes(TreeNode node) {
        if(node != null) {
            numOfNodes++;
            return 1 + Math.max(getHeightAndNumberOfNodes(node.right), getHeightAndNumberOfNodes(node.left));
        } else {
            return 0;
        }
    }

    private void initVisuals() {
        int position = (int) (Math.pow(2, height - 1) - 1);
        int layer = 0;
        double width = 0.5;
        root.x.bind(grid.widthProperty().multiply(width));
        root.y.set(CIRCLE_RADIUS);
        addToGrid(root, position, layer);
        layer++;
        initVisualsRec(root.left, position - getPositionShift(height, layer), layer, -1, CIRCLE_RADIUS);
        initVisualsRec(root.right, position + getPositionShift(height, layer), layer, 1, -CIRCLE_RADIUS);
    }

    private void initVisualsRec(TreeNode currentNode, int position, int layer, double multiplier, double subtraction) {
        if(currentNode != null) {
            //NO TOUCHERINO!
            currentNode.x.bind(currentNode.parent.x.add(grid.widthProperty().divide(Math.pow(2, layer + 1)).multiply(multiplier).subtract(subtraction / (layer + 1))));
            currentNode.y.bind(currentNode.parent.y.add(CIRCLE_RADIUS * 2));

            addToGrid(currentNode, position, layer);
            addLine(currentNode);
            layer++;
            initVisualsRec(currentNode.left, position - getPositionShift(height, layer), layer, -1, CIRCLE_RADIUS);
            initVisualsRec(currentNode.right, position + getPositionShift(height, layer), layer, 1, -CIRCLE_RADIUS);
        } else if(layer != height) {
            addGhostCircle(position, layer);
        }
    }

    private static int getPositionShift(int height, int layer) {
        return (int) Math.pow(2, (height - layer - 1));
    }

    private void addToGrid(TreeNode node, int position, int layer) {
        Label label = new Label("" + node.number);
        Circle circle = new Circle(CIRCLE_RADIUS);
        circle.getStyleClass().add("circle-standard");
        StackPane stack = new StackPane(circle, label);
        node.visual = stack;
        grid.add(stack, position, layer);
    }

    private void addGhostCircle(int position, int layer) {
        Circle circle = new Circle(CIRCLE_RADIUS);
        circle.getStyleClass().add("ghost-cirle");
        grid.add(circle, position, layer);

    }

    private void addLine(TreeNode node) {
        TreeNode parent = node.parent;
        if(parent != null) {
            Line line = new Line();
            line.startXProperty().bind(parent.x);
            line.startYProperty().bind(parent.y);
            line.endXProperty().bind(node.x);
            line.endYProperty().bind(node.y);
            line.setStrokeWidth(7);
            line.setStrokeLineCap(StrokeLineCap.BUTT);
            line.setManaged(false);
            line.getStyleClass().add("binary-tree-connector");
            lineBox.getChildren().add(line);
        }
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            visualBox.getChildren().clear();
            visualBox.getChildren().add(lineBox);
        });
    }
}
