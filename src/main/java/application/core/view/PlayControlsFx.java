package application.core.view;

import application.core.ControlsListener;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

import static application.core.utils.I18nService.i18nService;

public class PlayControlsFx extends BorderPane {

    private ControlsListener controlsListener;

    private ImageView playPauseImageView;
    private Label albumPictureLabel;
    private Label playLastLabel;
    private Label playLabel;
    private Label playNextLabel;

    private Slider songSlider;
    private ProgressBar songProgressBar;

    private Label musicTitleLabel;
    private Label musicArtistLabel;

    private Label currentTimeLabel;
    private Label totalTimeLabel;

    private Slider volumeSlider;
    private ProgressBar volumeProgressBar;

    BorderPane parent;
    Node left;
    Node center;

    boolean isPlayed = false; // TODO
    double totalDurationSeconds = 100; // TODO: mediaPlayer.getTotalDuration().toSeconds();

    boolean isMute = false;
    double currentVolume = 100;


    static final String sequencePlay = i18nService.getMessage("player.playMode.sequencePlay");
    static final String sequenceRoop = i18nService.getMessage("player.playMode.sequenceRoop");
    static final String singleRoop = i18nService.getMessage("player.playMode.singleRoop");
    static final String randomPlay = i18nService.getMessage("player.playMode.randomPlay");

    private String currentPlayMode = randomPlay;


