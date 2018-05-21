package runsplitter.application.gui;

import com.sun.javafx.collections.ImmutableObservableList;
import java.io.IOException;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import runsplitter.application.ApplicationSettingsPersistence;
import runsplitter.application.ApplicationState;
import runsplitter.application.Game;
import runsplitter.application.GameLibrary;
import runsplitter.speedrun.Instant;

/**
 *
 */
public class GuiApplication extends Application {

    private static final String APPLICATION_TITLE = "Run splitter";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Catch loading issues and show an error message in the GUI or something...
        ApplicationState state = new ApplicationState(
                ApplicationSettingsPersistence.load(),
                new GameLibrary() // TODO: load
        );
        Supplier<ApplicationState> stateSupplier = () -> state;

        GuiHelper guiHelper = new GuiHelper(state.getSettings().getTheme());

        // Exit the application if all windows are closed
        Platform.setImplicitExit(true);

        SplitPane splitPane = new SplitPane(createRunSelectionPane(guiHelper, stateSupplier), createCurrentRunPane());

        BorderPane mainPane = new BorderPane(
                splitPane, // center
                createMenuPane(guiHelper, stateSupplier, primaryStage), // top
                null, // right
                null, // bottom
                null // left
        );

        guiHelper.initializeScene(new Scene(mainPane, 640, 480), primaryStage);
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.show();
    }

    private static Node createCurrentRunPane() {
        // Split times table
        Label splitsLbl = new Label("Split times:");
        TableView<SplitEntry> splitsTableView = new TableView<>();
        // Disable sorting in the split times table
        splitsTableView.setSortPolicy(entry -> false);
        TableColumn<SplitEntry, String> splitsLabelCol = new TableColumn<>("Label");
        splitsLabelCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getSplitLabel()));
        TableColumn<SplitEntry, String> splitsTimeCol = new TableColumn<>("Time");
        splitsTimeCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getTime().toTimestamp()));
        ObservableList<TableColumn<SplitEntry, ?>> splitsCols = splitsTableView.getColumns();
        splitsCols.add(splitsLabelCol);
        splitsCols.add(splitsTimeCol);
        ObservableList<SplitEntry> splitsItems = splitsTableView.getItems();
        splitsItems.add(new SplitEntry("1-1", new Instant(120320L)));
        splitsItems.add(new SplitEntry("1-2", new Instant(288722L)));
        splitsItems.add(new SplitEntry("1-3", new Instant(379810L)));
        splitsItems.add(new SplitEntry("1-4", new Instant(502188L)));
        splitsItems.add(new SplitEntry("1-5", new Instant(662889L)));
        splitsItems.add(new SplitEntry("1-6", new Instant(813878L)));
        splitsItems.add(new SplitEntry("1-7", new Instant(952319L)));
        splitsItems.add(new SplitEntry("1-8", new Instant(1212310L)));

        // Time slider
        Slider timeSlider = new Slider(0, 100, 0);
        Label timeSliderLabel = new Label(new Instant(0).toTimestamp());
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> timeSliderLabel.setText(new Instant(newVal.longValue() * 1212310L / 100L).toTimestamp()));
        VBox timeControlBox = new VBox(timeSlider, timeSliderLabel);
        timeControlBox.setAlignment(Pos.CENTER);

        // Video output settings
        Label outputModeLbl = new Label("Output:");
        ObservableList<String> outputModeOptions = FXCollections.observableArrayList("Disabled", "Embedded", "VLC");
        ComboBox<String> outputModeCombo = new ComboBox<>(outputModeOptions);
        outputModeCombo.setValue("VLC");

        // Separator between settings and controls
        StackPane controlsSeparator = new StackPane(new Separator());
        controlsSeparator.setPadding(new Insets(10, 2, 10, 2));

        // Video/run controls
        Button playBtn = new Button("Play");
        Button editBtn = new Button("Edit");
        HBox controlButtonsHBox = new HBox(playBtn, editBtn);
        controlButtonsHBox.setSpacing(10);
        controlButtonsHBox.setAlignment(Pos.CENTER);

        // Layout of all elements in this pane
        VBox centerBox = new VBox(
                splitsLbl,
                splitsTableView,
                new Label("Time:"),
                timeControlBox,
                outputModeLbl,
                outputModeCombo,
                controlsSeparator,
                controlButtonsHBox
        );
        centerBox.setPadding(new Insets(2));
        VBox.setVgrow(splitsTableView, Priority.ALWAYS);

        return centerBox;
    }

    private static Node createRunSelectionPane(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier) throws IOException {
        GameLibrary library = stateSupplier.get().getLibrary();

        ListView<Game> gameListView = new ListView<>(FXCollections.observableList(library.getGamesModifiable()));
        gameListView.setCellFactory(listView -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);

                if (empty || game == null || game.getName() == null) {
                    setText(null);
                } else {
                    setText(game.getName());
                }
            }
        });

        ReadOnlyObjectProperty<Game> gameSelectedItemProperty = gameListView.getSelectionModel().selectedItemProperty();
        Button gameAddBtn = guiHelper.createAddButton(
                () -> EditGameDialog.showAndWait(guiHelper, null),
                game -> gameListView.getItems().add(game));
        Button gameRemoveBtn = guiHelper.createRemoveButton(
                gameSelectedItemProperty,
                Game::getName,
                game -> gameListView.getItems().remove(game));
        Button gameEditBtn = guiHelper.createEditButton(
                gameSelectedItemProperty,
                game -> {
                    EditGameDialog.showAndWait(guiHelper, game);
                    // Refresh the view in case the name of the game was changed
                    gameListView.refresh();
                }
        );
        Button gameUpBtn = guiHelper.createMoveUpButton(gameListView);
        Button gameDownBtn = guiHelper.createMoveDownButton(gameListView);

        VBox gameBox = new VBox(
                new Label("Game:"),
                gameListView,
                createControlsBox(gameAddBtn, gameRemoveBtn, gameEditBtn, gameUpBtn, gameDownBtn)
        );

        ListView<String> categoryListView = new ListView<>(new ImmutableObservableList<>("Clean World 1-2 100%", "Full 100%", "World 1 100%", "World 2 100%", "World 3 100%"));

        ReadOnlyObjectProperty<String> categorySelectedItemProperty = categoryListView.getSelectionModel().selectedItemProperty();
        Button categoryAddBtn = guiHelper.createAddButton(null, null);
        Button categoryRemoveBtn = guiHelper.createRemoveButton(categorySelectedItemProperty, null, null);
        Button categoryEditBtn = guiHelper.createEditButton(categorySelectedItemProperty, null);
        Button categoryUpBtn = guiHelper.createMoveUpButton(categoryListView);
        Button categoryDownBtn = guiHelper.createMoveDownButton(categoryListView);

        VBox categoryBox = new VBox(
                new Label("Category:"),
                categoryListView,
                createControlsBox(categoryAddBtn, categoryRemoveBtn, categoryEditBtn, categoryUpBtn, categoryDownBtn)
        );

        TableColumn<RunEntry, String> runsTimeCol = new TableColumn<>("Total time");
        runsTimeCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getTotalTime().toTimestamp()));
        TableColumn<RunEntry, String> runsVideoCol = new TableColumn<>("Video");
        runsVideoCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getVideoName()));
        TableView<RunEntry> runsTableView = new TableView<>();
        ObservableList<TableColumn<RunEntry, ?>> runsCols = runsTableView.getColumns();
        runsCols.add(runsTimeCol);
        runsCols.add(runsVideoCol);
        ObservableList<RunEntry> runsItems = runsTableView.getItems();
        runsItems.add(new RunEntry(new Instant(321878L), "W1_Perfect_run.mkv"));
        runsItems.add(new RunEntry(new Instant(421878L), "W1_Got_lava_skip.mkv"));
        runsItems.add(new RunEntry(new Instant(621878L), "W1_First_try.mkv"));

        ReadOnlyObjectProperty<RunEntry> runsSelectedItemProperty = runsTableView.getSelectionModel().selectedItemProperty();
        Button runsAddBtn = guiHelper.createAddButton(null, null);
        Button runsRemoveBtn = guiHelper.createRemoveButton(runsSelectedItemProperty, null, null);
        Button runsEditBtn = guiHelper.createEditButton(runsSelectedItemProperty, null);

        VBox runsBox = new VBox(
                new Label("Runs:"),
                runsTableView,
                createControlsBox(runsAddBtn, runsRemoveBtn, runsEditBtn)
        );

        StackPane analyzeControlsSeparator = new StackPane(new Separator());
        analyzeControlsSeparator.setPadding(new Insets(10, 2, 10, 2));

        VBox leftVBox = new VBox(gameBox, categoryBox, runsBox);
        leftVBox.setPadding(new Insets(2));
        return leftVBox;
    }

    private static Node createControlsBox(Button... buttons) {
        HBox gameControlsBox = new HBox(buttons);
        gameControlsBox.setAlignment(Pos.TOP_RIGHT);
        gameControlsBox.setPadding(new Insets(1, 0, 1, 0));
        gameControlsBox.setSpacing(2);
        return gameControlsBox;
    }

    private static void showSettings(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier, Stage primaryStage) {
        Stage settingsWindow = SettingsWindow.createWindow(guiHelper, stateSupplier.get().getSettings(), ApplicationSettingsPersistence::save, String.format("%s - Settings", APPLICATION_TITLE));
        settingsWindow.initModality(Modality.WINDOW_MODAL);
        settingsWindow.initOwner(primaryStage);
        settingsWindow.show();
    }

    private static Node createMenuPane(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier, Stage primaryStage) {
        // File menu
        MenuItem fileQuit = new MenuItem("Quit");
        fileQuit.setOnAction(evt -> primaryStage.close());
        Menu fileMenu = new Menu("File", null, fileQuit);

        // Tools menu
        MenuItem toolsSettings = new MenuItem("Settings...");
        toolsSettings.setOnAction(evt -> showSettings(guiHelper, stateSupplier, primaryStage));
        Menu toolsMenu = new Menu("Tools", null, toolsSettings);

        return new MenuBar(fileMenu, toolsMenu);
    }

    /**
     * Temporary class for creating the runs table.
     */
    private static class RunEntry {

        private final Instant totalTime;
        private final String videoName;

        public RunEntry(Instant totalTime, String videoName) {
            this.totalTime = totalTime;
            this.videoName = videoName;
        }

        public Instant getTotalTime() {
            return totalTime;
        }

        public String getVideoName() {
            return videoName;
        }

    }

    /**
     * Temporary class for creating the split times table.
     */
    private static class SplitEntry {

        private final Instant time;
        private final String splitLabel;

        public SplitEntry(String splitLabel, Instant totalTime) {
            this.splitLabel = splitLabel;
            this.time = totalTime;
        }

        public Instant getTime() {
            return time;
        }

        public String getSplitLabel() {
            return splitLabel;
        }

    }
}
