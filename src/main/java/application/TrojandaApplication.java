package application;

import application.dto.SongInfo;
import application.services.impl.DefaultConfigurationService;
import application.services.impl.DefaultDatabaseService;
import application.services.impl.DefaultI18nService;
import application.services.impl.DefaultSongService;
import application.ui.MediaTableFx;
import application.ui.PlayControlsFx;
import application.ui.PlayListsFx;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class TrojandaApplication extends Application {

    public static DefaultConfigurationService configurationService;
    //public static DefaultDatabaseService databaseService;
    
    private final DefaultI18nService i18nService = DefaultI18nService.INSTANCE;
    private final DefaultSongService songService = DefaultSongService.INSTANCE;

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

    static final String sequencePlay = DefaultI18nService.INSTANCE.getMessage("player.playMode.sequencePlay");
    static final String sequenceRoop = DefaultI18nService.INSTANCE.getMessage("player.playMode.sequenceRoop");
    static final String singleRoop = DefaultI18nService.INSTANCE.getMessage("player.playMode.singleRoop");
    static final String randomPlay = DefaultI18nService.INSTANCE.getMessage("player.playMode.randomPlay");

    private Stage primaryStage;
    private BorderPane rootBorderPane;
    private StackPane rootStackPane;

    private PlayListsFx musicLibraryFx;
    private MediaTableFx mediaTableFx;
    private PlayControlsFx playControlsFx;

    @Override
    public void init() throws Exception {
        DefaultDatabaseService.databaseService = new DefaultDatabaseService();
        configurationService = DefaultConfigurationService.INSTANCE;
        this.play.songsList = FXCollections.observableArrayList(songService.getAllSongFiles());
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        play.mediaDestroy();
        if (DefaultDatabaseService.databaseService != null) {
            DefaultDatabaseService.databaseService.stop();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        play.setAutoPlaySongListener(listeners::autoPlaySongListener);
        play.setOnEndOfMediaListener(listeners::onEndOfMediaListener);
        
        // The bottom borderPane of the main stage (main interface)
        rootBorderPane = new BorderPane();
        rootBorderPane.setBackground(new Background(new BackgroundFill(Color.rgb(250, 250, 252), null, null)));
        rootBorderPane.setLeft(getLeftPane());
        rootBorderPane.setCenter(getCenterPane());
        rootBorderPane.setBottom(getBottomPane());
        rootBorderPane.setBorder(new Border(new BorderStroke(Color.rgb(110, 110, 111), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        // StackPane всієї основної сцени (основного інтерфейсу) розміщується внизу. Інформаційна підказка для 
        // перемикання режиму відтворення може бути динамічно додана у верхню частину stageStackPane, а підказкова 
        // інформація про режим відтворення у верхній частині може бути видалена після завершення відображення.
        rootStackPane = new StackPane();
        rootStackPane.getChildren().addAll(rootBorderPane);

        Scene scene = new Scene(rootStackPane, primaryStageMinWidth, primaryStageMinHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
        // primaryStage.centerOnScreen();
        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2.0);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2.0);
        primaryStage.setTitle(i18nService.getMessage("application.title")); // "音乐"
        primaryStage.getIcons().add(new Image("image/ApplicationIcon.png"));// 设置任务栏图标

        primaryStage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Make sure that after the window is minimized in the maximized state, the size of the screen occupied 
                // when the taskbar icon is clicked is a visual full screen
                if (primaryStage.isMaximized()) {
                    primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                    primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                }
            }
        });
    }

    /** 
     * Create a panel on the left that displays information and playlists.
     * @return
     */
    private BorderPane getLeftPane() {
        musicLibraryFx = new PlayListsFx(primaryStage);
        musicLibraryFx.setBeforeDialogShowListener(listeners::beforeDialogShowListener);
        musicLibraryFx.setAfterDialogShowListener(listeners::afterDialogShowListener);
        return musicLibraryFx;
    }

    private BorderPane getCenterPane() {
        //createLyricPane();
        mediaTableFx = new MediaTableFx(primaryStage);
        mediaTableFx.setSongSelectedListener(listeners::songSelectedListener);
        mediaTableFx.setSongSelectedOneClickListener(listeners::songSelectedOneClickListener);
        mediaTableFx.setBeforeDialogShowListener(listeners::beforeDialogShowListener);
        mediaTableFx.setAfterDialogShowListener(listeners::afterDialogShowListener);
        mediaTableFx.setPreferencesCloseListener(listeners::preferencesClosedListener);

        if (play.songsList != null && play.songsList.size() > 0) {
            mediaTableFx.setSongsList(play.songsList);
        }
        return mediaTableFx;
    }

    /** 
     * Create the lower playback control panel, including previous song, pause, next song, playback time display,
     * progress bar display, etc.
     */
    private PlayControlsFx getBottomPane() {
        playControlsFx = new PlayControlsFx();
        playControlsFx.setPlayPreviousListener(listeners::playPreviousListener);
        playControlsFx.setPlayListener(listeners::playListener);
        playControlsFx.setPauseListener(listeners::pauseListener);
        playControlsFx.setPlayNextListener(listeners::playNextListener);
        playControlsFx.songPositionChangeListener(listeners::songPositionChangeListener);
        playControlsFx.setMuteChangeListener(listeners::isMuteChangeListener);
        playControlsFx.setVolumeChangeListemer(listeners::volumeChangeListener);
        playControlsFx.setChangePlayModeListener(listeners::changePlayModeListener);
        return playControlsFx;
    }

    private Listeners listeners = new Listeners();
    class Listeners {
        private void preferencesClosedListener() {
            //First need to deal with the mediaPlayer player object, release resources
            if (play.mediaPlayer != null) {
                play.mediaDestroy();
                //Set the name of the song and the singer to be unknown, and the button for playing pause is the pause button
                playControlsFx.setUnknown();
            }
            //If the size of the song collection is greater than 0, clear the collection
            if (play.songsList != null && play.songsList.size() > 0) {
                play.songsList.clear();
            }
            songService.scanMusicLibrary();
            play.songsList = FXCollections.observableArrayList(songService.getAllSongFiles());
            //Set the content of the song table and sort by the song title column
            mediaTableFx.setSongsList(play.songsList);

        }

        private void songSelectedListener(SongInfo songInfo) {
            int index = play.songsList.indexOf(songInfo);
            if (index == -1) {
                System.out.println("Cannot find song " + songInfo);
                return;
            }
            play.mediaAutoPlay(songInfo);
        }

        private void songSelectedOneClickListener(SongInfo songInfo) {
            // TODO: song just selectd in the playlist
            System.out.println("TODO: song just selectd in the playlist");
        }

        private void playPreviousListener() {
            int index = play.determineNextPlayIndex(-1);
            play.mediaAutoPlay(index);
        }

        private final void playListener() {
            if (play.mediaPlayer != null && (play.mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED
                    || play.mediaPlayer.getStatus() == MediaPlayer.Status.READY)) {
                play.mediaPlayer.play();
            } else {
                int index = play.determineNextPlayIndex(0);
                play.mediaAutoPlay(index);
            }
        }

        private final void playNextListener() {
            int index = play.determineNextPlayIndex(1);
            play.mediaAutoPlay(index);
        }

        private final void pauseListener() {
            play.mediaPlayer.pause();
        }

        private void songPositionChangeListener(Duration duration) {
            if (play.mediaPlayer != null) {
                play.mediaPlayer.seek(duration);
            }
        }

        private Double isMuteChangeListener(Boolean isMute) {
            if (play.mediaPlayer != null) {
                play.mediaPlayer.setMute(isMute);
                return isMute ? 0 : play.mediaPlayer.getVolume();
            }
            return null;
        }

        private void volumeChangeListener(Double volume) {
            if (play.mediaPlayer != null) {
                if (volume > 0 && play.mediaPlayer.isMute()) {
                    play.mediaPlayer.setMute(false);
                }
                play.mediaPlayer.setVolume(volume);
            }
        }

        private void changePlayModeListener(String playModeString) {
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

        private void autoPlaySongListener(SongInfo currentSong) {
            mediaTableFx.autoScrollToSongHandler(currentSong);
        }

        private void onEndOfMediaListener(SongInfo currentSong) {
            songService.updateLastPlayedAndPlayCount(currentSong);
        }
    }

    private Play play = new Play();
    class Play {
        Consumer<SongInfo> autoPlaySongListener;
        Consumer<SongInfo> onEndOfMediaListener;
        public void setAutoPlaySongListener(Consumer<SongInfo> autoPlaySongListener) {
            this.autoPlaySongListener = autoPlaySongListener;
        }
        private void fireAutoPlaySongListener(SongInfo currentSong) {
            if (currentSong != null) {
                currentSong.setNowPlaying("▶");
            }
            if (this.autoPlaySongListener != null) {
                this.autoPlaySongListener.accept(currentSong);
            }
        }
        public void setOnEndOfMediaListener(Consumer<SongInfo> onEndOfMediaListener) {
            this.onEndOfMediaListener = onEndOfMediaListener;
        }
        private void fireOnEndOfMediaListener(SongInfo currentSong) {
            if (this.onEndOfMediaListener != null) {
                this.onEndOfMediaListener.accept(currentSong);
            }
        }

        private ObservableList<SongInfo> songsList;
        private List<SongInfo> lastPlayedSongsList = new ArrayList<>();
        private List<SongInfo> randomSongsList = new ArrayList<>();
        
        private String currentPlayMode = randomPlay;
        private SongInfo currentSong;
        private Media media;
        private MediaPlayer mediaPlayer;

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

        /**
         * Function to release player resources
         */
        private void mediaDestroy() {
            playControlsFx.stop();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            media = null;
            mediaPlayer = null;
            if (currentSong != null) {
                currentSong.setNowPlaying("");
            }
            currentSong = null;
            System.gc();
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

        /**
         * The function of music playback, and after mediaPlayer playback is over, the current playback mode is judged, 
         *  and the next song is played
         */
        private void mediaAutoPlay(int index) {
            mediaAutoPlay(getSongInfoByIdx(index));
        }

        private void mediaAutoPlay(SongInfo song) {
            if (song == null) {
                mediaDestroy();
                return;
            }
            double volume = 0;
            boolean isMute = false;
            if (mediaPlayer != null) {
                isMute = mediaPlayer.isMute();
                volume = mediaPlayer.getVolume();
                this.mediaDestroy();
            }
            currentSong = song;
            this.fireAutoPlaySongListener(currentSong);
            playControlsFx.play(song.getMusicName(), song.getSinger(), song.getTotalSeconds(), song.getTotalTime());
            media = new Media(new File(song.getSrc()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            if (volume != 0 && isMute) {
                mediaPlayer.setMute(true);
                mediaPlayer.setVolume(volume);
            } else {
                mediaPlayer.setVolume(playControlsFx.getVolume());
            }
            // The player is ready to play
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
            
            // Add a listener for the current playing time to the player, and update the information of 
            // the current playing progress bar.
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    if (mediaPlayer != null) {
                        playControlsFx.updateSognSlider(newValue, mediaPlayer.getTotalDuration().toSeconds());
                    }
                }
            });
            // Action performed by the player until the end
            mediaPlayer.setOnEndOfMedia(() -> {
                lastPlayedSongsList.add(currentSong);
                while (lastPlayedSongsList.size() > 50) {
                    lastPlayedSongsList.remove(0);
                }
                SongInfo songInfo = this.getAutoplayNextSong();
                if (songInfo == null) {
                    mediaPlayer.seek(new Duration(0));
                    playControlsFx.pause();
                } else if (songInfo.equals(currentSong)) {
                    mediaPlayer.seek(new Duration(0));
                    mediaPlayer.play();
                } else {
                    mediaAutoPlay(songInfo);
                }
            });
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


