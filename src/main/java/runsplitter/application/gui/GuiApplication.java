package runsplitter.application.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import runsplitter.VideoAnalyzer;
import runsplitter.YoshisIslandAnalyzer;
import runsplitter.application.ApplicationSettingsPersistence;
import runsplitter.application.ApplicationState;
import runsplitter.application.Category;
import runsplitter.application.Game;
import runsplitter.application.GameLibrary;
import runsplitter.application.GameLibraryPersistence;
import runsplitter.application.SplitDescriptor;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableSpeedrun;

/**
 *
 */
public class GuiApplication extends Application {

    private static final Logger LOG = Logger.getLogger(GuiApplication.class.getName());
    private static final String APPLICATION_TITLE = "Run splitter";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Catch loading issues and show an error message in the GUI or something...
        List<VideoAnalyzer> analyzers = Arrays.asList(new YoshisIslandAnalyzer());
        GameLibraryPersistence libraryPersistence = new GameLibraryPersistence(() -> analyzers);
        Supplier<GameLibraryPersistence> libraryPersistenceSupplier = () -> libraryPersistence;

        ApplicationState state = new ApplicationState(
                ApplicationSettingsPersistence.load(),
                libraryPersistence.load(),
                analyzers);

        Supplier<ApplicationState> stateSupplier = () -> state;

        AtomicReference<GameLibrary> lastSavedLibrary = new AtomicReference<>(libraryPersistence.load());

        GuiHelper guiHelper = new GuiHelper(state.getSettings().getTheme());

        // Exit the application if all windows are closed
        Platform.setImplicitExit(true);

        AtomicReference<ReadOnlyObjectProperty<MutableSpeedrun>> selectedRunPropertyReference = new AtomicReference<>();
        AtomicReference<ReadOnlyObjectProperty<Category>> categoryPropertyReference = new AtomicReference<>();
        SplitPane splitPane = new SplitPane(
                createRunSelectionPane(guiHelper, stateSupplier, primaryStage, categoryPropertyReference::set, selectedRunPropertyReference::set),
                createCurrentRunPane(categoryPropertyReference.get(), selectedRunPropertyReference.get())
        );

        BorderPane mainPane = new BorderPane(
                splitPane, // center
                createMenuPane(guiHelper, stateSupplier, primaryStage, libraryPersistenceSupplier, lastSavedLibrary::set), // top
                null, // right
                null, // bottom
                null // left
        );

