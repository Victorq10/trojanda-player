package application.core;

import application.core.songs.SongInfo;

import static application.TrojandaApplication.Application;
import static application.core.songs.SongService.songService;

public class UIListener {

    public void songSelectedListener(SongInfo songInfo) {
        int index = Application.play.songsList.indexOf(songInfo);
        if (index == -1) {
            System.out.println("Cannot find song " + songInfo);
            return;
        }
        Application.play.startToPlay(songInfo);
    }

    public void songSelectedOneClickListener(SongInfo songInfo) {
        // TODO: song just selectd in the playlist
        System.out.println("TODO: song just selectd in the playlist");
    }

    /**
     * Prevent the main stage borderPane from responding to mouse events and changing the opacity function.
     * blockBorderPane
     */
    public void beforeDialogShowListener() {
        // Set the main stage interface borderPane except for the titleBar part at the top, other parts do not respond to mouse events
        Application.rootBorderPane.getLeft().setMouseTransparent(true);
        Application.rootBorderPane.getCenter().setMouseTransparent(true);
        Application.rootBorderPane.getBottom().setMouseTransparent(true);
        // By the way, set the opaque color for easy reminder
        Application.rootBorderPane.getLeft().setOpacity(0.4);
        Application.rootBorderPane.getCenter().setOpacity(0.4);
        Application.rootBorderPane.getBottom().setOpacity(0.4);
    }

    /**
     * The function that releases the main stage borderPane in response to mouse events and the opacity
     * becomes the default value.
     * releaseBorderPane
     */
    public void afterDialogShowListener() {
        Application.rootBorderPane.getLeft().setMouseTransparent(false);
        Application.rootBorderPane.getCenter().setMouseTransparent(false);
        Application.rootBorderPane.getBottom().setMouseTransparent(false);
        Application.rootBorderPane.getLeft().setOpacity(1);
        Application.rootBorderPane.getCenter().setOpacity(1);
        Application.rootBorderPane.getBottom().setOpacity(1);
    }

    public void preferencesClosedListener() {
        //First need to deal with the mediaPlayer player object, release resources
        Application.mediaPlayerPlayback.despose();
        //Set the name of the song and the singer to be unknown, and the button for playing pause is the pause button
        Application.playControlsFx.setUnknown();
        Application.play.songsList.clear();
        Thread run = new Thread(() -> {
            songService.scanMusicLibrary();
            Application.play.reloadSongs();
            //Set the content of the song table and sort by the song title column
            //mediaTableFx.setSongsList(play.songsList);
        });
        run.start();
    }
}
