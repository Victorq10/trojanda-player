package application;

import application.core.playback.MediaPlayerPlayback;
import application.core.view.MediaTableFx;
import application.core.view.PlayControlsFx;
import application.core.view.PlayListsFx;
import application.songs.SongInfo;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static application.core.database.DatabaseService.databaseService;
import static application.core.i18n.I18nService.i18nService;
import static application.songs.SongService.songService;

public class TrojandaApplication extends Application {

    public static Color PRIMARY_HIGHLIGHT_BG_COLOR = Color.rgb(200, 88, 86);
    public static Color PRIMARY_BG_COLOR = Color.rgb(188, 47, 45);
    public static Color PRIMARY_TEXT_COLOR = Color.WHITE;
    public static Color PRIMARY_DISABLED_BG_COLOR = Color.rgb(228, 171, 171);

    public static Color SECONDARY_HIGHLIGHT_BG_COLOR = Color.rgb(255, 0, 0, 0.05);
    public static Color SECONDARY_BG_COLOR = Color.rgb(255, 255, 255);
    public static Color SECONDARY_BORDER_COLOR = Color.rgb(222, 153, 153);
    public static Color SECONDARY_TEXT_COLOR = Color.rgb(200, 88, 86);

    private double primaryStageMinWidth = 858;
    private double primaryStageMinHeight = 570;

    static final String sequencePlay = i18nService.getMessage("player.playMode.sequencePlay");
    static final String sequenceRoop = i18nService.getMessage("player.playMode.sequenceRoop");
    static final String singleRoop = i18nService.getMessage("player.playMode.singleRoop");
    static final String randomPlay = i18nService.getMessage("player.playMode.randomPlay");

    private Stage primaryStage;
    private BorderPane rootBorderPane;
    private StackPane rootStackPane;

    private PlayListsFx musicLibraryFx;
    private MediaTableFx mediaTableFx;
    private PlayControlsFx playControlsFx;

    @Override
    public void init() throws Exception {
        databaseService.initConnection();
        this.play.reloadSongs();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mediaPlayerPlayback.despose();
        databaseService.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        mediaPlayerPlayback.setStartToPlaySongListener(listeners::startToPlaySongListener);
        mediaPlayerPlayback.setOnEndOfMediaListener(listeners::onEndOfMediaListener);
        mediaPlayerPlayback.setOnCurrentTimeChangeListener(listeners.currentTimePropertyChangeListener);

        // The bottom borderPane of the main stage (main interface)
        rootBorderPane = new BorderPane();
        rootBorderPane.setBackground(new Background(new BackgroundFill(Color.rgb(250, 250, 252), null, null)));
        rootBorderPane.setLeft(getLeftPane_PlayListsFx());
        rootBorderPane.setCenter(getCenterPane_MediaTableFx());
//        SplitPane splitPane = new SplitPane(getLeftPane(), getCenterPane());
//        rootBorderPane.setCenter(splitPane);
        rootBorderPane.setBottom(getBottomPane_PlayControlsFx());
        rootBorderPane.setBorder(new Border(new BorderStroke(Color.rgb(110, 110, 111), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        // StackPane всієї основної сцени (основного інтерфейсу) розміщується внизу. Інформаційна підказка для
        // перемикання режиму відтворення може бути динамічно додана у верхню частину stageStackPane, а підказкова
        // інформація про режим відтворення у верхній частині може бути видалена після завершення відображення.
        rootStackPane = new StackPane();
        rootStackPane.getChildren().addAll(rootBorderPane);

        Scene scene = new Scene(rootStackPane, primaryStageMinWidth, primaryStageMinHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - primaryStage.getWidth()) / 2.0);
        primaryStage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - primaryStage.getHeight()) / 2.0);
        primaryStage.setTitle(i18nService.getMessage("application.title"));
        primaryStage.getIcons().add(new Image("image/mallow32.png"));
    }

    /**
     * Create a panel on the left that displays information and playlists.
     *
     * @return
     */
    private BorderPane getLeftPane_PlayListsFx() {
        musicLibraryFx = new PlayListsFx(primaryStage);
        musicLibraryFx.setBeforeDialogShowListener(listeners::beforeDialogShowListener);
        musicLibraryFx.setAfterDialogShowListener(listeners::afterDialogShowListener);
        return musicLibraryFx;
    }

