package runsplitter.application.gui;

import java.util.List;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import runsplitter.VideoAnalyzer;
import runsplitter.application.Category;
import runsplitter.application.SplitDescriptor;

/**
 *
 */
public class EditCategoryDialog {

    public static Category showAndWait(GuiHelper guiHelper, Category categoryIn, List<VideoAnalyzer> analyzers) {
        final Category category;
        boolean isNew;
        if (categoryIn == null) {
            isNew = true;
            category = new Category();
        } else {
            isNew = false;
            category = categoryIn;
        }

        // Validation
        ValidationSupport validation = new ValidationSupport();
        // Form binding (for transferring values between bean and the form)
        MultiFormBinding multiFormBinding = new MultiFormBinding();

        Node form = createForm(guiHelper, category, multiFormBinding, validation, analyzers);

        // Transfer all bean values to the form
        multiFormBinding.beanToForm();

        String title;
        if (isNew) {
            title = "New category";
        } else {
            title = String.format("Edit %s", category.getName());
        }

        // Show the dialog
        ButtonType btnType = guiHelper.createSaveCancelDialog(title, form, validation)
                .showAndWait()
                .get();

        if (btnType != ButtonType.OK) {
            return null;
        }

        // Transfer all values from form to bean
        multiFormBinding.formToBean();

        return category;
    }

    private static Node createForm(GuiHelper guiHelper, Category category, MultiFormBinding multiFormBinding, ValidationSupport validation, List<VideoAnalyzer> analyzers) {
        // Name
        TextField nameTextField = new TextField();

        // Analyzer
        ComboBox<Optional<VideoAnalyzer>> analyzerCombo = GuiHelper.createAnalyzerCombo(analyzers, "(Inherit)");

        // Splits
        TableColumn<SplitDescriptor, String> splitsNameCol = new TableColumn<>("Name");
        splitsNameCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
        splitsNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        splitsNameCol.setOnEditCommit(event -> event.getRowValue().setName(event.getNewValue()));
        TableColumn<SplitDescriptor, String> splitsDescCol = new TableColumn<>("Description");
        splitsDescCol.setCellFactory(TextFieldTableCell.forTableColumn());
        splitsDescCol.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getDescription()));
        splitsDescCol.setOnEditCommit(event -> event.getRowValue().setDescription(event.getNewValue()));
        TableView<SplitDescriptor> splitsTableView = new TableView<>();
        ObservableList<TableColumn<SplitDescriptor, ?>> splitsColumns = splitsTableView.getColumns();
        splitsColumns.add(splitsNameCol);
        splitsColumns.add(splitsDescCol);
        splitsTableView.setEditable(true);

        ReadOnlyObjectProperty<SplitDescriptor> selectedItemProperty = splitsTableView.getSelectionModel().selectedItemProperty();
        Button splitsAddBtn = guiHelper.createAddButton(
                () -> new SplitDescriptor(String.format("Split #%d", splitsTableView.getItems().size() + 1)),
                desc -> splitsTableView.getItems().add(desc));
        Button splitsRemoveBtn = guiHelper.createRemoveButton(
                selectedItemProperty,
                SplitDescriptor::getName,
                desc -> splitsTableView.getItems().remove(desc));
        Button splitsUpBtn = guiHelper.createMoveUpButton(splitsTableView);
        Button splitsDownBtn = guiHelper.createMoveDownButton(splitsTableView);

        VBox splitsBox = new VBox(
                splitsTableView,
                GuiHelper.createControlsBox(splitsAddBtn, splitsRemoveBtn, splitsUpBtn, splitsDownBtn)
        );

        GridPane grid = GuiHelper.createFormGrid();
        grid.add(new Label("Name"), 0, 0);
        grid.add(nameTextField, 1, 0);
        grid.add(new Label("Analyzer"), 0, 1);
        grid.add(analyzerCombo, 1, 1);
        grid.add(new Label("Splits"), 0, 2);
        grid.add(splitsBox, 1, 2);

        // Form binding
        multiFormBinding.add(FormBindings.create(
                category::getName, category::setName,
                nameTextField::getText, nameTextField::setText
        ));
        multiFormBinding.add(FormBindings.create(
                category::getVideoAnalyzer, category::setVideoAnalyzer,
                () -> analyzerCombo.getValue().orElse(null), analyzer -> analyzerCombo.setValue(Optional.ofNullable(analyzer))
        ));
        multiFormBinding.add(FormBindings.create(
                // Bean getter
                () -> FXCollections.observableArrayList(category.getSplitDescriptors()),
                // Bean setter
                obsList -> {
                    List<SplitDescriptor> targetSplits = category.getSplitDescriptorsModifiable();
                    targetSplits.clear();
                    targetSplits.addAll(obsList);
                },
                // Form getter
                () -> splitsTableView.getItems(),
                // Form setter
                obsList -> splitsTableView.setItems(obsList)
        ));

        // Validation
        validation.registerValidator(nameTextField, Validator.createEmptyValidator("Name is required"));

        return grid;
    }
}
