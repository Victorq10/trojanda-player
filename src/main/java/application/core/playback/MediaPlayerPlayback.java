package application.core.playback;

import application.songs.SongInfo;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.function.Consumer;

public class MediaPlayerPlayback {
    private Media media;
    private MediaPlayer mediaPlayer;

    ChangeListener<Duration> currentTimeChangelistener;
    Runnable onEndOfMediaListener;
    Consumer<SongInfo> startToPlaySongListener;

    public void setOnCurrentTimeChangeListener(ChangeListener<Duration> currentTimeChangelistener) {
        this.currentTimeChangelistener = currentTimeChangelistener;
    }

    public void setOnEndOfMediaListener(Runnable onEndOfMediaListener) {
        this.onEndOfMediaListener = onEndOfMediaListener;
    }
    public void setStartToPlaySongListener(Consumer<SongInfo> startToPlayListener) {
        this.startToPlaySongListener = startToPlayListener;
    }

    public void despose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            media = null;
            mediaPlayer = null;
        }
        System.gc();
    }

    public boolean autoPlay(SongInfo song, double controlVolume) {
        if (song == null) {
            despose();
            // TODO: check if we need to update UI play controls and currentSong in the TableView
            return false;
        }
        double volume = 0;
        boolean isMute = false;
        if (mediaPlayer != null) {
            isMute = mediaPlayer.isMute();
            volume = mediaPlayer.getVolume();
        }
        despose();
        media = new Media(new File(song.getSrc()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        if (volume != 0 && isMute) {
            mediaPlayer.setMute(true);
            mediaPlayer.setVolume(volume);
        } else {
            mediaPlayer.setVolume(controlVolume);
        }
        mediaPlayer.setOnReady(() -> mediaPlayer.play());
        // Add a listener for the current playing time to the player, and update the information of
        // the current playing progress bar.
        mediaPlayer.currentTimeProperty().addListener(currentTimeChangelistener);
        // Action performed by the player until the end
        mediaPlayer.setOnEndOfMedia(onEndOfMediaListener);
        if (this.startToPlaySongListener != null) {
            this.startToPlaySongListener.accept(song);
        }
        return true;
    }

    public boolean isMute() {
        if (mediaPlayer != null) {
            return mediaPlayer.isMute();
        }
        return false;
    }
    // TODO: review return volume
    public final Double setMute(boolean isMute) {
        if (mediaPlayer != null /*&& (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING
                || mediaPlayer.getStatus() == MediaPlayer.Status.READY)*/) {
            mediaPlayer.setMute(isMute);
            return isMute ? 0. : mediaPlayer.getVolume();
        }
        return null;
    }

    public double getVolume() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVolume();
        }
        return -1;
    }

    public final void setVolume(double volume) {
        if (mediaPlayer != null) {
            if (volume > 0 && mediaPlayer.isMute()) {
                mediaPlayer.setMute(false);
            }
            mediaPlayer.setVolume(volume);
        }
    }

    public final boolean play() {
        if (mediaPlayer != null && (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED
                || mediaPlayer.getStatus() == MediaPlayer.Status.READY)) {
            mediaPlayer.play();
            return true;
        }
        return false;
    }

    public final boolean pause() {
        if (mediaPlayer != null && (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING
                || mediaPlayer.getStatus() == MediaPlayer.Status.READY)) {
            mediaPlayer.pause();
            return true;
        }
        return false;
    }

    public final boolean seek(Duration duration) {
        if (mediaPlayer != null && (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING
                || mediaPlayer.getStatus() == MediaPlayer.Status.READY)) {
            mediaPlayer.seek(duration);
            return true;
        }
        return false;
    }

    public final Double getTotalDurationInSeconds() {
        if (mediaPlayer != null) {
            return mediaPlayer.getTotalDuration().toSeconds();
        }
        return null;
    }
}
