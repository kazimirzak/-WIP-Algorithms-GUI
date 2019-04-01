package templates;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This class was made so an undecorated stage could be resized draggable and fullscreenable.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class StageInteractor {

    private final Stage stage;
    private final Scene scene;

    private boolean isDraggable = false;
    private boolean isDragging = false;
    private final boolean allowDragging = true;
    private double dragMarginTop = 0;
    private double dragMarginRight = 0;
    private double dragMarginBottom = 0;
    private double dragMarginLeft = 0;

    private boolean isFullscreenable = false;
    public static BooleanProperty isFullscreen = new SimpleBooleanProperty(false);
    public static BooleanProperty isHalfScreen = new SimpleBooleanProperty(false);
    private final boolean allowFullscreen = true;
    private double fullscreenMarginTop = 0;
    private double fullscreenMarginRight = 0;
    private double fullscreenMarginBottom = 0;
    private double fullscreenMarginLeft = 0;
    private double stageWidthBeforeFullscreen = 0;
    private double stageHeightBeforeFullscreen = 0;
    private double stageXBeforeFullscreen = 0;
    private double stageYBeforeFullscreen = 0;
    private double stageWidthBeforeHalfScreen = 0;
    private double stageHeightBeforeHalfScreen = 0;
    private double stageXBeforeHalfScreen = 0;
    private double stageYBeforeHalfScreen = 0;

    private boolean isResizeable = false;
    private boolean isResizing = false;
    private boolean allowResizing = true;
    private ResizeDirection resizeDirection = null;
    private List<Cursor> resizeCursors;
    private double resizeMarginTop = 0;
    private double resizeMarginRight = 0;
    private double resizeMarginBottom = 0;
    private double resizeMarginLeft = 0;

    private double dragStartOffSetX = 0;
    private double dragStartOffSetY = 0;

    private double resizeStartFromSceneX = 0;
    private double resizeStartFromSceneY = 0;
    private double resizeStartFromScreenX = 0;
    private double resizeStartFromScreenY = 0;
    private double resizeStartStageWidth = 0;
    private double resizeStartStageHeight = 0;

    /**
     * Constructor.
     * @param stage
     */

    public StageInteractor(Stage stage) {
        this.stage = stage;
        this.scene = stage.getScene();
        initializeCursorArray();
    }

    /**
     * Initializes an array with all the different resize cursors.
     */

    private void initializeCursorArray() {
        resizeCursors = new ArrayList<>(8);
        resizeCursors.add(Cursor.NW_RESIZE);
        resizeCursors.add(Cursor.NE_RESIZE);
        resizeCursors.add(Cursor.SW_RESIZE);
        resizeCursors.add(Cursor.SE_RESIZE);
        resizeCursors.add(Cursor.N_RESIZE);
        resizeCursors.add(Cursor.S_RESIZE);
        resizeCursors.add(Cursor.W_RESIZE);
        resizeCursors.add(Cursor.E_RESIZE);
    }

    /**
     * Makes the layout draggable with a standard top margin of 50 and rest to 0.
     */

    public void makeDraggable() {
        double marginTop = 50.0;
        double marginRight = 0.0;
        double marginBottom = 0.0;
        double marginLeft = 0.0;
        makeDraggable(marginTop, marginRight, marginBottom, marginLeft);
    }

    /**
     * Makes the screen draggable at the given margins on the screen.
     * @param marginTop how big a margin should be draggable at the top.
     * @param marginRight how big a margin should be draggable at the right.
     * @param marginBottom how big a margin should be draggable at the bottom.
     * @param marginLeft how big a margin should be draggable at the left.
     */

    public void makeDraggable(double marginTop, double marginRight, double marginBottom, double marginLeft) {
        dragMarginTop = marginTop;
        dragMarginRight = marginRight;
        dragMarginBottom = marginBottom;
        dragMarginLeft = marginLeft;
        if(!isDraggable) {
            isDraggable = true;

            dragStartOffSetX = 0.0;
            dragStartOffSetY = 0.0;

            //Handles wether the mouse is pressed and saves dragStartOffSetX and Y used when the stage is actually dragged.

            scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                if(e.getButton() == MouseButton.MIDDLE) {
                    stage.centerOnScreen();
                }else if(e.getButton() == MouseButton.PRIMARY) {
                    if(!isFullscreen.getValue()) {
                        dragStartOffSetX = stage.getX() - e.getScreenX();
                        dragStartOffSetY = stage.getY() - e.getScreenY();
                    } else {
                        // This is make the window snap to where the mouse it and keep the position of the scene proportinal to where the mouse is and was on the 2 different screen sizes.
                        dragStartOffSetX = ((stage.getWidth() - e.getSceneX()) / stageWidthBeforeFullscreen) - (e.getSceneX() / (stage.getWidth() / stageWidthBeforeFullscreen));
                        dragStartOffSetY = ((stage.getHeight() - e.getSceneY()) / stageWidthBeforeFullscreen) - (e.getSceneY()/ (stage.getHeight()/ stageWidthBeforeFullscreen));
                    }
                }
            });

            //Handles the dragging of the stage. If screen is fullscreen it is disabled.

            scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
                if(e.getButton() == MouseButton.PRIMARY) {
                    boolean isWithinBounds = detectDraggingBounds(e);
                    if(isDraggable && allowDragging && isWithinBounds) {
                        isDragging = true;
                        if(isFullscreen.getValue()) {
                            isFullscreen.setValue(false);
                        }
                        if(isHalfScreen.getValue()) {
                            isHalfScreen.setValue(false);
                        }
                    }

                    if(isDragging) {
                        stage.setX(e.getScreenX() + dragStartOffSetX);
                        stage.setY(e.getScreenY() + dragStartOffSetY);
                    }

                }
            });

            //Checks when dragging stops.

            scene.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                if(isDragging) {
                    isDragging = false;

                    // Sets the window to fullscreen if dragged to the top.
                    if(e.getScreenY() <= 1) {
                        isFullscreen.setValue(true);
                    }

                    // Sets the window to fill half the screen if dragged to the side.
                    if(e.getScreenX() <= 1) {
                        isHalfScreen.setValue(true);
                        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
                        stage.setX(Screen.getPrimary().getVisualBounds().getMinX());
                        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
                    }

                    // Sets the window to fill half the screen if dragged to the side.
                    if(e.getScreenX() >= Screen.getPrimary().getVisualBounds().getMaxX() - 1) {
                        isHalfScreen.setValue(true);
                        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
                        stage.setX(stage.getWidth());
                        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
                    }
                }
            });
        }
    }

    /**
     * Detects whether the given mouse event is out of bounds.
     * @param e the mouse event
     * @return Returns true if the mouse event is inside dragging bounds.
     */

    private boolean detectDraggingBounds(MouseEvent e) {
        return e.getSceneY() <= dragMarginTop
                || scene.getHeight() - e.getSceneY() <= dragMarginBottom
                || e.getSceneX() <= dragMarginLeft
                || scene.getWidth() - e.getSceneX() <= dragMarginRight;
    }

    /**
     * Makes the stage fullscreenable with a top margin of 50 and all others to 0.
     */

    public void makeFullScreenable() {
        double marginTop = 50;
        double marginRight = 0;
        double marginBottom = 0;
        double marginLeft = 0;
        makeFullScreenable(marginTop, marginRight, marginBottom, marginLeft);
    }

    /**
     * Makes the screen fullscreenable by double clicking within the margin.
     * @param marginTop how big a margin should be double clickable at the top.
     * @param marginRight how big a margin should be double clickable at the right.
     * @param marginBottom how big a margin should be double clickable at the bottom.
     * @param marginLeft how big a margin should be double clickable at the left.
     */

    public void makeFullScreenable(double marginTop, double marginRight, double marginBottom, double marginLeft) {
        fullscreenMarginTop = marginTop;
        fullscreenMarginRight = marginRight;
        fullscreenMarginBottom = marginBottom;
        fullscreenMarginLeft = marginLeft;

        if(!isFullscreenable) {
            isFullscreenable = true;

            //Handles actions when fullscreen mode is changed.

            isFullscreen.addListener((property, oldValue, newValue) -> {
                if(!newValue) {
                    allowResizing = true;

                    //if else to make sure that if we start dragging when fullscreen we set the stage to the mouse.
                    if(!isDragging) {
                        stage.setX(stageXBeforeFullscreen);
                        stage.setY(stageYBeforeFullscreen);
                    } else {
                        stage.setX(dragStartOffSetX);
                        stage.setY(dragStartOffSetY);
                    }
                    stage.setWidth(stageWidthBeforeFullscreen);
                    stage.setHeight(stageHeightBeforeFullscreen);
                    stage.setMaximized(false);
                } else {
                    allowResizing = false;
                    stageWidthBeforeFullscreen = stage.getWidth();
                    stageHeightBeforeFullscreen = stage.getHeight();
                    stageXBeforeFullscreen = stage.getX();
                    stageYBeforeFullscreen = stage.getY();
                    stage.setX(Screen.getPrimary().getBounds().getMinX());
                    stage.setY(Screen.getPrimary().getBounds().getMinY());
                    stage.setHeight(Screen.getPrimary().getBounds().getHeight());
                    stage.setWidth(Screen.getPrimary().getBounds().getWidth());
                    stage.setMaximized(true);
                }
            });

            //Handles actions when halfscreen mode is changed.

            isHalfScreen.addListener((property, oldValue, newValue) -> {
                if(!newValue) {
                    stage.setX(stageXBeforeHalfScreen);
                    stage.setY(stageYBeforeHalfScreen);
                    stage.setWidth(stageWidthBeforeHalfScreen);
                    stage.setHeight(stageHeightBeforeHalfScreen);
                } else {
                    stageWidthBeforeHalfScreen = stage.getWidth();
                    stageHeightBeforeHalfScreen = stage.getHeight();
                    stageXBeforeHalfScreen = stage.getX();
                    stageYBeforeHalfScreen = stage.getY();
                }
            });

            //If topbar is double clicked it goes into fullscreen.

            scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                boolean isDoubleClick = e.getButton() == MouseButton.PRIMARY && e.getClickCount() >= 2;
                if(isFullscreenable && allowFullscreen && isDoubleClick && detectFullscreenBounds(e)) {
                    if(!isFullscreen.getValue()) {
                        isFullscreen.setValue(true);
                    } else {
                        isFullscreen.setValue(false);
                    }
                }
            });
        }
    }

    /**
     * Detects wether the given mouse event is within fullscreen bounds.
     * @param e the mouse event to check
     * @return true if the mouse event is inside bounds.
     */

    private boolean detectFullscreenBounds(MouseEvent e) {
        boolean isWithinBounds = e.getSceneY() <= fullscreenMarginTop
                || scene.getHeight() - e.getSceneY() <= fullscreenMarginBottom
                || e.getSceneX() <= fullscreenMarginLeft
                || scene.getWidth() - e.getSceneX() <= fullscreenMarginRight;
        ResizeDirection localResizeDirection = detectResizeDirection(e);
        return isWithinBounds && (localResizeDirection == null);
    }

    /**
     * Makes the stage resizeable with a standard margin of 10.
     */

    public void makeResizeable() {
        double marginTop = 10;
        double marginRight = 10;
        double marginBottom = 10;
        double marginLeft = 10;
        makeResizeable(marginTop, marginRight, marginBottom, marginLeft);
    }

    /**
     * Makes the stage resizeable within the given margins.
     * @param marginTop how big of a margin should be resizeable at the top of this stage.
     * @param marginRight how big of a margin should be resizeable at the top of this stage.
     * @param marginBottom how big of a margin should be resizeable at the top of this stage.
     * @param marginLeft how big of a margin should be resizeable at the top of this stage.
     */

    public void makeResizeable(double marginTop, double marginRight, double marginBottom, double marginLeft) {
        resizeMarginTop = marginTop;
        resizeMarginRight = marginRight;
        resizeMarginBottom = marginBottom;
        resizeMarginLeft = marginLeft;
        if(!isResizeable) {
            isResizeable = true;

            //Constantly checks if the mouse is within resize regions.

            scene.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
                if(isResizeable && allowResizing && !isResizing) {
                    ResizeDirection cursorLocation = detectResizeDirection(e);
                    if(cursorLocation != null) {
                        scene.setCursor(getCursor(cursorLocation));
                    } else if (resizeCursors.contains(scene.getCursor())) {
                        scene.setCursor(Cursor.DEFAULT);
                    }
                }
            });

            //checks if mouse is clicked twice, if it is then it get the direction of the mouse and puts the border of the window to the max size of the screen.

            scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                if(isResizeable && allowResizing && !isResizing) {
                    resizeDirection = detectResizeDirection(e);

                    if(resizeDirection != null) {
                        if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() >= 2) {
                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

                            if(resizeDirection == ResizeDirection.NORTH || resizeDirection == ResizeDirection.NORTH_WEST || resizeDirection == ResizeDirection.NORTH_EAST) {
                                stage.setHeight(ensureStageHeightIsWithinLimits(stage.getHeight() + stage.getY() - screenBounds.getMinY()));
                                stage.setY(0);
                            }

                            if (resizeDirection == ResizeDirection.SOUTH || resizeDirection == ResizeDirection.SOUTH_WEST || resizeDirection == ResizeDirection.SOUTH_EAST) {
                                stage.setHeight(ensureStageHeightIsWithinLimits(screenBounds.getHeight() - stage.getY() + screenBounds.getMinY()));
                                if(stage.getHeight() == screenBounds.getHeight()) {
                                    stage.setY(0);
                                }
                            }

                            if (resizeDirection == ResizeDirection.WEST || resizeDirection == ResizeDirection.NORTH_WEST || resizeDirection == ResizeDirection.SOUTH_WEST) {
                                stage.setWidth(ensureStageWidthIsWithinLimits(stage.getWidth() + stage.getX()));
                                stage.setX(0);
                            }

                            if (resizeDirection == ResizeDirection.EAST || resizeDirection == ResizeDirection.NORTH_EAST || resizeDirection == ResizeDirection.SOUTH_EAST) {
                                stage.setWidth(ensureStageWidthIsWithinLimits(screenBounds.getWidth() - stage.getX()));

                                if(stage.getWidth() == screenBounds.getWidth()) {
                                    stage.setX(0);
                                }
                            }
                        } else {
                            isResizing = true;
                            isDraggable = false;
                            isFullscreenable = false;
                            resizeStartFromScreenX = e.getScreenX();
                            resizeStartFromScreenY = e.getScreenY();
                            resizeStartFromSceneX = e.getSceneX();
                            resizeStartFromSceneY = e.getSceneY();
                            resizeStartStageWidth = stage.getWidth();
                            resizeStartStageHeight = stage.getHeight();
                        }
                    }
                }
            });

            // Handles the actual dragging for resizing.

            scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
                if(isResizing) {
                    if(resizeDirection == ResizeDirection.NORTH || resizeDirection == ResizeDirection.NORTH_WEST || resizeDirection == ResizeDirection.NORTH_EAST) {
                        double newHeight = ensureStageHeightIsWithinLimits(resizeStartStageHeight + (resizeStartFromScreenY - e.getScreenY()));
                        double newY;
                        if(newHeight <= stage.getMinHeight())
                            newY = stage.getY();
                        else
                            newY = e.getScreenY() - resizeStartFromSceneY;
                        stage.setHeight(newHeight);
                        stage.setY(newY);
                    }

                    if(resizeDirection == ResizeDirection.SOUTH || resizeDirection == ResizeDirection.SOUTH_WEST || resizeDirection == ResizeDirection.SOUTH_EAST) {
                        double newHeight = ensureStageHeightIsWithinLimits(resizeStartStageHeight + (e.getScreenY() - resizeStartFromScreenY));
                        stage.setHeight(newHeight);
                    }

                    if(resizeDirection == ResizeDirection.WEST || resizeDirection == ResizeDirection.NORTH_WEST || resizeDirection == ResizeDirection.SOUTH_WEST) {
                        double newWidth = ensureStageWidthIsWithinLimits(resizeStartStageWidth + (resizeStartFromScreenX - e.getScreenX()));
                        double newX;
                        if(newWidth <= stage.getMinWidth())
                            newX = stage.getX();
                        else
                            newX = e.getScreenX() - resizeStartFromSceneX;
                        stage.setWidth(newWidth);
                        stage.setX(newX);
                    }

                    if(resizeDirection == ResizeDirection.EAST || resizeDirection == ResizeDirection.NORTH_EAST || resizeDirection == ResizeDirection.SOUTH_EAST) {
                        double newWidth = ensureStageWidthIsWithinLimits(resizeStartStageWidth + (e.getScreenX() - resizeStartFromScreenX));
                        stage.setWidth(newWidth);
                    }
                }
            });

            //Checks when resizing is complete

            scene.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                if(isResizing) {
                    isResizing = false;
                    isDraggable = true;
                    isFullscreenable = true;
                }
            });
        }
    }

    /**
     * Returns the cursor layout depending on the given cursor location.
     * @param cursorLocation
     * @return
     */

    private Cursor getCursor(ResizeDirection cursorLocation) {
        Cursor cursor;
        switch(cursorLocation) {
            case NORTH_WEST:
                cursor = Cursor.NW_RESIZE;
                break;
            case NORTH_EAST:
                cursor = Cursor.NE_RESIZE;
                break;
            case SOUTH_WEST:
                cursor = Cursor.SW_RESIZE;
                break;
            case SOUTH_EAST:
                cursor = Cursor.SE_RESIZE;
                break;
            case NORTH:
                cursor = Cursor.N_RESIZE;
                break;
            case SOUTH:
                cursor = Cursor.S_RESIZE;
                break;
            case WEST:
                cursor = Cursor.W_RESIZE;
                break;
            case EAST:
                cursor = Cursor.E_RESIZE;
                break;
            default:
                cursor = Cursor.DEFAULT;
                break;
        }
        return cursor;
    }

    /**
     * Checks where the given mouse event is and returns what direction it is at.
     * @param e
     * @return
     */

    private ResizeDirection detectResizeDirection(MouseEvent e) {
        boolean isNorthResize = e.getSceneY() <= resizeMarginTop;
        boolean isSouthResize = scene.getHeight() - e.getSceneY() <= resizeMarginBottom;
        boolean isWestResize = e.getSceneX() <= resizeMarginLeft;
        boolean isEastResize = scene.getWidth() - e.getSceneX() <= resizeMarginRight;
        boolean isNorthWestResize = isNorthResize && isWestResize;
        boolean isNorthEastResize = isNorthResize && isEastResize;
        boolean isSouthWestResize = isSouthResize && isWestResize;
        boolean isSouthEastResize = isSouthResize && isEastResize;
        if(isNorthWestResize)
            return ResizeDirection.NORTH_WEST;
        else if(isNorthEastResize)
            return ResizeDirection.NORTH_EAST;
        else if(isSouthWestResize)
            return ResizeDirection.SOUTH_WEST;
        else if(isSouthEastResize)
            return ResizeDirection.SOUTH_EAST;
        else if(isNorthResize)
            return ResizeDirection.NORTH;
        else if(isSouthResize)
            return ResizeDirection.SOUTH;
        else if(isWestResize)
            return ResizeDirection.WEST;
        else if(isEastResize)
            return ResizeDirection.EAST;
        else
            return null;
    }

    /**
     * Ensures that the given width is not greater than the screen or smaller than the minWidth of the stage.
     * @param width
     * @return
     */

    private double ensureStageWidthIsWithinLimits(double width) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double result;
        if (width <= stage.getMinWidth())
            result = stage.getMinWidth();
        else if (width >= screenBounds.getWidth())
            result = screenBounds.getWidth();
        else
            result = width;
        return result;
    }

    /**
     * Ensures that the given height is not greater than the screen or smaller than the minHeight of the stage.
     * @param height
     * @return
     */

    private double ensureStageHeightIsWithinLimits(double height) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double result;
        if (height <= stage.getMinHeight())
            result = stage.getMinHeight();
        else if (height >= screenBounds.getHeight())
            result = screenBounds.getHeight();
        else
            result = height;
        return result;
    }

    /**
     * ResizeDirections.
     */

    private enum ResizeDirection {
        NORTH, NORTH_EAST, NORTH_WEST,
        SOUTH, SOUTH_EAST, SOUTH_WEST,
        EAST, WEST;
    }
}

