package runsplitter.application.gui;

import afester.javafx.svg.SvgLoader;
import java.io.IOException;
import java.io.InputStream;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import runsplitter.common.Thrower;

/**
 *
 */
public class GuiHelper {

    public static void applyTheme(Scene mainScene) {
        mainScene.getStylesheets().add("/runsplitter/application/theme.css");
    }

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

    public Button createAddButton() throws IOException {
        Group svgImage = getSvgImage("plus.svg");
        return createIconButton(svgImage, "Add");
    }

    public Button createRemoveButton(ReadOnlyObjectProperty<?> selectedItemProperty) throws IOException {
        Group svgImage = getSvgImage("minus.svg");
        Button button = createIconButton(svgImage, "Remove");
        applyVisiblityUpdates(button, selectedItemProperty);
        return button;
    }

    public Button createEditButton(ReadOnlyObjectProperty<?> selectedItemProperty) throws IOException {
        Group svgImage = getSvgImage("pencil.svg");
        Button button = createIconButton(svgImage, "Edit");
        applyVisiblityUpdates(button, selectedItemProperty);
        return button;
    }

    public Button createMoveUpButton(ReadOnlyObjectProperty<?> selectedItemProperty) throws IOException {
        Group svgImage = getSvgImage("arrow-top.svg");
        Button button = createIconButton(svgImage, "Move up");
        applyVisiblityUpdates(button, selectedItemProperty);
        return button;
    }

    public Button createMoveDownButton(ReadOnlyObjectProperty<?> selectedItemProperty) throws IOException {
        Group svgImage = getSvgImage("arrow-bottom.svg");
        Button button = createIconButton(svgImage, "Move down");
        applyVisiblityUpdates(button, selectedItemProperty);
        return button;
    }

    private static void applyVisiblityUpdates(Button button, ReadOnlyObjectProperty<?> selectedItemProperty) {
        button.setDisable(selectedItemProperty.getValue() == null);
        selectedItemProperty.addListener((observable, oldValue, newValue) -> button.setDisable(newValue == null));
    }

    private Button createIconButton(Group svgImage, String toolTip) {
        svgImage.setScaleX(2);
        svgImage.setScaleY(2);

        // Wrap in a group for proper button sizing
        Button button = new Button();
        button.setMinWidth(24);
        button.setMinHeight(24);
        button.setGraphic(svgImage);
        button.setTooltip(new Tooltip(toolTip));
        return button;
    }

    private Group getSvgImage(String resourceName) throws IOException {
        try (InputStream svgFile = getResource(resourceName)) {
            SvgLoader loader = new SvgLoader();
            Group svgImage = loader.loadSvg(svgFile);
            if (svgImage == null) {
                throw new IOException(String.format("Resource %s could not be loaded.", resourceName));
            }
            return svgImage;
        }
    }

    private InputStream getResource(String name) {
        return getClass().getResourceAsStream("/runsplitter/application/" + name);
    }
}
