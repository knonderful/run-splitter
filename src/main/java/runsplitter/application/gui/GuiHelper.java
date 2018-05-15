package runsplitter.application.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import runsplitter.common.Thrower;

/**
 *
 */
public class GuiHelper {

    public static void handleException(Thrower thrower, String message) {
        try {
            thrower.call();
        } catch (Throwable e) {
            popupError(message, e);
        }
    }
    
    public static void popupError(String message, Throwable error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, String.format("%s: %s", message, error.getMessage()), ButtonType.OK);
        alert.showAndWait();
    }
}