    private BorderPane getCenterPane_MediaTableFx() {
        //createLyricPane();
        mediaTableFx = new MediaTableFx(primaryStage);
        mediaTableFx.setSongSelectedListener(listeners::songSelectedListener);
        mediaTableFx.setSongSelectedOneClickListener(listeners::songSelectedOneClickListener);
        mediaTableFx.setBeforeDialogShowListener(listeners::beforeDialogShowListener);
        mediaTableFx.setAfterDialogShowListener(listeners::afterDialogShowListener);
        mediaTableFx.setPreferencesCloseListener(listeners::preferencesClosedListener);

//        if (play.songsList != null && play.songsList.size() > 0) {
            mediaTableFx.setSongsList(play.songsList);
//        }
        return mediaTableFx;
    }

    /**
     * Create the lower playback control panel, including previous song, pause, next song, playback time display,
     * progress bar display, etc.
     */
    private PlayControlsFx getBottomPane_PlayControlsFx() {
        playControlsFx = new PlayControlsFx();
        playControlsFx.setPlayPreviousListener(listeners::playPreviousListener);
        playControlsFx.setPlayListener(listeners::playButtonListener);
        playControlsFx.setPauseListener(listeners::pauseButtonListener);
        playControlsFx.setPlayNextListener(listeners::playNextListener);
        playControlsFx.songPositionChangeListener(listeners::songPositionSliderChangeListener);
        playControlsFx.setMuteChangeListener(listeners::isMuteButtonChangeListener);
        playControlsFx.setVolumeChangeListemer(listeners::volumeSliderChangeListener);
        playControlsFx.setChangePlayModeListener(listeners::playModeButtonChangeListener);
        return playControlsFx;
    }

    private Listeners listeners = new Listeners();
    public class Listeners {
        private void preferencesClosedListener() {
            //First need to deal with the mediaPlayer player object, release resources
            mediaPlayerPlayback.despose();
            //Set the name of the song and the singer to be unknown, and the button for playing pause is the pause button
            playControlsFx.setUnknown();
            play.songsList.clear();
            Thread run = new Thread(() -> {
                songService.scanMusicLibrary();
                play.reloadSongs();
                //Set the content of the song table and sort by the song title column
                //mediaTableFx.setSongsList(play.songsList);
            });
            run.start();
        }

        private void songSelectedListener(SongInfo songInfo) {
            int index = play.songsList.indexOf(songInfo);
            if (index == -1) {
                System.out.println("Cannot find song " + songInfo);
                return;
            }
            play.startToPlay(songInfo);
        }

        private void songSelectedOneClickListener(SongInfo songInfo) {
            // TODO: song just selectd in the playlist
            System.out.println("TODO: song just selectd in the playlist");
        }

        private void playPreviousListener() {
            play.startToPlay(-1);
        }

        private final void playButtonListener() {
            if (!mediaPlayerPlayback.play()) {
                play.startToPlay(0);
            }
        }

        private final void playNextListener() {
            play.startToPlay(1);
        }

        private final void pauseButtonListener() {
            mediaPlayerPlayback.pause();
        }

        private void songPositionSliderChangeListener(Duration duration) {
            mediaPlayerPlayback.seek(duration);
        }

        private Double isMuteButtonChangeListener(Boolean isMute) {
            return mediaPlayerPlayback.setMute(isMute);
        }

        private void volumeSliderChangeListener(Double volume) {
            mediaPlayerPlayback.setVolume(volume);
        }

        private void playModeButtonChangeListener(String playModeString) {
            play.currentPlayMode = playModeString;
            Label playModeLabel = new Label(playModeString);
            ToastPlayModeInfo(playModeLabel);
        }

        /**
         * Prevent the main stage borderPane from responding to mouse events and changing the opacity function.
         * blockBorderPane
         */
        private void beforeDialogShowListener() {
            // Set the main stage interface borderPane except for the titleBar part at the top, other parts do not respond to mouse events
            rootBorderPane.getLeft().setMouseTransparent(true);
            rootBorderPane.getCenter().setMouseTransparent(true);
            rootBorderPane.getBottom().setMouseTransparent(true);
            // By the way, set the opaque color for easy reminder
            rootBorderPane.getLeft().setOpacity(0.4);
            rootBorderPane.getCenter().setOpacity(0.4);
            rootBorderPane.getBottom().setOpacity(0.4);
        }

