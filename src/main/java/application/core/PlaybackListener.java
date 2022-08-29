package application.core;

import application.core.models.songs.SongInfo;
import javafx.util.Duration;

import static application.TrojandaApplication.Application;
import static application.core.models.SongService.songService;

public class PlaybackListener {

    public void currentTimeChangeListener(Double totalDurationInSeconds, Duration newValue) {
        if (totalDurationInSeconds != null && Application.playControlsFx != null) {
            Application.playControlsFx.updateSognSlider(newValue, totalDurationInSeconds);
        }
    }

    public void onEndOfMediaListener() {
        songService.updateLastPlayedAndPlayCount(Application.play.currentSong);
        Application.play.updateLastPlayedSongsList();

        SongInfo songInfo = Application.play.getAutoplayNextSong();
        if (songInfo == null) {
            Application.mediaPlayerPlayback.seek(new Duration(0));
            Application.playControlsFx.pause();
        } else if (songInfo.equals(Application.play.currentSong)) {
            Application.mediaPlayerPlayback.seek(new Duration(0));
            Application.mediaPlayerPlayback.play();
        } else {
            Application.play.startToPlay(songInfo);
        }
    }

    public void startToPlaySongListener(SongInfo song) {
        Application.mediaTableFx.autoScrollToSongHandler(song);
        Application.playControlsFx.play(song.getMusicName(), song.getSinger(), song.getTotalSeconds(), song.getTotalTime());
        Application.play.updateSongOnStartToPlay(song);
    }


}
