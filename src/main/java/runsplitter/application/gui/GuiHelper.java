package runsplitter.application.gui;

import afester.javafx.svg.SvgLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
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

    public static void handleException(Thrower thrower) {
        try {
            thrower.call();
        } catch (Throwable e) {
            popupError(e);
        }
    }

    public static void popupError(Throwable error) {
        popupError(null, error);
    }

    public static void popupError(String message, Throwable error) {
        String msg;
        if (message == null) {
            msg = error.getMessage();
            if (msg == null) {
                msg = String.format("An unknown error occurred: %s.", error.getClass().getSimpleName());
            }
        } else {
            msg = String.format("%s: %s", message, error.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public <T> Button createAddButton(Supplier<T> itemSupplier, Consumer<T> itemConsumer) {
        return createAddButton(itemSupplier, itemConsumer, null);
    }

    public <T> Button createAddButton(Supplier<T> itemSupplier, Consumer<T> itemConsumer, ReadOnlyObjectProperty<?> parentObjectSelectedItemProperty) {
        Group svgImage = getSvgImage("plus.svg");
        Button button = createIconButton(svgImage, "Add");
        if (parentObjectSelectedItemProperty != null) {
            applyVisiblityUpdates(button, parentObjectSelectedItemProperty);
        }
        button.setOnAction(event -> {
            T item = itemSupplier.get();
            if (item != null) {
                itemConsumer.accept(item);
            }
        });
        return button;
    }

    public <T> Button createRemoveButton(ReadOnlyObjectProperty<T> selectedItemProperty, Function<T, String> itemNameFunction, Consumer<T> itemConsumer) {
        Group svgImage = getSvgImage("minus.svg");
        Button button = createIconButton(svgImage, "Remove");
        applyVisiblityUpdates(button, selectedItemProperty);
        button.setOnAction(event -> {
            T item = selectedItemProperty.get();
            if (item == null) {
                return;
            }

            Dialog<ButtonType> confirmationDialog = new Dialog<>();
            confirmationDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            String name = itemNameFunction.apply(item);
            String contentText;
            String title;
            if (name == null || name.isEmpty()) {
                contentText = "Are you sure that you want to remove the selected item?";
                title = "Remove item";
            } else {
                contentText = String.format("Are you sure that you want to remove '%s'?", name);
                title = String.format("Remove '%s'", name);
            }
            confirmationDialog.setContentText(contentText);
            confirmationDialog.setTitle(title);
            confirmationDialog.showAndWait().ifPresent(btnType -> {
                if (btnType == ButtonType.OK) {
                    itemConsumer.accept(item);
                }
            });
        });
        return button;
    }

    public <T> Button createEditButton(ReadOnlyObjectProperty<T> selectedItemProperty, Consumer<T> itemConsumer) {
        Group svgImage = getSvgImage("pencil.svg");
        Button button = createIconButton(svgImage, "Edit");
        applyVisiblityUpdates(button, selectedItemProperty);
        button.setOnAction(event -> {
            T item = selectedItemProperty.get();
            if (item == null) {
                return;
            }

            itemConsumer.accept(item);
        });
        return button;
    }

    public <T> Button createMoveUpButton(ListView<T> itemListView) {
        return createMoveUpButton(itemListView::getSelectionModel, itemListView::getItems);
    }

    public <T> Button createMoveUpButton(TableView<T> itemListView) {
        return createMoveUpButton(itemListView::getSelectionModel, itemListView::getItems);
    }

    public <T> Button createMoveUpButton(Supplier<MultipleSelectionModel<T>> selectionModelSupplier, Supplier<ObservableList<T>> itemsSupplier) {
        Group svgImage = getSvgImage("arrow-top.svg");
        Button button = createIconButton(svgImage, "Move up");
        MultipleSelectionModel<T> selectionModel = selectionModelSupplier.get();
        ReadOnlyObjectProperty<T> selectedItemProperty = selectionModel.selectedItemProperty();
        applyVisiblityUpdates(button, selectedItemProperty);
        button.setOnAction(event -> {
            T selectedItem = selectedItemProperty.get();
            if (selectedItem == null) {
                return;
            }

            ObservableList<T> items = itemsSupplier.get();
            int index = items.indexOf(selectedItem);
            if (index <= 0) {
                return;
            }
            items.remove(index);
            int targetIndex = index - 1;
            items.add(targetIndex, selectedItem);
            selectionModel.select(targetIndex);
        });
        return button;
    }

    public <T> Button createMoveDownButton(ListView<T> itemListView) {
        return createMoveDownButton(itemListView::getSelectionModel, itemListView::getItems);
    }

    public <T> Button createMoveDownButton(TableView<T> itemListView) {
        return createMoveDownButton(itemListView::getSelectionModel, itemListView::getItems);
    }

    public <T> Button createMoveDownButton(Supplier<MultipleSelectionModel<T>> selectionModelSupplier, Supplier<ObservableList<T>> itemsSupplier) {
        Group svgImage = getSvgImage("arrow-bottom.svg");
        Button button = createIconButton(svgImage, "Move down");
        MultipleSelectionModel<T> selectionModel = selectionModelSupplier.get();
        ReadOnlyObjectProperty<T> selectedItemProperty = selectionModel.selectedItemProperty();
        applyVisiblityUpdates(button, selectedItemProperty);
        button.setOnAction(event -> {
            T selectedItem = selectedItemProperty.get();
            if (selectedItem == null) {
                return;
            }

            ObservableList<T> items = itemsSupplier.get();
            int index = items.indexOf(selectedItem);
            if (index >= items.size() - 1) {
                return;
            }
            items.remove(index);
            int targetIndex = index + 1;
            items.add(targetIndex, selectedItem);
            selectionModel.select(targetIndex);
        });
        return button;
    }

    public static Node createControlsBox(Button... buttons) {
        HBox gameControlsBox = new HBox(buttons);
        gameControlsBox.setAlignment(Pos.TOP_RIGHT);
        gameControlsBox.setPadding(new Insets(1, 0, 1, 0));
        gameControlsBox.setSpacing(2);
        return gameControlsBox;
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

    private Group getSvgImage(String resourceName) {
        try (InputStream svgFile = getResource(resourceName)) {
            SvgLoader loader = new SvgLoader();
            Group svgImage = loader.loadSvg(svgFile);
            if (svgImage == null) {
                throw new IOException(String.format("Resource %s could not be loaded.", resourceName));
            }
            return svgImage;
        } catch (IOException e) {
            // This can only happen in case the application is bugged, so let's wrap it into a run-time exception
            throw new RuntimeException(e);
        }
    }

    private InputStream getResource(String name) {
        return getClass().getResourceAsStream("/runsplitter/application/" + name);
    }
}
