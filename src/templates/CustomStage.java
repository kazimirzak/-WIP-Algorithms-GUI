package templates;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Custom stage that is undecorated.
 *
 * @author Kenny Brink - kebri18@student.sdu.dk
 */
public class CustomStage extends Stage {

    public static final double screenSizeHeight = Screen.getPrimary().getVisualBounds().getHeight();
    public static final double screenSizeWidth = Screen.getPrimary().getVisualBounds().getWidth();

    public static BooleanProperty isFullscreen = StageInteractor.isFullscreen;
    public static BooleanProperty isDarkmode = new SimpleBooleanProperty(true);

    private boolean allowFullscreen;

    public String darkmode = getClass().getResource("darkmode.css").toExternalForm();
    public String lightmode = getClass().getResource("lightmode.css").toExternalForm();

    private final StageInteractor stageInteractor;

    /**
     * Constructor sets the minimum width and height of this stage to the given inputs.
     *
     * @param minWidth
     * @param minHeight
     */

    public CustomStage(double minWidth, double minHeight, boolean allowFullscreen) {
        super(StageStyle.UNDECORATED);
        this.allowFullscreen = allowFullscreen;
        setMinWidth(minWidth);
        setMinHeight(minHeight);
        setHeight(screenSizeHeight * 0.75);
        setWidth(screenSizeWidth * 0.65);
        stageInteractor = new StageInteractor(this);
    }

    /**
     * Sets the screen and sets the size of the stage to 75% of screen height and 65% of screen width.
     *
     * @param scene
     */

    public void setMyScene(Scene scene) {
        setScene(scene);
        makeStageInteractable();
        initColorMode(scene);
    }

    public void setMyScene(Parent root) {
        Scene currentScene = getScene();
        currentScene.setRoot(root);
        makeStageInteractable();
        initColorMode(getScene());
    }

    /**
     * Initializes the scenes color mode.
     *
     * @param scene
     */

    private void initColorMode(Scene scene) {
        if (isDarkmode.getValue()) {
            scene.getStylesheets().add(darkmode);
        } else {
            scene.getStylesheets().add(lightmode);
        }

        isDarkmode.addListener((property, oldValue, newValue) -> {
            if (newValue) {
                scene.getStylesheets().remove(0);
                scene.getStylesheets().add(darkmode);
            } else {
                scene.getStylesheets().remove(0);
                scene.getStylesheets().add(lightmode);
            }
        });
    }

    /**
     * Makes the stage interactable using StageInteractor.
     */

    private void makeStageInteractable() {
            if(allowFullscreen)
                stageInteractor.makeFullScreenable(30, 0, 0, 0);
            stageInteractor.makeResizeable(5, 5, 5, 5);
            stageInteractor.makeDraggable(30, 0, 0, 0);
    }

}
