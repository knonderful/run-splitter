package runsplitter.application.gui;

import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import runsplitter.VideoAnalyzer;
import runsplitter.application.Game;

/**
 *
 */
public class EditGameDialog {

    public static Game showAndWait(GuiHelper guiHelper, Game gameIn, List<VideoAnalyzer> analyzers) {
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

        Node form = createForm(game, multiFormBinding, validation, analyzers);

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

    private static Node createForm(Game game, MultiFormBinding multiFormBinding, ValidationSupport validation, List<VideoAnalyzer> analyzers) {
        // Name
        TextField nameTextField = new TextField();

        // Default video analyzer
        ComboBox<Optional<VideoAnalyzer>> defaultAnalyzerCombo = GuiHelper.createAnalyzerCombo(analyzers, "(None)");

        GridPane grid = GuiHelper.createFormGrid();
        grid.add(new Label("Name"), 0, 0);
        grid.add(nameTextField, 1, 0);
        grid.add(new Label("Default analyzer"), 0, 1);
        grid.add(defaultAnalyzerCombo, 1, 1);

        // Form binding
        multiFormBinding.add(FormBindings.create(
                game::getName, game::setName,
                nameTextField::getText, nameTextField::setText
        ));
        multiFormBinding.add(FormBindings.create(
                game::getDefaultVideoAnalyzer, game::setDefaultVideoAnalyzer,
                () -> defaultAnalyzerCombo.getValue().orElse(null), analyzer -> defaultAnalyzerCombo.setValue(Optional.ofNullable(analyzer))
        ));

        // Validation
        validation.registerValidator(nameTextField, Validator.createEmptyValidator("Name is required"));

        return grid;
    }
}
