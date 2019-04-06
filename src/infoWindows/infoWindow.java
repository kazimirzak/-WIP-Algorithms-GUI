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

    public infoWindow(String info) {
        window = new CustomStage(100, 100, false);
        window.setMyScene(new Scene(new BorderPane()));
        errorEncountered = false;
        setScene(info);
        window.setWidth(CustomStage.screenSizeWidth * 0.4);
        window.setHeight(CustomStage.screenSizeHeight * 0.35);
        showWindow();
    }

    private void setScene(String info) {
        switch(info) {
            case ("Main Menu"):
                window.setMyScene(getMainMenuInfo());
                break;
            default:
                errorEncountered = true;
                CustomAlertBox alertbox = new CustomAlertBox();
                alertbox.showAlertBox("Error retrieving info!");
                break;
        }
    }

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

    private void showWindow() {
        if(!errorEncountered)
            window.show();
    }
}
