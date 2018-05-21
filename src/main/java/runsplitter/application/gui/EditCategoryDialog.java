package runsplitter.application.gui;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import runsplitter.application.Category;

/**
 *
 */
public class EditCategoryDialog {

    public static Category showAndWait(GuiHelper guiHelper, Category categoryIn) {
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

        Node form = createForm(category, multiFormBinding, validation);

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

    private static Node createForm(Category category, MultiFormBinding multiFormBinding, ValidationSupport validation) {
        TextField nameTextField = new TextField();
        validation.registerValidator(nameTextField, Validator.createEmptyValidator("Name is required"));
        multiFormBinding.add(FormBindings.create(
                category::getName, category::setName,
                nameTextField::getText, nameTextField::setText
        ));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.add(new Label("Name"), 0, 0);
        grid.add(nameTextField, 1, 0);
        return grid;
    }
}
