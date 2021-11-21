package application.core.view;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import javafx.stage.Stage;

import java.util.List;

import static application.TrojandaApplication.PRIMARY_BG_COLOR;
import static application.TrojandaApplication.PRIMARY_HIGHLIGHT_BG_COLOR;
import static application.TrojandaApplication.PRIMARY_TEXT_COLOR;
import static application.TrojandaApplication.SECONDARY_BG_COLOR;
import static application.TrojandaApplication.SECONDARY_BORDER_COLOR;
import static application.TrojandaApplication.SECONDARY_HIGHLIGHT_BG_COLOR;
import static application.TrojandaApplication.SECONDARY_TEXT_COLOR;
import static application.core.i18n.I18nService.i18nService;
import static application.core.playlists.PlaylistService.playlistService;

public class AddPlaylistWindow extends Stage {
    private double width = 362;
    private double height = 226;
    private boolean confirm;
    private String playlistName;
    private Stage primaryStage;  // Main stage object


    private TextField textInput;
    private Label info;
    private Button confirmButton;
    private Button cancelButton;

    public String getPlaylistName() {
        return playlistName;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public AddPlaylistWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.initOwner(primaryStage);
        this.setTitle(i18nService.getMessage("AddPlaylistStage.title"));
        this.setX((primaryStage.getWidth() - width) / 2.0 + primaryStage.getX());
        this.setY((primaryStage.getHeight() - height) / 2.0 + primaryStage.getY());
        setSyncCenter(true);  //设置同步居中

        textInput = new TextField();
        textInput.setFocusTraversable(false);
        textInput.setPromptText(i18nService.getMessage("AddPlaylistStage.playlistName"));
        textInput.setFont(new Font("宋体", 16));
        textInput.setPrefWidth(320);
        textInput.setPrefHeight(42);
        textInput.getStylesheets().add("css/AddPlaylistStage.css");
        textInput.textProperty().addListener(this::textInput_onChanged);

        HBox textInputHBox = new HBox();
        textInputHBox.setAlignment(Pos.CENTER);
        textInputHBox.getChildren().addAll(textInput);

        // The red prompt playlist cannot be an empty label
        info = new Label();
        info.setTextFill(Color.rgb(188, 47, 45));
        info.setFont(new Font("宋体", 12));
        info.setPadding(new Insets(0, 0, 0, 25));

        HBox infoHBox = new HBox();
        infoHBox.setAlignment(Pos.CENTER_LEFT);
        infoHBox.getChildren().add(info);


        VBox centerVBox = new VBox(10);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().addAll(textInputHBox, infoHBox);


        confirmButton = new Button(i18nService.getMessage("AddPlaylistStage.Add"));
        confirmButton.setPrefHeight(32);
        //confirmButton.setPrefWidth(92);
        confirmButton.setPadding(new Insets(0, 10, 0, 10));
        confirmButton.setFont(new Font("宋体", 15));
        confirmButton.setBackground(new Background(new BackgroundFill(PRIMARY_BG_COLOR, null, null)));
        confirmButton.setTextFill(PRIMARY_TEXT_COLOR);
        confirmButton.setOnMouseEntered(e -> {
            confirmButton.setBackground(new Background(new BackgroundFill(PRIMARY_HIGHLIGHT_BG_COLOR, null, null)));
        });
        confirmButton.setOnMouseExited(e -> {
            confirmButton.setBackground(new Background(new BackgroundFill(PRIMARY_BG_COLOR, null, null)));
        });
        confirmButton.setOnMouseClicked(this::confirmButton_onMouseClicked);
        confirmButton.setDisable(true);


        cancelButton = new Button(i18nService.getMessage("AddPlaylistStage.Cancel"));
        cancelButton.setPrefHeight(32);
        //cancelButton.setPrefWidth(92);
        cancelButton.setPadding(new Insets(0, 10, 0, 10));
        cancelButton.setFont(new Font("宋体", 15));
        cancelButton.setBackground(new Background(new BackgroundFill(SECONDARY_BG_COLOR, null, null)));
        cancelButton.setTextFill(SECONDARY_TEXT_COLOR);
        cancelButton.setBorder(new Border(new BorderStroke(SECONDARY_BORDER_COLOR, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        cancelButton.setOnMouseEntered(e ->
                cancelButton.setBackground(new Background(new BackgroundFill(SECONDARY_HIGHLIGHT_BG_COLOR, null, null))));
        cancelButton.setOnMouseExited(e ->
                cancelButton.setBackground(new Background(new BackgroundFill(SECONDARY_BG_COLOR, null, null))));
        cancelButton.setOnMouseClicked(this::cancelButton_onMouseClicked);

        HBox buttonsHBox = new HBox(12);
        buttonsHBox.setPrefHeight(64);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsHBox.setPadding(new Insets(0, 20, 0, 0));
        buttonsHBox.getChildren().addAll(confirmButton, cancelButton);


        BorderPane borderPane = new BorderPane();
        borderPane.setPrefHeight(height);
        borderPane.setPrefWidth(width);
        borderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        borderPane.setBorder(new Border(new BorderStroke(Color.rgb(201, 201, 203), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        borderPane.setCenter(centerVBox);
        borderPane.setBottom(buttonsHBox);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("css/AddPlaylistStage.css");
        this.setScene(scene);
    }

    private void textInput_onChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue.trim().equals("")) {
            info.setText(i18nService.getMessage("AddPlaylistStage.ErrorMessage"));
            confirmButton.setDisable(true);
            // OR
            //confirmButton.setMouseTransparent(true);
            //confirmButton.setBackground(new Background(new BackgroundFill(PRIMARY_DISABLED_BG_COLOR, null, null)));
        } else {
            info.setText("");
            confirmButton.setDisable(false);
            // OR
            //confirmButton.setMouseTransparent(false);
            //confirmButton.setBackground(new Background(new BackgroundFill(PRIMARY_BG_COLOR, null, null)));

        }
    }

    private void confirmButton_onMouseClicked(MouseEvent e) {
        String trimedPlaylistName = textInput.getText().trim();
        if (!trimedPlaylistName.equals("") && trimedPlaylistName.length() > 0) {

            List<String> playlistNames = playlistService.getPlaylistNames();

            // If there is a playlist with the same name, close the small stage and return without processing
            for (String playlistName : playlistNames) {
                if (playlistName.equals(trimedPlaylistName)) {
                    this.setConfirm(false);
                    this.hide();
                    return;
                }
            }
            playlistName = trimedPlaylistName;
            this.setConfirm(true);
        }
        this.close();
    }

    private void cancelButton_onMouseClicked(MouseEvent e) {
        this.hide();
    }

    /**
     * Set whether to synchronize the center in the parent form function
     **/
    private void setSyncCenter(boolean bool) {
        if (bool) {
            // The listener that needs to be triggered when the coordinates and width of the main form change,
            // update the position of the sub form, so that the sub form is always centered
            primaryStage.xProperty().addListener((observable, oldValue, newValue) ->
                    AddPlaylistWindow.this.setX((primaryStage.getWidth() - width) / 2.0 + newValue.doubleValue()));
            primaryStage.yProperty().addListener((observable, oldValue, newValue) ->
                    AddPlaylistWindow.this.setY((primaryStage.getHeight() - height) / 2.0 + newValue.doubleValue()));
            primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
                    AddPlaylistWindow.this.setX((newValue.doubleValue() - width) / 2.0 + primaryStage.getX()));
            primaryStage.heightProperty().addListener((observable, oldValue, newValue) ->
                    AddPlaylistWindow.this.setY((newValue.doubleValue() - height) / 2.0 + primaryStage.getY()));
        }
    }

}
