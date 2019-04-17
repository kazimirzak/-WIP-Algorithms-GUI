package templates;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Creates an alertBox to show and error with.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class CustomAlertBox {

    private final Stage window;

    private CustomAlertBoxController controller;

    private Parent root;

    /**
     * Constructor. Creates and undecorated window with modality = application modal.
     */

    public CustomAlertBox() {
        window = new Stage(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Shows the alertBox with the given error message.
     * @param message the message to show.
     */

    public void showAlertBox(String message) {
        root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomAlertBoxFXML.fxml"));
            root = loader.load();
            controller = loader.getController();
            controller.setWindow(window);
            controller.initText(message);
        } catch (IOException ex) {
            // Doesnt make a difference what I do here it crashes no matter.
        }
        Scene scene = new Scene(root);
        if(CustomStage.isDarkmode.getValue())
            scene.getStylesheets().add(getClass().getResource("darkmode.css").toExternalForm());
        else
            scene.getStylesheets().add(getClass().getResource("lightmode.css").toExternalForm());
        window.setScene(scene);

        StageInteractor stageInteractor = new StageInteractor(window);
        stageInteractor.makeDraggable(30, 0, 0, 0);

        window.showAndWait();
    }
}
