package runsplitter.application.gui;

import java.io.File;
import java.nio.file.Paths;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import runsplitter.application.ApplicationSettings;
import runsplitter.common.SaveCallback;

/**
 *
 */
public class SettingsWindow {

    public static Stage createWindow(ApplicationSettings settings, SaveCallback<ApplicationSettings> saveCallback, String title) {
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

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(evt -> {
            // Update the ApplicationSettings (in memory)
            settings.setVideosDirectory(Paths.get(defaultVideoDirectoryTextField.getText()));

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
                saveCancelHBox
        );
        layout.setSpacing(10);

        layout.setPadding(new Insets(2));
        Scene scene = new Scene(layout);
        GuiHelper.applyTheme(scene);
        stage.setScene(scene);
        return stage;
    }
}
