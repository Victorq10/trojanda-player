package application.ui;

import application.dto.SongInfo;
import application.services.impl.DefaultI18nService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Consumer;


public class MediaTableFx extends BorderPane {
    private final DefaultI18nService i18nService = DefaultI18nService.INSTANCE;

    private Stage primaryStage;
    private TableColumn<SongInfo, String> musicName;
    public TableView<SongInfo> songTableView;
    private Timeline timeline;

    public MediaTableFx(Stage primaryStage) {
        this.primaryStage = primaryStage;

        //createLyricPane();
        
        musicName = createTableColumn("musicName", 200, i18nService.getMessage("table.column.SongName"));
        musicName.setSortType(TableColumn.SortType.ASCENDING);
        musicName.getStyleClass().add("musicName"); // Add the class name to this column
        
        TableColumn<SongInfo, String> singer = createTableColumn("singer", 100, i18nService.getMessage("table.column.Artist"));
        TableColumn<SongInfo, String> album = createTableColumn("album", 90, i18nService.getMessage("table.column.Album"));
        TableColumn<SongInfo, String> totalTime = createTableColumn("totalTime", 100, i18nService.getMessage("table.column.TotalTime"));
        TableColumn<SongInfo, String> size = createTableColumn("size", 80, i18nService.getMessage("table.column.Size"));
        size.setMinWidth(80);

        songTableView = new TableView<>();
        songTableView.getStylesheets().add("css/TableViewStyle.css");
        songTableView.getColumns().addAll(musicName, singer, album, totalTime, size);
        songTableView.columnResizePolicyProperty().setValue(TableView.CONSTRAINED_RESIZE_POLICY);
        songTableView.setRowFactory(tv -> {
            TableRow<SongInfo> row = new TableRow<>();
            row.setOnMouseClicked(this::rowOnMouseClickListener);
            //表格的鼠标进入、退出事件
            row.setOnMouseEntered(this::rowOnMouseEnteredListener);
            row.setOnMouseExited(this::rowOnMouseExitedListener);
            return row;
        });

        // "Select Directory"
        BorderPane borderPaneChooseFolder = new BorderPane();
        Label lab = new Label(i18nService.getMessage("LocalMusic"));
        lab.setAlignment(Pos.CENTER);
        lab.setPrefHeight(30);
        lab.setTextFill(Color.rgb(102, 102, 102));
        lab.setPadding(new Insets(20, 0, 0, 25));

        ImageView chooseFolderIcon = new ImageView("image/ChooseFolderIcon.png");
        Label choose = new Label(i18nService.getMessage("choseFolder"));
        choose.setTextFill(Color.rgb(26, 90, 153));
        HBox preferencesHBox = new HBox();
        preferencesHBox.setAlignment(Pos.CENTER);
        preferencesHBox.getChildren().addAll(chooseFolderIcon, choose);
        preferencesHBox.setPadding(new Insets(20, 25, 0, 0));
        //"Select Directory" monitoring event
        preferencesHBox.setOnMouseClicked(this::showPreferencesStage_onMouseClick);

        borderPaneChooseFolder.setLeft(lab);
        borderPaneChooseFolder.setPrefHeight(30);
        borderPaneChooseFolder.setRight(preferencesHBox);


        BorderPane borderPaneCenter = new BorderPane();
        borderPaneCenter.setTop(borderPaneChooseFolder);
        borderPaneCenter.setCenter(songTableView);
        borderPaneCenter.setPadding(new Insets(0));

//		this.setTop(scrollpane);
        this.setCenter(borderPaneCenter);
    }
    
