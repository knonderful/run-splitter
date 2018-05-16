package runsplitter.application.gui;

import afester.javafx.svg.SvgLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import runsplitter.application.GuiTheme;
import runsplitter.common.Thrower;

/**
 * Helper class for GUI-related work.
 */
public class GuiHelper {

    private final Collection<Supplier<ObservableList<String>>> stylesheetListSuppliers = new HashSet<>(4);
    private List<String> defaultStylesheets;
    private GuiTheme currentTheme;

    GuiHelper(GuiTheme theme) {
        this.currentTheme = theme;
    }

    /**
     * Initializes a {@link Scene}.
     * <p>
     * This applies common characteristics for all {@link Scene}s in the application, like applying the theme.
     *
     * @param scene The {@link Scene}.
     * @param stage The {@link Stage} for the {@link Scene}.
     */
    public void initializeScene(Scene scene, Stage stage) {
        // Only grab the default stylesheets once, when we create the first scene.
        if (defaultStylesheets == null) {
            defaultStylesheets = scene.getStylesheets().stream().collect(Collectors.toList());
        }

        stage.setScene(scene);
        stage.setOnHiding(evt -> unregisterStylesheetSupplier(scene::getStylesheets));
        stage.setOnShowing(evt -> registerStylesheetSupplier(scene::getStylesheets));
    }

    /**
     * Creates a dialog with an OK and Cancel button.
     *
     *
     * @param title      The title of the dialog.
     * @param form       The form inside the dialog (excluding the control buttons).
     * @param validation The {@link ValidationSupport} for the form.
     * @return The {@link Dialog}.
     */
    public Dialog<ButtonType> createSaveCancelDialog(String title, Node form, ValidationSupport validation) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        DialogPane dialogPane = new DialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        registerStylesheetSupplier(dialogPane::getStylesheets);
        dialogPane.setContent(form);
        dialog.setDialogPane(dialogPane);

        if (validation != null) {
            // Prevent the form from being closed if the OK button is pressed while there are validation errors
            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okBtn.addEventFilter(ActionEvent.ACTION, event -> {
                if (validation.isInvalid()) {
                    event.consume();
                }
            });
        }

        return dialog;
    }

    /**
     * Sets the current {@link GuiTheme}.
     *
     * @param theme The {@link GuiTheme}.
     */
    public void setCurrentTheme(GuiTheme theme) {
        if (this.currentTheme == theme) {
            return;
        }

        this.currentTheme = theme;
        stylesheetListSuppliers.forEach(this::applyThemeTo);
    }

    private void registerStylesheetSupplier(Supplier<ObservableList<String>> parent) {
        applyThemeTo(parent);
        stylesheetListSuppliers.add(parent);
    }

    private void unregisterStylesheetSupplier(Supplier<ObservableList<String>> parent) {
        stylesheetListSuppliers.remove(parent);
    }

    private void applyThemeTo(Supplier<ObservableList<String>> scene) throws IllegalStateException {
        ObservableList<String> sheets = scene.get();
        sheets.clear();
        switch (currentTheme) {
            case COMPACT:
                sheets.add("/runsplitter/application/theme.css");
                break;
            case DEFAULT:
                // This should not happen, unless there is a coding error.
                if (defaultStylesheets == null) {
                    throw new IllegalStateException("Can not set theme yet; the default stylesheets have not been set.");
                }

                sheets.addAll(defaultStylesheets);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown theme: %s.", currentTheme));
        }
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
