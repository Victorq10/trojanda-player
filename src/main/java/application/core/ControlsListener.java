package application.core;

import javafx.scene.control.Label;
import javafx.util.Duration;

import static application.TrojandaApplication.Application;

public class ControlsListener {

    public void playPreviousListener() {
        Application.play.startToPlay(-1);
    }

    public final void playButtonListener() {
        if (!Application.mediaPlayerPlayback.play()) {
            Application.play.startToPlay(0);
        }
    }

    public final void pauseButtonListener() {
        Application.mediaPlayerPlayback.pause();
    }

    public final void playNextListener() {
        Application.play.startToPlay(1);
    }

    public void songPositionSliderChangeListener(Duration duration) {
        Application.mediaPlayerPlayback.seek(duration);
    }

    public Double isMuteButtonChangeListener(Boolean isMute) {
        return Application.mediaPlayerPlayback.setMute(isMute);
    }

    public void volumeSliderChangeListener(Double volume) {
        Application.mediaPlayerPlayback.setVolume(volume);
    }

    public void playModeButtonChangeListener(String playModeString) {
        Application.play.currentPlayMode = playModeString;
        Label playModeLabel = new Label(playModeString);
        Application.ToastPlayModeInfo(playModeLabel);
    }

}
