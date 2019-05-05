package infoWindows;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import templates.CustomAlertBox;
import templates.CustomStage;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class infoWindow {

    private final CustomStage window;

    private boolean errorEncountered;

    /**
     * Constructor.
     * @param info
     */

    public infoWindow(String info) {
        window = new CustomStage(100, 100, false);
        window.setMyScene(new Scene(new BorderPane()));
        errorEncountered = false;
        setScene(info);
        window.setWidth(CustomStage.screenSizeWidth * 0.45);
        window.setHeight(CustomStage.screenSizeHeight * 0.5);
        showWindow();
    }

    /**
     * Sets the scene of this window using the string given.
     * @param info the string used to identify what info window to show.
     */

    private void setScene(String info) {
        switch(info) {
            case ("Main Menu"):
                window.setMyScene(getMainMenuInfo());
                break;
            case ("Linear Search"):
                window.setMyScene(getLinearSearchInfo());
                break;
            case ("Binary Search"):
                window.setMyScene(getBinarySearchInfo());
                break;
            case ("Insertion Sort"):
                window.setMyScene(getInsertionSortInfo());
                break;
            case ("Quick Sort"):
                window.setMyScene(getQuickSortInfo());
                break;
            case ("Bubble Sort"):
                window.setMyScene(getBubbleSortInfo());
                break;
            case ("Selection Sort"):
                window.setMyScene(getSelectionSortInfo());
                break;
            case ("Merge Sort"):
                window.setMyScene(getMergeSortInfo());
                break;
            default:
                errorEncountered = true;
                CustomAlertBox alertbox = new CustomAlertBox();
                alertbox.showAlertBox("Error retrieving info!");
                break;
        }
    }

    /**
     * Gets the mainMenuInfo window
     * @return the scene to show.
     */

    private Parent getMainMenuInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuInfoFXML.fxml"));
        try {
            root = loader.load();
            MainMenuInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

     /**
     * Gets the linearSearchInfo window
     * @return the scene to show.
     */

    private Parent getLinearSearchInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LinearSearchInfoFXML.fxml"));
        try {
            root = loader.load();
            LinearSearchInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    /**
     * Gets the binarySearchInfo window
     * @return the scene to show.
     */

    private Parent getBinarySearchInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BinarySearchInfoFXML.fxml"));
        try {
            root = loader.load();
            BinarySearchInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    /**
     * Gets the binarySearchInfo window
     * @return the scene to show.
     */

    private Parent getInsertionSortInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InsertionSortInfoFXML.fxml"));
        try {
            root = loader.load();
            InsertionSortInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    private Parent getQuickSortInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QuickSortInfoFXML.fxml"));
        try {
            root = loader.load();
            QuickSortInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    private Parent getBubbleSortInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BubbleSortInfoFXML.fxml"));
        try {
            root = loader.load();
            BubbleSortInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    private Parent getSelectionSortInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectionSortInfoFXML.fxml"));
        try {
            root = loader.load();
            SelectionSortInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    private Parent getMergeSortInfo() {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MergeSortInfoFXML.fxml"));
        try {
            root = loader.load();
            MergeSortInfoController controller = loader.getController();
            controller.setWindow(window);
        } catch (IOException e) {
            System.out.println(e);
            errorEncountered = true;
            CustomAlertBox alertbox = new CustomAlertBox();
            alertbox.showAlertBox("Error retrieving info!");
        }
        return root;
    }

    /**
     * If no errors were found it shows the window.
     */

    private void showWindow() {
        if(!errorEncountered)
            window.show();
    }
}
