package application.core.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static application.core.i18n.I18nService.i18nService;
import static application.core.view.GlobalMenu.globalMenu;
import static application.playlists.PlaylistService.playlistService;

public class PlayListsFx extends BorderPane {

    private Stage primaryStage;

    private List<HBox> clickableHBoxList = new ArrayList<>();
    private Label addPlaylistLabel;
    private VBox playlistsVBox;


    public PlayListsFx(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // My music label
        HBox libraryHBox = createLibraryHBox();
        // Local music HBox. Picture of local music
        HBox localMusicTagHBox = this.createClickableHBox("image/MusicLocalMusicIcon.png", i18nService.getMessage("LocalMusic"));
        // Add playlist. Display the label of "created playlist"
        BorderPane playlistsHBox = createPlaylistsBorderPane();
        // Recently played music HBox
        HBox recentPlayTagHBox = this.createClickableHBox("image/MusicRecentPlayIcon.png", i18nService.getMessage("RecentPlay"));
        HBox mostPlayedTagHBox = this.createClickableHBox("image/MusicRecentPlayIcon.png", i18nService.getMessage("MostPlayed"));
        HBox recentlyAddedTagHBox = this.createClickableHBox("image/MusicRecentPlayIcon.png", i18nService.getMessage("RecentlyAdded"));
        HBox mostRatedTagHBox = this.createClickableHBox("image/MusicRecentPlayIcon.png", i18nService.getMessage("MostRated"));

        // A VBox container package logo, author, production date, HBox (label and ImageLabel)
        playlistsVBox = new VBox();
        playlistsVBox.setPadding(new Insets(5, 5, 5, 0));
        playlistsVBox.getChildren().addAll(
                libraryHBox,
                localMusicTagHBox,
                playlistsHBox,
                recentPlayTagHBox,
                mostPlayedTagHBox,
                recentlyAddedTagHBox,
                mostRatedTagHBox);
        playlistsVBox.getChildren().addAll(this.createPlaylistHBoxList());

        // Set mouse events
        addPlaylistLabel.setOnMouseClicked(this::showAddGroupStage_onMouseClick);

        Pane content = new Pane();
        content.getChildren().addAll(playlistsVBox);

        ScrollPane scrollpane = new ScrollPane();
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setPadding(new Insets(0, 5, 0, 0));
        scrollpane.setContent(content);
        scrollpane.setBorder(new Border(new BorderStroke(
                null, Color.rgb(221, 221, 225), Color.rgb(221, 221, 225), null,
                null, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null,
                null, new BorderWidths(0, 1, 0, 0), null)));
        scrollpane.getStylesheets().add("css/ScrollPane.css");

        HBox userInfoHBox = createUserInfoHBox();

        this.setBackground(new Background(new BackgroundFill(Color.rgb(243, 243, 245), null, null)));
        this.setPadding(new Insets(0, 0, 0, 5));
        this.setCenter(scrollpane);
        this.setBottom(userInfoHBox);
    }

    private HBox createLibraryHBox() {
        Label libraryLabel = new Label(i18nService.getMessage("leftPane.Library"));
        //libraryLabel.setPrefWidth(180);
        libraryLabel.setFont(new Font("黑体", 12));
        libraryLabel.setPadding(new Insets(5, 0, 5, 4));
        libraryLabel.getStyleClass().add("infoLabel");

        HBox libraryHBox = new HBox(10, libraryLabel);
        //libraryHBox.setPrefHeight(40);
        libraryHBox.setAlignment(Pos.CENTER_LEFT);
        libraryHBox.setPadding(new Insets(0, 0, 0, 0));

        return libraryHBox;
    }

    // Create an HBoxTag by passing in the label icon of the Tag label and the displayed text
    private HBox createClickableHBox(String iconPath, String text) {
        Label iconLabel = new Label("", new ImageView(iconPath));
        Label textLabel = new Label(text);

        textLabel.setFont(new Font("宋体", 13));
        HBox leftHBoxTag = new HBox(12, iconLabel, textLabel);
        //leftHBoxTag.setPrefHeight(40);
        leftHBoxTag.setAlignment(Pos.CENTER_LEFT);
        leftHBoxTag.setPadding(new Insets(5, 0, 5, 6));

        clickableHBoxList.add(leftHBoxTag);
        leftHBoxTag.setOnMouseEntered(ee -> {
            if (!leftHBoxTag.isMouseTransparent())
                leftHBoxTag.setBackground(new Background(new BackgroundFill(Color.rgb(232, 232, 232), null, null)));
        });
        leftHBoxTag.setOnMouseExited(ee -> {
            leftHBoxTag.setBackground(new Background(new BackgroundFill(Color.rgb(243, 243, 245), null, null)));
            for (HBox h : clickableHBoxList) {
                if (h.isMouseTransparent())
                    h.setBackground(new Background(new BackgroundFill(Color.rgb(221, 221, 225), null, null)));
            }
        });
        leftHBoxTag.setOnMouseClicked(ee -> {
            for (HBox h : clickableHBoxList) {
                h.setMouseTransparent(false);
                h.setBackground(new Background(new BackgroundFill(Color.rgb(243, 243, 245), null, null)));
            }
            leftHBoxTag.setBackground(new Background(new BackgroundFill(Color.rgb(221, 221, 225), null, null)));
            leftHBoxTag.setMouseTransparent(true);
        });
        return leftHBoxTag;
    }