    public PlayControlsFx(ControlsListener controlsListener) {
        this.controlsListener = controlsListener;

        setId("play-controls");

        createAlbumPictureLabel();
        createPlayLast();
        createPlayLabel();
        createPlayNextLabel();

        HBox playButtonsHBox = createPlayButtonsHBox(albumPictureLabel, playLastLabel, playLabel, playNextLabel);
        BorderPane songInfoAndProgressBarBorderPane = createSongInfoAndProgressBarBorderPane();
        HBox volumeAndOtherControlsHBox = createVolumeAndOtherControlsHBox();

        setPrefHeight(60);

        setLeft(playButtonsHBox);
        setCenter(songInfoAndProgressBarBorderPane);
        setRight(volumeAndOtherControlsHBox);

        setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), null, null)));
        setBorder(new Border(new BorderStroke(Color.rgb(228, 228, 231), null, null, null,
                BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(1, 0, 0, 0), null)));
    }

    private HBox createPlayButtonsHBox(Node...children) {
        HBox playButtonsHBox = new HBox(20);
        playButtonsHBox.setAlignment(Pos.CENTER);
        playButtonsHBox.setPadding(new Insets(0, 29, 0, 0));
//		leftHBox.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        playButtonsHBox.getChildren().addAll(children);
        return playButtonsHBox;
    }

    private void createAlbumPictureLabel() {
        albumPictureLabel = createBtnLabel("", "image/DefaultAlbumWhiteBackground.png", 58, 58, e -> {
            Pane pane = new Pane(new Label("Just a test."));
            pane.setPrefWidth(128);
            pane.setPrefHeight(128);
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(221, 221, 221), null, null)));
            if (e.getButton() == MouseButton.PRIMARY && parent == null) {
                parent = (BorderPane) this.getParent();
                left = parent.getLeft();
                center = parent.getCenter();

                parent.setLeft(null);
                parent.setCenter(pane);
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.5), pane);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                //开始播放渐变动画提示
                fadeTransition.play();
            } else if (e.getButton() == MouseButton.SECONDARY && parent != null) {
                parent.setLeft(left);
                parent.setCenter(center);
                parent = null;
            }
        });
        albumPictureLabel.setBorder(new Border(new BorderStroke(Color.rgb(242, 242, 242), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
    }

    private void createPlayLast() {
        playLastLabel = createBtnLabel("", "image/PlaybackLast.png", 30, 30, e -> {
            if (controlsListener != null) {
                controlsListener.playPreviousListener();
            }
        });
    }

    private void createPlayLabel() {
        playPauseImageView = createImage("image/PlaybackPlay.png", 32, 32);
        playLabel = createBtnLabel("", playPauseImageView, 32, 32, this::playStopListener);
    }

    public final void playStopListener(MouseEvent e) {
        isPlayed = !isPlayed;
        if (isPlayed) {
            playPauseImageView.setImage(new Image("image/PlaybackPause.png"));
            if (controlsListener != null) {
                controlsListener.playButtonListener();
            }
        } else {
            playPauseImageView.setImage(new Image("image/PlaybackPlay.png"));
            if (controlsListener != null) {
                controlsListener.pauseButtonListener();
            }
        }
    }

    private void createPlayNextLabel() {
        playNextLabel = createBtnLabel("", "image/PlaybackNext.png", 30, 30, e -> {
            if (controlsListener != null)
                controlsListener.playNextListener();
        });
    }

    private BorderPane createSongInfoAndProgressBarBorderPane() {
        // Set up monitoring events for the play activity bar
        HBox songTitleAndArtistHBox = createSongTitleAndArtistHBox();
        HBox songCurrentTimeAndTotalTimeHBox = createSongCurrentTimeAndTotalTimeHBox();

        BorderPane songTitleAndTimeBorderPane = new BorderPane();
        songTitleAndTimeBorderPane.setLeft(songTitleAndArtistHBox);
        songTitleAndTimeBorderPane.setRight(songCurrentTimeAndTotalTimeHBox);

        StackPane songProgressBarStackPane = createSongProgressBarStackPane();

        BorderPane songInfoAndProgressBarBorderPane = new BorderPane();
        songSlider.prefWidthProperty().bind(songInfoAndProgressBarBorderPane.widthProperty());
        songProgressBar.prefWidthProperty().bind(songInfoAndProgressBarBorderPane.widthProperty());
        songInfoAndProgressBarBorderPane.setTop(songTitleAndTimeBorderPane);
        songInfoAndProgressBarBorderPane.setCenter(songProgressBarStackPane);

        return songInfoAndProgressBarBorderPane;
    }

    private HBox createSongTitleAndArtistHBox() {
        musicTitleLabel = new Label(i18nService.getMessage("Unknown"));
        musicTitleLabel.setTextFill(Color.rgb(102, 102, 102));
        musicTitleLabel.setMaxWidth(150);
        Label dashLabel = new Label("-");
        dashLabel.setTextFill(Color.rgb(151, 151, 151));
        musicArtistLabel = new Label(i18nService.getMessage("Unknown"));
        musicArtistLabel.setTextFill(Color.rgb(151, 151, 151));
        musicArtistLabel.setMaxWidth(150);

        HBox songTitleAndArtistHBox = new HBox(3);
        songTitleAndArtistHBox.setPadding(new Insets(10, 0, 0, 0));
        songTitleAndArtistHBox.getChildren().addAll(musicTitleLabel, dashLabel, musicArtistLabel);

        return songTitleAndArtistHBox;
    }

    private HBox createSongCurrentTimeAndTotalTimeHBox() {
        // Play time label
        currentTimeLabel = new Label("00:00");
//		labPlayedTime.setPrefHeight(40);
        currentTimeLabel.setFont(new Font("宋体", 9));
        currentTimeLabel.setTextFill(Color.rgb(102, 102, 102));

        // Play time and total time separator
        Label slashLabel = new Label("/");
        slashLabel.setFont(new Font("宋体", 10));
        slashLabel.setTextFill(Color.rgb(151, 151, 151));

        // Total song time label
        totalTimeLabel = new Label("00:00");
        totalTimeLabel.setTextFill(Color.rgb(151, 151, 151));
        totalTimeLabel.setFont(new Font("宋体", 9));
//		labTotalTime.setPrefHeight(40);
//		labTotalTime.setPadding(new Insets(0,10,0,2));

        HBox songCurrentTimeAndTotalTimeHBox = new HBox(3);
        songCurrentTimeAndTotalTimeHBox.setPadding(new Insets(10, 0, 0, 0));
        songCurrentTimeAndTotalTimeHBox.setAlignment(Pos.CENTER_RIGHT);
//		topHBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        songCurrentTimeAndTotalTimeHBox.getChildren().addAll(currentTimeLabel, slashLabel, totalTimeLabel);

        return songCurrentTimeAndTotalTimeHBox;
    }

    private StackPane createSongProgressBarStackPane() {
        // Play progress bar
        songProgressBar = new ProgressBar();
        songProgressBar.setProgress(0);

        // Play slider
        songSlider = new Slider();
        songSlider.setOnMouseReleased(e -> {
            if (isPlayed) {
                if (songSlider.isFocused()) {
                    double playTimeValue = songSlider.getValue();
                    songProgressBar.setProgress(playTimeValue / songSlider.getMax());
                    Duration duration = new Duration(1000 * playTimeValue);
                    if (controlsListener != null) {
                        controlsListener.songPositionSliderChangeListener(duration);;
                    }
                }

            }
        });
        // Change the value of sliderSong to add a listener, set the value of the progressBarSong progress bar
        songSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (isPlayed) {
                    if (songSlider.isPressed() && !songSlider.isValueChanging()) {
                        Duration duration = new Duration(1000 * songSlider.getValue());
                        if(controlsListener != null) {
                            controlsListener.songPositionSliderChangeListener(duration);
                        }
                    }
                    Date date = new Date((int) newValue.doubleValue() * 1000); //乘以一千变成秒数
                    currentTimeLabel.setText(new SimpleDateFormat("mm:ss").format(date));
                    songProgressBar.setProgress(newValue.doubleValue() / totalDurationSeconds);
                    //songProgressBar.setProgress(newValue.doubleValue() / songSlider.getMax());
//                    System.out.println("songSlider value:" + songSlider.getValue() + " max:" + songSlider.getMax() +
//                            " songProgressBar progress:" + songProgressBar.getProgress() +
//                            " totalDurationSeconds:" + totalDurationSeconds +
//                            " songSlider.value / songSlider.max:" + (songSlider.getValue() / songSlider.getMax()));
                }
            }
        });

        // StackPane container package playback slider and playback progress bar
        StackPane songProgressBarStackPane = new StackPane();
        songProgressBarStackPane.getChildren().addAll(songProgressBar, songSlider);
