package runsplitter.application.gui;

import java.io.File;
import java.nio.file.Paths;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import runsplitter.application.ApplicationSettings;
import runsplitter.application.GuiTheme;
import runsplitter.common.SaveCallback;

/**
 *
 */
public class SettingsWindow {

    public static Stage createWindow(GuiHelper guiHelper, ApplicationSettings settings, SaveCallback<ApplicationSettings> saveCallback, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);

        TextField defaultVideoDirectoryTextField = new TextField(settings.getVideosDirectory().toString());
        Button defaultVideoDirectoryBrowseButton = new Button("...");
        defaultVideoDirectoryBrowseButton.setOnAction(evt -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose a directory...");
            chooser.setInitialDirectory(new File(defaultVideoDirectoryTextField.getText()));
            File selected = chooser.showDialog(stage);
            if (selected == null) {
                return;
            }

            defaultVideoDirectoryTextField.setText(selected.getAbsolutePath());
        });

        HBox defaultVideoDirectoryHBox = new HBox(defaultVideoDirectoryTextField, defaultVideoDirectoryBrowseButton);
        defaultVideoDirectoryHBox.setSpacing(2);

        ComboBox<GuiTheme> themeCombo = new ComboBox<>(FXCollections.observableArrayList(GuiTheme.values()));

        themeCombo.setConverter(new StringConverter<GuiTheme>() {
            @Override
            public String toString(GuiTheme theme) {
                return theme.getName();
            }

            @Override
            public GuiTheme fromString(String internalName) {
                return GuiTheme.fromName(internalName);
            }
        });
        themeCombo.setValue(settings.getTheme());
        themeCombo.valueProperty().addListener((obs, before, after) -> {
            guiHelper.setCurrentTheme(after);
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(evt -> {
            // Update the ApplicationSettings (in memory)
            settings.setVideosDirectory(Paths.get(defaultVideoDirectoryTextField.getText()));
            settings.setTheme(themeCombo.getValue());

            // Save the ApplicationSettings (to disk)
            GuiHelper.handleException(() -> saveCallback.save(settings), "Could not save the settings");

            stage.close();
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(evt -> stage.close());

        HBox saveCancelHBox = new HBox(saveBtn, cancelBtn);
        saveCancelHBox.setSpacing(10);
        saveCancelHBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(
                new VBox(new Label("Default video directory:"), defaultVideoDirectoryHBox),
                new VBox(new Label("Theme:"), themeCombo),
                saveCancelHBox
        );
        layout.setSpacing(10);
        layout.setPadding(new Insets(2));

        guiHelper.initializeScene(new Scene(layout), stage);
        return stage;
    }
}