        /**
         * The function that releases the main stage borderPane in response to mouse events and the opacity
         * becomes the default value.
         * releaseBorderPane
         */
        private void afterDialogShowListener() {
            rootBorderPane.getLeft().setMouseTransparent(false);
            rootBorderPane.getCenter().setMouseTransparent(false);
            rootBorderPane.getBottom().setMouseTransparent(false);
            rootBorderPane.getLeft().setOpacity(1);
            rootBorderPane.getCenter().setOpacity(1);
            rootBorderPane.getBottom().setOpacity(1);
        }

        private void startToPlaySongListener(SongInfo song) {
            mediaTableFx.autoScrollToSongHandler(song);
            playControlsFx.play(song.getMusicName(), song.getSinger(), song.getTotalSeconds(), song.getTotalTime());
            play.updateSongOnStartToPlay(song);
        }

        private void onEndOfMediaListener() {
            songService.updateLastPlayedAndPlayCount(play.currentSong);
            play.updateLastPlayedSongsList();

            SongInfo songInfo = play.getAutoplayNextSong();
            if (songInfo == null) {
                mediaPlayerPlayback.seek(new Duration(0));
                playControlsFx.pause();
            } else if (songInfo.equals(play.currentSong)) {
                mediaPlayerPlayback.seek(new Duration(0));
                mediaPlayerPlayback.play();
            } else {
                play.startToPlay(songInfo);
            }
        }

        public void currentTimePropertyChangeListener(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
            Double totalDurationInSeconds = mediaPlayerPlayback.getTotalDurationInSeconds();
            if (totalDurationInSeconds != null) {
                playControlsFx.updateSognSlider(newValue, totalDurationInSeconds);
            }
        }