        guiHelper.initializeScene(new Scene(mainPane, 640, 480), primaryStage);
        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.setOnCloseRequest(evt -> {
            GameLibrary library = stateSupplier.get().getLibrary();
            if (!library.equals(lastSavedLibrary.get())) {
                Dialog<ButtonType> confirmationDialog = new Dialog<>();
                confirmationDialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                confirmationDialog.setContentText("Do you want to save the changes to the game library?");
                confirmationDialog.setTitle("Save changes");
                confirmationDialog.showAndWait().ifPresent(btnType -> {
                    if (btnType == ButtonType.YES) {
                        try {
                            libraryPersistenceSupplier.get().save(library);
                        } catch (IOException e) {
                            LOG.log(Level.SEVERE, "Could not save game library.", e);
                        }
                    }
                });
            }
        });
        primaryStage.show();
    }

    private static Node createCurrentRunPane(ReadOnlyObjectProperty<Category> categorySelectedItemProperty, ReadOnlyObjectProperty<MutableSpeedrun> runsSelectedItemProperty) {
        // Split times table
        Label splitsLbl = new Label("Split times:");
        TableView<SplitEntry> splitsTableView = new TableView<>();
        // Disable sorting in the split times table
        splitsTableView.setSortPolicy(entry -> false);
        TableColumn<SplitEntry, String> splitsNameCol = new TableColumn<>("Name");
        splitsNameCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getName()));
        TableColumn<SplitEntry, String> splitsTimeCol = new TableColumn<>("Time");
        splitsTimeCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getTime().toTimestamp()));
        TableColumn<SplitEntry, String> splitsDescriptionCol = new TableColumn<>("Description");
        splitsDescriptionCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getDescription()));
        ObservableList<TableColumn<SplitEntry, ?>> splitsCols = splitsTableView.getColumns();
        splitsCols.add(splitsNameCol);
        splitsCols.add(splitsTimeCol);
        splitsCols.add(splitsDescriptionCol);

        runsSelectedItemProperty.addListener((observable, oldRun, newRun) -> {
            ObservableList<SplitEntry> observableList;
            if (newRun == null) {
                observableList = FXCollections.unmodifiableObservableList(FXCollections.emptyObservableList());
            } else {
                Category category = categorySelectedItemProperty.get();
                List<Instant> splits = newRun.getMarkers().getSplits();
                List<SplitDescriptor> descriptors = category.getSplitDescriptors();
                List<SplitEntry> entries = new ArrayList<>(splits.size());

                Iterator<SplitDescriptor> descIterator = descriptors.iterator();
                for (Instant split : splits) {
                    String name;
                    String description;
                    if (descIterator.hasNext()) {
                        SplitDescriptor descriptor = descIterator.next();
                        name = descriptor.getName();
                        description = descriptor.getDescription();
                    } else {
                        name = null;
                        description = null;
                    }

                    entries.add(new SplitEntry(name, split, description));
                }
                observableList = FXCollections.observableList(entries);
            }
            splitsTableView.setItems(observableList);
            splitsTableView.getSelectionModel().select(0);
        });

        ObservableList<SplitEntry> splitsItems = splitsTableView.getItems();
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

    private static Node createRunSelectionPane(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier, Stage primaryStage, Consumer<ReadOnlyObjectProperty<Category>> categoryPropertyConsumer, Consumer<ReadOnlyObjectProperty<MutableSpeedrun>> speedRunPropertyConsumer) {
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
                () -> EditGameDialog.showAndWait(guiHelper, null, stateSupplier.get().getAnalyzers()),
                game -> gameListView.getItems().add(game));
        Button gameRemoveBtn = guiHelper.createRemoveButton(
                gameSelectedItemProperty,
                Game::getName,
                game -> gameListView.getItems().remove(game));
        Button gameEditBtn = guiHelper.createEditButton(
                gameSelectedItemProperty,
                game -> {
                    EditGameDialog.showAndWait(guiHelper, game, stateSupplier.get().getAnalyzers());
                    // Refresh the view in case the name of the game was changed
                    gameListView.refresh();
                }
        );
        Button gameUpBtn = guiHelper.createMoveUpButton(gameListView);
        Button gameDownBtn = guiHelper.createMoveDownButton(gameListView);

        VBox gameBox = new VBox(
                new Label("Game:"),
                gameListView,
                GuiHelper.createControlsBox(gameAddBtn, gameRemoveBtn, gameEditBtn, gameUpBtn, gameDownBtn)
        );

        ListView<Category> categoryListView = new ListView<>();
        categoryListView.setCellFactory(listView -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);

                if (empty || category == null || category.getName() == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
        // Update categories when game is changed
        gameSelectedItemProperty.addListener((observable, oldVal, newVal) -> {
            ObservableList<Category> observableList;
            if (newVal == null) {
                observableList = FXCollections.unmodifiableObservableList(FXCollections.emptyObservableList());
            } else {
                observableList = FXCollections.observableList(newVal.getCategoriesModifiable());
            }
            categoryListView.setItems(observableList);
            categoryListView.getSelectionModel().select(0);
        });

        ReadOnlyObjectProperty<Category> categorySelectedItemProperty = categoryListView.getSelectionModel().selectedItemProperty();
        categoryPropertyConsumer.accept(categorySelectedItemProperty);
        Button categoryAddBtn = guiHelper.createAddButton(
                () -> EditCategoryDialog.showAndWait(guiHelper, null, stateSupplier.get().getAnalyzers()),
                category -> categoryListView.getItems().add(category),
                gameSelectedItemProperty);
        Button categoryRemoveBtn = guiHelper.createRemoveButton(
                categorySelectedItemProperty,
                Category::getName,
                category -> categoryListView.getItems().remove(category));
        Button categoryEditBtn = guiHelper.createEditButton(
                categorySelectedItemProperty,
                category -> {
                    EditCategoryDialog.showAndWait(guiHelper, category, stateSupplier.get().getAnalyzers());
                    // Refresh the view in case the name of the category was changed
                    categoryListView.refresh();
                }
        );
        Button categoryUpBtn = guiHelper.createMoveUpButton(categoryListView);
        Button categoryDownBtn = guiHelper.createMoveDownButton(categoryListView);

        VBox categoryBox = new VBox(
                new Label("Category:"),
                categoryListView,
                GuiHelper.createControlsBox(categoryAddBtn, categoryRemoveBtn, categoryEditBtn, categoryUpBtn, categoryDownBtn)
        );

        // Speedruns
        TableColumn<MutableSpeedrun, String> runsTimeCol = new TableColumn<>("Time");
        runsTimeCol.setCellValueFactory(entry
                -> new ReadOnlyObjectWrapper<>(entry.getValue().getMarkers().getFinalSplit().toTimestamp()));
        TableColumn<MutableSpeedrun, String> runsSourceCol = new TableColumn<>("Source");
        runsSourceCol.setCellValueFactory(entry -> new ReadOnlyObjectWrapper<>(entry.getValue().getSourceName()));
        TableView<MutableSpeedrun> runsTableView = new TableView<>();
        ObservableList<TableColumn<MutableSpeedrun, ?>> runsCols = runsTableView.getColumns();
        runsCols.add(runsTimeCol);
        runsCols.add(runsSourceCol);

        ReadOnlyObjectProperty<MutableSpeedrun> runsSelectedItemProperty = runsTableView.getSelectionModel().selectedItemProperty();
        speedRunPropertyConsumer.accept(runsSelectedItemProperty);
        Button runsAddBtn = guiHelper.createAddButton(
                () -> {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Choose a video file...");
                    chooser.setInitialDirectory(stateSupplier.get().getSettings().getVideosDirectory().toFile());
                    File selected = chooser.showOpenDialog(primaryStage);
                    if (selected == null) {
                        return null;
                    }

                    // TODO: Select the correct video analyzer for this category/game
                    return AnalyzeVideoDialog.showAndWait(guiHelper, selected, new YoshisIslandAnalyzer());
                },
                run -> {
                    // TODO: Add at the correct place in the list
                    runsTableView.getItems().add(0, run);
                },
                categoryListView.getSelectionModel().selectedItemProperty()
        );
        Button runsRemoveBtn = guiHelper.createRemoveButton(
                runsSelectedItemProperty,
                run -> null, // No name means the dialog will handle it as anonymous
                run -> runsTableView.getItems().remove(run)
        );
        Button runsEditBtn = guiHelper.createEditButton(
                runsSelectedItemProperty,
                run -> {
                    Dialog dlg = new Dialog();
                    dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                    dlg.setContentText("Not yet implemented.");
                    dlg.showAndWait();
                }
        );

        // Update speedruns when category is changed
        categorySelectedItemProperty.addListener((observable, oldVal, newVal) -> {
            ObservableList<MutableSpeedrun> observableList;
            if (newVal == null) {
                observableList = FXCollections.unmodifiableObservableList(FXCollections.emptyObservableList());
            } else {
                observableList = FXCollections.observableList(newVal.getSpeedrunsModifiable());
            }
            runsTableView.setItems(observableList);
            runsTableView.getSelectionModel().select(0);
        });

        VBox runsBox = new VBox(
                new Label("Runs:"),
                runsTableView,
                GuiHelper.createControlsBox(runsAddBtn, runsRemoveBtn, runsEditBtn)
        );

        StackPane analyzeControlsSeparator = new StackPane(new Separator());
        analyzeControlsSeparator.setPadding(new Insets(10, 2, 10, 2));

        VBox leftVBox = new VBox(gameBox, categoryBox, runsBox);
        leftVBox.setPadding(new Insets(2));
        return leftVBox;
    }

    private static void showSettings(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier, Stage primaryStage) {
        Stage settingsWindow = SettingsWindow.createWindow(guiHelper, stateSupplier.get().getSettings(), ApplicationSettingsPersistence::save, String.format("%s - Settings", APPLICATION_TITLE));
        settingsWindow.initModality(Modality.WINDOW_MODAL);
        settingsWindow.initOwner(primaryStage);
        settingsWindow.show();
    }

    private static Node createMenuPane(GuiHelper guiHelper, Supplier<ApplicationState> stateSupplier, Stage primaryStage, Supplier<GameLibraryPersistence> libraryPersistenceSupplier, Consumer<GameLibrary> libraryCopyConsumer) {
        // File menu
        MenuItem fileSave = new MenuItem("Save");
        fileSave.setOnAction(evt -> {
            GameLibrary library = stateSupplier.get().getLibrary();
            GameLibraryPersistence libraryPersistence = libraryPersistenceSupplier.get();
            GuiHelper.handleException(() -> libraryPersistence.save(library));
            try {
                libraryCopyConsumer.accept(libraryPersistence.load());
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Could not load game library.", e);
            }
        });
        MenuItem fileQuit = new MenuItem("Quit");
        fileQuit.setOnAction(evt -> {
            primaryStage.fireEvent(
                    new WindowEvent(
                            primaryStage,
                            WindowEvent.WINDOW_CLOSE_REQUEST
                    )
            );
        });
        Menu fileMenu = new Menu("File", null, fileSave, fileQuit);

        // Tools menu
        MenuItem toolsSettings = new MenuItem("Settings...");
        toolsSettings.setOnAction(evt -> showSettings(guiHelper, stateSupplier, primaryStage));
        Menu toolsMenu = new Menu("Tools", null, toolsSettings);

        return new MenuBar(fileMenu, toolsMenu);
    }

    /**
     * An entry in a splits table.
     */
    private static class SplitEntry {

        private final Instant time;
        private final String name;
        private final String description;

        public SplitEntry(String name, Instant totalTime, String description) {
            this.name = name;
            this.time = totalTime;
            this.description = description;
        }

        public Instant getTime() {
            return time;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
