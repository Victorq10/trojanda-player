package application.core.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static application.core.i18n.DefaultI18nService.i18nService;
import static application.core.preferences.DefaultPreferencesService.preferencesService;
import static application.songs.DefaultSongService.songService;

public class PreferencesStage extends Stage {
    private double width = 700;
    private double height = 400;
    private boolean confirm;
    private List<CheckBox> checkboxList = new ArrayList<>();
    private VBox vWrapCheckBoxList;
    Stage primaryStage;  // Main stage object

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }


    private void addFolderToWrapCheckBoxList(String folderPath) {
        CheckBox checkBox = new CheckBox(folderPath);
        //checkBox.getStylesheets().add("css/CheckBoxStyle.css");
        checkBox.setFont(new Font("宋体", 14));
        checkBox.setSelected(true);
        checkboxList.add(checkBox);

        // Traverse the checkbox read from the file, use the hbox container, and then add the hbox to the vbox
        HBox hBoxCheckBox = new HBox();
        hBoxCheckBox.setPadding(new Insets(0, 0, 0, 10));
        hBoxCheckBox.getChildren().add(checkBox);
        vWrapCheckBoxList.getChildren().add(hBoxCheckBox);

    }

    public PreferencesStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.initOwner(primaryStage);
        this.setX((primaryStage.getWidth() - width) / 2.0 + primaryStage.getX());
        this.setY((primaryStage.getHeight() - height) / 2.0 + primaryStage.getY());
        setSyncCenter(true);  // Set sync center

        ScrollPane foldersListScrollPane = createFolderCheckBoxList();
        HBox footerHBox = createButtons();

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefHeight(height);
        borderPane.setPrefWidth(width);
        borderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        borderPane.setBorder(new Border(new BorderStroke(Color.rgb(201, 201, 203), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        borderPane.setCenter(foldersListScrollPane);
        borderPane.setBottom(footerHBox);
        Scene scene = new Scene(borderPane);
        this.setScene(scene);
        this.setTitle(i18nService.getMessage("ChooseFolderStage.title"));
        this.initModality(Modality.NONE);
    }

    private HBox createButtons() {
        Button btnConfirm = new Button(i18nService.getMessage("ChooseFolderStage.Confirm"));
        btnConfirm.setPrefHeight(32);
        btnConfirm.setPrefWidth(92);
        btnConfirm.setFont(new Font("宋体", 15));
        btnConfirm.setBackground(new Background(new BackgroundFill(Color.rgb(188, 47, 45), null, null)));
        btnConfirm.setTextFill(Color.WHITE);
        btnConfirm.setOnMouseEntered(e -> {
            btnConfirm.setBackground(new Background(new BackgroundFill(Color.rgb(200, 88, 86), null, null)));
        });
        btnConfirm.setOnMouseExited(e -> {
            btnConfirm.setBackground(new Background(new BackgroundFill(Color.rgb(188, 47, 45), null, null)));
        });
        btnConfirm.setOnMouseClicked(this::clickConfirmButton);

        Button btnAddFolder = new Button(i18nService.getMessage("ChooseFolderStage.AddFolder"));
        btnAddFolder.setPrefHeight(32);
        btnAddFolder.setPrefWidth(92);
        btnAddFolder.setPadding(new Insets(0));
        btnAddFolder.setFont(new Font("宋体", 15));
        btnAddFolder.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), null, null)));
        btnAddFolder.setTextFill(Color.rgb(200, 88, 86));
        btnAddFolder.setBorder(new Border(new BorderStroke(Color.rgb(222, 153, 153), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        btnAddFolder.setOnMouseEntered(e -> {
            btnAddFolder.setBackground(new Background(new BackgroundFill(Color.rgb(254, 254, 254), null, null)));
        });
        btnAddFolder.setOnMouseExited(e -> {
            btnAddFolder.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), null, null)));
        });
        btnAddFolder.setOnMouseClicked(this::clickAddFolderButton);


        HBox footerHBox = new HBox(12);
        footerHBox.setPrefHeight(64);
        footerHBox.setAlignment(Pos.CENTER);
        footerHBox.setBorder(new Border(new BorderStroke(Color.rgb(232, 232, 234), null, null, null, BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(1), null)));
        footerHBox.getChildren().addAll(btnConfirm, btnAddFolder);

        return footerHBox;
    }

    private ScrollPane createFolderCheckBoxList() {
        ScrollPane foldersListScrollPane = new ScrollPane();

        HBox hBoxInfo = new HBox();
        Label labInfo = new Label(i18nService.getMessage("ChooseFolderStage.InfoMessage"));
        labInfo.setFont(new Font("宋体", 12));
        labInfo.setTextFill(Color.rgb(153, 153, 153));
        labInfo.setPadding(new Insets(20, 0, 0, 10));
        hBoxInfo.getChildren().add(labInfo);

        vWrapCheckBoxList = new VBox(20);  // Install the CheckBox for prompt and selected folder
        vWrapCheckBoxList.getChildren().addAll(hBoxInfo);

        // Initialize and determine whether the selected directory was saved last time
        List<String> musicLibraryLocations = preferencesService.getMusicLibraryLocations();
        for (String musicLibraryLocation : musicLibraryLocations) {
            // Create a CheckBox and save it to the List collection
            addFolderToWrapCheckBoxList(musicLibraryLocation);
        }

        foldersListScrollPane.setContent(vWrapCheckBoxList);
        foldersListScrollPane.getStylesheets().add("css/PreferencesScrollPaneStyle.css");
        return foldersListScrollPane;
    }

    private void clickAddFolderButton(MouseEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(this);
        if (directory != null) {
            String folderPath = directory.getPath();
            if (!preferencesService.isFolderExists(folderPath)) {
                addFolderToWrapCheckBoxList(folderPath);
            }
        }
    }

    private void clickConfirmButton(MouseEvent e) {
        if (checkboxList != null && checkboxList.size() >= 1) {
            Iterator<CheckBox> it = checkboxList.iterator();
            while (it.hasNext()) {
                CheckBox checkBox = it.next();
                if (checkBox.isSelected()) {
                    preferencesService.addMediaFolder(checkBox.getText());
                } else {
                    preferencesService.removeMediaFolder(checkBox.getText());
                    it.remove();
                }
            }
            if (checkboxList.isEmpty()) {
                songService.removeMediaLibrary();
            }
        }
        this.setConfirm(true);
        this.close();
    }

    private void setSyncCenter(boolean bool) {
        if (bool) {
            // The listener that needs to be triggered when the coordinates and width of the main form change,
            // update the position of the sub form, so that the sub form is always centered
            primaryStage.xProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    PreferencesStage.this.setX((primaryStage.getWidth() - width) / 2.0 + newValue.doubleValue());
                }

            });
            primaryStage.yProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    PreferencesStage.this.setY((primaryStage.getHeight() - height) / 2.0 + newValue.doubleValue());
                }

            });
            primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    PreferencesStage.this.setX((newValue.doubleValue() - width) / 2.0 + primaryStage.getX());
                }

            });
            primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    PreferencesStage.this.setY((newValue.doubleValue() - height) / 2.0 + primaryStage.getY());
                }

            });
        }
    }
}