        ChangeListener currentTimePropertyChangeListener = new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                currentTimePropertyChangeListener(observable, oldValue, newValue);
            }
        };
    }

    private Play play = new Play();
    private MediaPlayerPlayback mediaPlayerPlayback = new MediaPlayerPlayback();

    public class Play {
        private void updateSongOnStartToPlay(SongInfo song) {
            if (currentSong != null) {
                currentSong.setNowPlaying("");
            }
            currentSong = song;
            if (currentSong != null) {
                currentSong.setNowPlaying("▶");
            }
        }

        private void updateLastPlayedSongsList() {
            lastPlayedSongsList.addLast(currentSong);
            while (lastPlayedSongsList.size() > 50) {
                lastPlayedSongsList.removeFirst();
            }
        }

        private void reloadSongs() {
            Platform.runLater(() -> {
                //If the size of the song collection is greater than 0, clear the collection
//                if (play.songsList != null/* && play.songsList.size() > 0*/) {
                songsList.clear();
//                }
                songsList.addAll(songService.getAllSongFiles());
            });
        }

        /**
         * The function of music playback, and after mediaPlayer playback is over, the current playback mode is judged,
         * and the next song is played
         */
        private void startToPlay(int step) {
            int index = play.determineNextPlayIndex(step);
            SongInfo song = getSongInfoByIdx(index);
            startToPlay(song);
        }

        private void startToPlay(SongInfo song) {
            if (!mediaPlayerPlayback.autoPlay(song, playControlsFx.getVolume())) {
                // TODO: check if we need to update UI play controls and currentSong in the TableView
                System.out.println("// TODO: check if we need to update UI play controls and currentSong in the TableView");
            }
        }


        private ObservableList<SongInfo> songsList = FXCollections.observableArrayList();
        private Deque<SongInfo> lastPlayedSongsList = new LinkedList<>();
        private List<SongInfo> randomSongsList = new ArrayList<>();

        private String currentPlayMode = randomPlay;
        private SongInfo currentSong;

        private int determineNextPlayIndex(int step) {
            if (songsList.isEmpty()) {
                return -1;
            }
            int currentIndex = getCurrentIndex();
            if (currentIndex == -1) { // Start from the beginning
                currentIndex = 0;
                if (randomPlay.equals(currentPlayMode)) {
                    checkRandomListSize();
                }
                return currentIndex;
            }
            currentIndex += step;
            if (singleRoop.equals(currentPlayMode)
                    || sequencePlay.equals(currentPlayMode)
                    || sequenceRoop.equals(currentPlayMode)) {
                if (currentIndex < 0) {
                    currentIndex = songsList.size() - 1;
                } else if (currentIndex >= songsList.size()) {
                    currentIndex = 0;
                }
                return currentIndex;
            } else if (randomPlay.equals(currentPlayMode)) {
                checkRandomListSize();
                if (currentIndex < 0) {
                    currentIndex = songsList.size() - 1;
                } else if (currentIndex >= songsList.size()) {
                    currentIndex = 0;
                }
                return currentIndex;
            }

            throw new RuntimeException("Unkown playmode [" + currentPlayMode + "]");
        }


        /**
         * find next song to play using playMode
         *
         * @return
         */
        private SongInfo getAutoplayNextSong() {
            boolean hasCurrentSongInPlayList = hasCurrentSongInPlayList();
            if (singleRoop.equals(currentPlayMode)) {
                return hasCurrentSongInPlayList ? currentSong : null;
            } else if (sequencePlay.equals(currentPlayMode)) {
                if (isLastSongInList()) {
                    return null;
                }
                return getSongInfoByIdx(getCurrentIndex() + 1);
            } else if (sequenceRoop.equals(currentPlayMode)) {
                if (isLastSongInList() && songsList.size() > 0) {
                    return songsList.get(0);
                }
                return getSongInfoByIdx(getCurrentIndex() + 1);
            } else if (randomPlay.equals(currentPlayMode)) {
                checkRandomListSize();
                if (isLastSongInList()) {
                    if (randomSongsList.size() > 0) {
                        return randomSongsList.get(0);
                    }
                } else {
                    int nextIndex = getCurrentIndex() + 1;
                    return randomSongsList.get(nextIndex + 1);
                }
            }
            return null;
        }

        private int getCurrentIndex() {
            if (currentSong == null) {
                return -1;
            }
            if (randomPlay.equals(currentPlayMode)) {
                return randomSongsList.indexOf(currentSong);
            }
            return songsList.indexOf(currentSong);
        }

        private boolean isLastSongInList() {
            return getCurrentIndex() == songsList.size() - 1;
        }

        private boolean hasCurrentSongInPlayList() {
            if (currentSong == null) {
                return false;
            }
            if (songsList.indexOf(currentSong) > -1) {
                return true;
            }
            return false;
        }

        private void checkRandomListSize() {
            if (randomPlay.equals(currentPlayMode)) {
                if (randomSongsList.size() != songsList.size() || !songsList.containsAll(randomSongsList)) {
                    Random random = new Random();
                    List<SongInfo> newRandomList = new ArrayList<>(songsList.size());
                    List<SongInfo> tmpList = new ArrayList<>(songsList);
                    while (!tmpList.isEmpty()) {
                        newRandomList.add(tmpList.remove(random.nextInt(tmpList.size())));

                    }
                    randomSongsList.clear();
                    randomSongsList = newRandomList;
                }
            }
        }

        private SongInfo getSongInfoByIdx(int index) {
            if (index > -1 && index < songsList.size()) {
                if (randomPlay.equals(currentPlayMode)) {
                    checkRandomListSize();
                    return randomSongsList.get(index);
                }
                return songsList.get(index);
            }
            return null;
        }
    }

    // Pop-up mode switching prompt animation function
    private void ToastPlayModeInfo(Label fadingShowPlayMode) {
        fadingShowPlayMode.getStylesheets().add("css/FadingLabelStyle.css");
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.5), fadingShowPlayMode);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        rootStackPane.getChildren().add(fadingShowPlayMode);
        // Remove the label component after the animation is complete
        fadeTransition.setOnFinished(fade -> {
            rootStackPane.getChildren().remove(1);
        });
        // Start playing gradual animation prompt
        fadeTransition.play();
    }

}