    private BorderPane createPlaylistsBorderPane() {
        Label playlistsLabel = new Label(i18nService.getMessage("Playlists"));
        playlistsLabel.setFont(new Font("宋体", 12));
        playlistsLabel.setPadding(new Insets(5, 0, 0, 4));
        playlistsLabel.getStyleClass().add("infoLabel");

        // ImageView showing the added playlist
        ImageView addPlaylistImageView = new ImageView("image/AddPlaylistIcon.png");
        addPlaylistImageView.setFitWidth(20);
        addPlaylistImageView.setFitHeight(20);
        addPlaylistLabel = new Label("", addPlaylistImageView);
        addPlaylistLabel.setPadding(new Insets(2, 0, 0, 0));

        BorderPane pane = new BorderPane();
        //pane.setPrefHeight(20);
        pane.setLeft(playlistsLabel);
        pane.setRight(addPlaylistLabel);
        //pane.setPadding(new Insets(5, 0, 0, 4));

        return pane;
    }

    private List<HBox> createPlaylistHBoxList() {
        List<String> playlistNames = playlistService.getPlaylistNames();
        List<HBox> playlistHBoxList = playlistNames.stream()
                .map(this::createPlaylistHBox)
                .collect(Collectors.toList());
        return playlistHBoxList;
    }

    private HBox createPlaylistHBox(String playlistName) {
        return this.createClickableHBox("image/MusicPlaylistIcon.png", playlistName);
    }

    private HBox createUserInfoHBox() {
        ImageView userIconImageView = new ImageView("image/mallow32.png");
        userIconImageView.setFitWidth(32);
        userIconImageView.setPreserveRatio(true);// Set the picture to keep the aspect ratio
        // Set the picture as a circle
        Circle userIconCircle = new Circle();
        userIconCircle.setCenterX(16);
        userIconCircle.setCenterY(16);
        userIconCircle.setRadius(16);
        userIconImageView.setClip(userIconCircle);

        Label userName = new Label(i18nService.getMessage("application.author"));

        HBox userInfoHBox = new HBox(10);
        userInfoHBox.setMinHeight(50);
        userInfoHBox.setAlignment(Pos.CENTER_LEFT);
        userInfoHBox.setPadding(new Insets(0, 0, 0, 10));
        userInfoHBox.setBorder(new Border(new BorderStroke(Color.rgb(223, 223, 245), Color.rgb(223, 223, 245), null, null, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, null, null, new BorderWidths(1, 1, 0, 0), null)));

        userInfoHBox.getChildren().addAll(userIconImageView, userName);
        return userInfoHBox;
    }

    private void showAddGroupStage_onMouseClick(MouseEvent e) {
        if (e.getButton() == MouseButton.SECONDARY) {
            addPlaylistLabel.setContextMenu(globalMenu);
            globalMenu.show(addPlaylistLabel, Side.RIGHT, 0, 0);
        } else if (e.getButton() == MouseButton.PRIMARY) {
            // Call a function that prevents the main interface from responding to mouse events
            if (beforeDialogShowListener != null) {
                beforeDialogShowListener.run();
            }
            AddPlaylistStage addPlaylistStage = new AddPlaylistStage(primaryStage);
            addPlaylistStage.showAndWait();
            // Determine whether the OK button is successfully pressed, and save the playlist after success
            if (addPlaylistStage.isConfirm()) {
                String playlistName = addPlaylistStage.getPlaylistName();
                playlistService.addPlaylist(playlistName);
                playlistsVBox.getChildren().add(this.createPlaylistHBox(playlistName));
            }
            // Call the function that releases the main interface in response to mouse events
            if (afterDialogShowListener != null) {
                afterDialogShowListener.run();
            }
        }
    }

    Runnable beforeDialogShowListener;
    Runnable afterDialogShowListener;

    public void setBeforeDialogShowListener(Runnable beforeDialogShowListener) {
        this.beforeDialogShowListener = beforeDialogShowListener;
    }
    public void setAfterDialogShowListener(Runnable afterDialogShowListener) {
        this.afterDialogShowListener = afterDialogShowListener;
    }

}
