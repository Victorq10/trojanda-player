package application;

import application.core.ControlsListener;
import application.core.PlaybackListener;
import application.core.UIListener;
import application.core.playback.MediaPlayerPlayback;
import application.core.view.MediaTableFx;
import application.core.view.PlayControlsFx;
import application.core.view.PlaylistsFx;
import application.core.songs.SongInfo;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
import static application.core.songs.SongService.songService;

public class TrojandaApplication extends Application {

    public static TrojandaApplication Application;

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
    public BorderPane rootBorderPane;
    private StackPane rootStackPane;

    private PlaylistsFx playlistsFx;
    public MediaTableFx mediaTableFx;
    public PlayControlsFx playControlsFx;

    @Override
    public void init() throws Exception {
        TrojandaApplication.Application = this;
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
        mediaPlayerPlayback.setPlaybackListener(new PlaybackListener());

        playlistsFx = createPlaylistsFx();
        mediaTableFx = createMediaTableFx();
        playControlsFx = new PlayControlsFx(new ControlsListener());

        // The bottom borderPane of the main stage (main interface)
        rootBorderPane = new BorderPane();
        rootBorderPane.setBackground(new Background(new BackgroundFill(Color.rgb(250, 250, 252), null, null)));
        rootBorderPane.setLeft(playlistsFx);
        rootBorderPane.setCenter(mediaTableFx);
//        SplitPane splitPane = new SplitPane(getLeftPane(), getCenterPane());
//        rootBorderPane.setCenter(splitPane);
        rootBorderPane.setBottom(playControlsFx);
        rootBorderPane.setBorder(new Border(new BorderStroke(Color.rgb(110, 110, 111), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        // StackPane всієї основної сцени (основного інтерфейсу) розміщується внизу. Інформаційна підказка для
        // перемикання режиму відтворення може бути динамічно додана у верхню частину stageStackPane, а підказкова
        // інформація про режим відтворення у верхній частині може бути видалена після завершення відображення.
        rootStackPane = new StackPane();
        rootStackPane.getStylesheets().add("css/LabelScaleStyle.css");
        rootStackPane.getStylesheets().add("css/TableViewStyle.css");
        rootStackPane.getStylesheets().add("css/SliderAndProgressBar.css");
        rootStackPane.getStylesheets().add("css/ScrollPane.css");
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
    private PlaylistsFx createPlaylistsFx() {
        PlaylistsFx playlistsFx = new PlaylistsFx(primaryStage);
        playlistsFx.setUIListener(new UIListener());
        return playlistsFx;
    }

    private MediaTableFx createMediaTableFx() {
        //createLyricPane();
        MediaTableFx mediaTableFx = new MediaTableFx(primaryStage);
        mediaTableFx.setUIListener(new UIListener());
        mediaTableFx.setSongsList(play.songsList);
        return mediaTableFx;
    }

    public Play play = new Play();
    public MediaPlayerPlayback mediaPlayerPlayback = new MediaPlayerPlayback();

    public class Play {
        public void updateSongOnStartToPlay(SongInfo song) {
            if (currentSong != null) {
                currentSong.setNowPlaying("");
            }
            currentSong = song;
            if (currentSong != null) {
                currentSong.setNowPlaying("▶");
            }
        }

        public void updateLastPlayedSongsList() {
            lastPlayedSongsList.addLast(currentSong);
            while (lastPlayedSongsList.size() > 50) {
                lastPlayedSongsList.removeFirst();
            }
        }

        public void reloadSongs() {
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
        public void startToPlay(int step) {
            int index = play.determineNextPlayIndex(step);
            SongInfo song = getSongInfoByIdx(index);
            startToPlay(song);
        }

        public void startToPlay(SongInfo song) {
            if (!mediaPlayerPlayback.autoPlay(song, playControlsFx.getVolume())) {
                // TODO: check if we need to update UI play controls and currentSong in the TableView
                System.out.println("// TODO: check if we need to update UI play controls and currentSong in the TableView");
            }
        }


        public ObservableList<SongInfo> songsList = FXCollections.observableArrayList();
        private Deque<SongInfo> lastPlayedSongsList = new LinkedList<>();
        private List<SongInfo> randomSongsList = new ArrayList<>();

        public String currentPlayMode = randomPlay;
        public SongInfo currentSong;

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
        public SongInfo getAutoplayNextSong() {
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
    public void ToastPlayModeInfo(Label fadingShowPlayMode) {
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
