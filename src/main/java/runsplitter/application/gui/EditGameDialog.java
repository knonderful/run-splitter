package runsplitter.application.gui;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import runsplitter.application.Game;

/**
 *
 */
public class EditGameDialog {

    public static Game showAndWait(GuiHelper guiHelper, Game gameIn) {
        final Game game;
        boolean isNew;
        if (gameIn == null) {
            isNew = true;
            game = new Game();
        } else {
            isNew = false;
            game = gameIn;
        }

        // Validation
        ValidationSupport validation = new ValidationSupport();
        // Form binding (for transferring values between bean and the form)
        MultiFormBinding multiFormBinding = new MultiFormBinding();

        Node form = createForm(game, multiFormBinding, validation);

        // Transfer all bean values to the form
        multiFormBinding.beanToForm();

        String title;
        if (isNew) {
            title = "New game";
        } else {
            title = String.format("Edit %s", game.getName());
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

        return game;
    }

    private static Node createForm(final Game game, MultiFormBinding multiFormBinding, ValidationSupport validation) {
        TextField nameTextField = new TextField();
        validation.registerValidator(nameTextField, Validator.createEmptyValidator("Name is required"));
        multiFormBinding.add(FormBindings.create(
                game::getName, game::setName,
                nameTextField::getText, nameTextField::setText
        ));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.add(new Label("Name"), 0, 0);
        grid.add(nameTextField, 1, 0);
        return grid;
    }
}