    private TableRow<SongInfo> selectedRow = null;
    private void rowOnMouseClickListener(MouseEvent mouseEvent) {
        if (selectedRow != null) {
            //selectedRow.setBackground(selectedRow.getIndex() % 2 == 0 ? evenRowBackground : oddRowBackground);
        }
        selectedRow = (TableRow<SongInfo>) mouseEvent.getSource();
        //selectedRow.setBackground(selectedRowBackground);
        // Double-click the left mouse button to execute playback
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (songSelectedListener != null) {
                songSelectedListener.accept(selectedRow.getItem());
            }
        }
        
    }

    private Background oddRowBackground = new Background(new BackgroundFill(Color.web("#f4f4f6"), null, null));
    private Background evenRowBackground = new Background(new BackgroundFill(Color.web("#fafafc"), null, null));
    private Background hoverRowBackground = new Background(new BackgroundFill(Color.web("#dbdbdb"), null, null));
    private Background selectedRowBackground = new Background(new BackgroundFill(Color.web("#e95420"), null, null));

    private void rowOnMouseEnteredListener(MouseEvent mouseEvent) {
        TableRow<SongInfo> row = (TableRow<SongInfo>) mouseEvent.getSource();
        row.setBackground(row.isSelected() ? selectedRowBackground : hoverRowBackground);
    }

    private void rowOnMouseExitedListener(MouseEvent mouseEvent) {
        TableRow<SongInfo> row = (TableRow<SongInfo>) mouseEvent.getSource();
        row.setBackground(row.isSelected() ? selectedRowBackground 
                : row.getIndex() % 2 != 0 ? oddRowBackground : evenRowBackground);
        // or
        //row.setBackground(row.isSelected() ? selectedRowBackground : null);
    }

    private TableColumn<SongInfo, String> createTableColumn(String property, int width, String message) {
        TableColumn column = new TableColumn<>(message);
        column.setPrefWidth(width);
        //column.setMaxWidth(width);
        column.setMinWidth(80);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private void showPreferencesStage_onMouseClick(MouseEvent e) {
        // Call a function that prevents the main interface from responding to mouse events
        if (beforeDialogShowListener != null) {
            beforeDialogShowListener.run();
        }
        PreferencesStage preferencesStage = new PreferencesStage(primaryStage);
        preferencesStage.showAndWait();
        // When the OK button is pressed, read the path where the ChoseFolder.xml file is saved, and scan the music files below the path
        if (preferencesStage.isConfirm()) {
            if (preferencesCloseListener != null) {
                preferencesCloseListener.run();
            }
        }
        // Call the function that releases the main interface in response to mouse events
        if (afterDialogShowListener != null) {
            afterDialogShowListener.run();
        }
        
    }

    private void createLyricPane() {
        // Define a circle
        Circle circle = new Circle();
        circle.setCenterX(75);
        circle.setCenterY(75);
        circle.setRadius(75);// 圆的半径
        ImageView albumImageView = new ImageView("image/DefaultAlbum.png");
        albumImageView.setFitHeight(150);
        albumImageView.setFitWidth(150);
        albumImageView.setClip(circle);

        Label albumLabel = new Label("", albumImageView);
        albumLabel.setLayoutX(25);
        albumLabel.setLayoutY(25);
        // Визначте анімацію "шкали часу"
        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(new Duration(1000 * 10), new KeyValue(albumImageView.rotateProperty(), 360))
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Infinite loop
        //timeline.play();

        VBox lyricVBox = new VBox(15);
        lyricVBox.setPadding(new Insets(20, 20, 20, 20));
        lyricVBox.setLayoutX(250);
        lyricVBox.setLayoutY(0);
        lyricVBox.setPrefHeight(200);
        lyricVBox.setPrefWidth(200);
        lyricVBox.setBorder(
                new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        // Розмиття фону
        ImageView albumBackground = createBackgroundImageView(new Image("image/DefaultAlbum.png"));

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(albumBackground, albumLabel, lyricVBox);

        ScrollPane scrollpane = new ScrollPane();
        scrollpane.setContent(anchorPane);
        scrollpane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setPrefHeight(200);
        scrollpane.setPadding(new Insets(0, 10, 0, 10));
        albumBackground.fitWidthProperty().bind(scrollpane.widthProperty());
        albumBackground.fitHeightProperty().bind(scrollpane.heightProperty());
    }
    
    private ImageView createBackgroundImageView(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color pixelColor = pixelReader.getColor(i, j);
                for (int n = 0; n < 4; n++) { // 四次颜色淡化
                    pixelColor = pixelColor.darker();
                }
                pixelWriter.setColor(i, j, pixelColor);
            }
        }
        ImageView backgroundImageView =  new ImageView(writableImage);
        backgroundImageView.setX(0);
        backgroundImageView.setY(0);
        GaussianBlur gasussian = new GaussianBlur();
        gasussian.setRadius(20);
        backgroundImageView.setEffect(gasussian);
        return backgroundImageView;
    }

    public void autoScrollToSongHandler(final SongInfo currentSong) {
        Platform.runLater(() -> {
            songTableView.requestFocus();
            songTableView.getSelectionModel().select(currentSong);
            //tableView.getFocusModel().focus(0);
            //tableView.scrollTo(currentSong);

            int index = songTableView.getItems().indexOf(currentSong);
            TableViewSkin<SongInfo> skin = (TableViewSkin<SongInfo>) songTableView.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) skin.getChildren().get(1);
            int first = vf.getFirstVisibleCell().getIndex();
            int last = vf.getLastVisibleCell().getIndex();
            if (index < first || index > last) {
                int scrollToIndex = (index - ((last - first) / 2));
                System.out.println("visible first: " + first + " last:" + last + " =  " + (last - first) +
                        " index:" + index + " scrollTo:" + scrollToIndex);
                if (scrollToIndex < 0 && songTableView.getItems().size() > 0) {
                    songTableView.scrollTo(0);
                } else if (scrollToIndex >= 0 && scrollToIndex < songTableView.getItems().size()) {
                    songTableView.scrollTo(scrollToIndex);
                }
            } else {
                System.out.println("Skip auto scroll to current song");
            }
        });
    }

    public void setSongsList(ObservableList<SongInfo> songsInfo) {
        songTableView.setItems(songsInfo);
        if (!songTableView.getSortOrder().contains(musicName)) {
            songTableView.getSortOrder().add(musicName);
        }
        songTableView.sort();

    } 
    
    Consumer<SongInfo> songSelectedListener;
    Consumer<SongInfo> songSelectedOneClickListener;
    Runnable beforeDialogShowListener;
    Runnable afterDialogShowListener;
    Runnable preferencesCloseListener;

    public void setSongSelectedListener(Consumer<SongInfo> songSelectedListener) {
        this.songSelectedListener = songSelectedListener;
    }
    public void setSongSelectedOneClickListener(Consumer<SongInfo> songSelectedOneClickListener) {
        this.songSelectedOneClickListener = songSelectedOneClickListener;
    }
    public void setBeforeDialogShowListener(Runnable beforeDialogShowListener) {
        this.beforeDialogShowListener = beforeDialogShowListener;
    }
    public void setAfterDialogShowListener(Runnable afterDialogShowListener) {
        this.afterDialogShowListener = afterDialogShowListener;
    }
    public void setPreferencesCloseListener(Runnable preferencesCloseListener) {
        this.preferencesCloseListener = preferencesCloseListener;
    }
}