//		stackPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        songProgressBarStackPane.setPadding(new Insets(0, 0, 11, 0));

        return songProgressBarStackPane;
    }

    private HBox createVolumeAndOtherControlsHBox() {
        HBox volumeControlsHBox = createVolumeControls();
        Label playPatternLabel = createPlayPatternLabel();
        Label lyricViewLabel = createLyricViewLabel();

        HBox volumeAndOtherControlsHBox = new HBox(10);
        volumeAndOtherControlsHBox.setAlignment(Pos.CENTER);
//		volumeAndOtherControlsHBox.setPadding(new Insets(0,0,0,10));
//		volumeAndOtherControlsHBox.setBorder(new Border(null,new BorderStroke(Color.rgb(112,112,112) , null, null, null,null, BorderStrokeStyle.SOLID,null, null, null, new BorderWidths(1), null)));
        volumeAndOtherControlsHBox.getChildren().addAll(volumeControlsHBox, playPatternLabel, lyricViewLabel);

        return volumeAndOtherControlsHBox;
    }

    private HBox createVolumeControls() {
        // Volume icon
        ImageView soundImageView = new ImageView("image/SoundVolume.png");
        soundImageView.setFitWidth(19);
        soundImageView.setFitHeight(19);
        Label soundIconLabel = new Label("", soundImageView);
        soundIconLabel.setPrefHeight(19);
        soundIconLabel.setPadding(new Insets(0, 0, 0, 10));
        soundIconLabel.setOnMouseClicked(e -> {
            if (isPlayed) {
                if (isMute) {
                    currentVolume = volumeSlider.getValue();
                    soundImageView.setImage(new Image("image/SoundVolume.png"));
                } else {
                    soundImageView.setImage(new Image("image/SoundVolumeMute.png"));
                }
                isMute = !isMute;
                Double newVolume = controlsListener.isMuteButtonChangeListener(isMute);
                volumeSlider.setValue(newVolume != null ? newVolume : currentVolume);
            }
        });

        // Volume scroll bar
        volumeSlider = new Slider();
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.05);
//		volumeSlider.setMajorTickUnit(0.01);// 每前进一格，增加多少的值
        volumeSlider.setPrefWidth(100);

        // Volume progress bar
        volumeProgressBar = new ProgressBar();
        volumeProgressBar.setProgress(0.05);
        volumeProgressBar.prefWidthProperty().bind(volumeSlider.prefWidthProperty());

        StackPane volumeBarStackPane = new StackPane();
        volumeBarStackPane.getChildren().addAll(volumeProgressBar, volumeSlider);

        // Set the listening event of the volume scroll bar, so that the progress bar always updates with the scroll bar
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                volumeProgressBar.setProgress(newValue.doubleValue());
            }
        });

        // Set the mouse drag and press to monitor events for the volume slider
        volumeSlider.setOnMouseDragged(e -> {
            if (isPlayed) {
                if (volumeSlider.getValue() == 0.0) {
                    soundImageView.setImage(new Image("image/SoundVolumeMute.png"));
                    soundIconLabel.setMouseTransparent(true);
                } else {
                    soundImageView.setImage(new Image("image/SoundVolume.png"));
                    soundIconLabel.setMouseTransparent(false);
                }
                controlsListener.volumeSliderChangeListener(volumeSlider.getValue());
            }
        });
        volumeSlider.setOnMousePressed(e -> {
            if (isPlayed) {
                double volumeValue = volumeSlider.getValue();
                if (volumeValue == 0.0) {
                    soundImageView.setImage(new Image("image/SoundVolumeMute.png"));
                } else {
                    soundImageView.setImage(new Image("image/SoundVolume.png"));
                }
                controlsListener.volumeSliderChangeListener(volumeValue);
            }
        });

        HBox volumeControlsHBox = new HBox(10, soundIconLabel, volumeBarStackPane);
        volumeControlsHBox.setAlignment(Pos.CENTER);

        return volumeControlsHBox;
    }

    private Label createPlayPatternLabel() {
        // Play mode picture
        ImageView playPatternView = new ImageView("image/PlayModeRandom.png");
        playPatternView.setFitWidth(24);
        playPatternView.setFitHeight(24);
        Label playPatternLabel = new Label("", playPatternView);
        playPatternLabel.setPrefHeight(20);

        playPatternLabel.setOnMouseClicked(e -> {
            if (randomPlay.equals(currentPlayMode)) {
                currentPlayMode = sequencePlay;
                playPatternView.setImage(new Image("image/PlayModeSequence.png"));
            } else if (sequencePlay.equals(currentPlayMode)) {
                currentPlayMode = sequenceRoop;
                playPatternView.setImage(new Image("image/PlayModeSequenceRoop.png"));
            } else if (sequenceRoop.equals(currentPlayMode)) {
                currentPlayMode = singleRoop;
                playPatternView.setImage(new Image("image/PlayModeSingleRoop.png"));
            } else if (singleRoop.equals(currentPlayMode)) {
                currentPlayMode = randomPlay;
                playPatternView.setImage(new Image("image/PlayModeRandom.png"));
            }
            if (controlsListener != null) {
                controlsListener.playModeButtonChangeListener(currentPlayMode);
            }
        });

        return playPatternLabel;
    }

    private Label createLyricViewLabel() {
        // Lyrics button image
        ImageView lyricView = new ImageView("image/LyricViewDark.png");
        lyricView.setFitWidth(20);
        lyricView.setFitHeight(20);
        Label lyricViewLabel = new Label("", lyricView);
        lyricViewLabel.setPrefWidth(20);
        lyricViewLabel.setPrefHeight(20);
        lyricViewLabel.setPadding(new Insets(0, 16, 0, 0));

        return lyricViewLabel;
    }

    private ImageView createImage(String imgSrc, int width, int height) {
        ImageView imageView = new ImageView(imgSrc);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return imageView;
    }

    private Label createBtnLabel(String txt, String imgSrc, int width, int height, EventHandler<? super MouseEvent> value) {
        ImageView imageView = createImage(imgSrc, width, height);
        Label label = createBtnLabel(txt, imageView, width, height, value);
        return label;

    }

    private Label createBtnLabel(String txt, ImageView imageView, int width, int height, EventHandler<? super MouseEvent> value) {
        Label label = new Label(txt, imageView);
        label.setPrefWidth(width);
        label.setPrefHeight(height);
        // label.setPadding(new Insets(10,10,10,20));
        label.setOnMouseClicked(value);

        label.getStyleClass().add("btn");
        return label;
    }

    public void setTitleAndArtist(String title, String artist) {
        musicTitleLabel.setText(title);
        musicArtistLabel.setText(artist);
    }

    public void updateSognSlider(Duration newValue, Double totalDurationSeconds) {
        if (!songSlider.isPressed()) {
            this.totalDurationSeconds = totalDurationSeconds;
            songSlider.setValue(newValue.toSeconds());
        }
    }

    public double getVolume() {
        return volumeSlider.getValue();
    }

    public void setUnknown() {
        musicTitleLabel.setText(i18nService.getMessage("Unknown"));
        musicArtistLabel.setText(i18nService.getMessage("Unknown"));
        this.playPauseImageView.setImage(new Image("image/PlaybackPlay.png"));
    }

    public void pause() {
        this.playPauseImageView.setImage(new Image("image/PlaybackPlay.png"));
    }
    public void pause(String title, String artist, double totalSeconds, String totalTime) {
        this.playPauseImageView.setImage(new Image("image/PlaybackPlay.png"));
        this.musicTitleLabel.setText(title);
        this.musicArtistLabel.setText(artist);
        this.songSlider.setMax(totalSeconds);
        this.totalTimeLabel.setText(totalTime);
    }

    public void play(String title, String artist, double totalSeconds, String totalTime) {
        isPlayed = true;
        this.playPauseImageView.setImage(new Image("image/PlaybackPause.png"));
        this.musicTitleLabel.setText(title);
        this.musicArtistLabel.setText(artist);
        this.songSlider.setMax(totalSeconds);
        this.songSlider.setValue(0);
        this.songProgressBar.setProgress(0.0);
        this.totalTimeLabel.setText(totalTime);
    }
    public void stop() {
        isPlayed = false;
        this.playPauseImageView.setImage(new Image("image/PlaybackPlay.png"));
        currentTimeLabel.setText("00:00");
        songSlider.setValue(0);
    }
}
