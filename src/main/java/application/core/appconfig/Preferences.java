package application.core.appconfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("Preferences")
public class Preferences {

    // TODO: use xml or java preferences

    // VIEW
    // ----
    boolean partyMode;                  // fullscreen mode
    boolean sidePane;                   // Playlist pane
    boolean playQueueInSidePane;        // show Queue pane
    boolean songPositionSlider;         // show songProgressBar
    boolean albumArt;                   // show playing AlbumArt
    boolean followPlayingTrack;         // auto scroll to playing song
    boolean showPlayControlsToolbar;    // show play controls
    boolean showSourceToolbar;          // show Edit and Import menu items above the song list

    // GENERAL
    // -------
    enum BrowserViews {
        Artists_And_Albums,
        Genres_And_Artists,
        Genres_Artists_And_Albums
    }

    BrowserViews browserView;

    List<String> viribleColumns;

    // Playback
    // --------
    Boolean crossFadeBetweenTracks;
    Double crossfadeDurationSeconds;

    // Music
    // -----
    @XStreamImplicit(itemFieldName = "MusicLibraryLocations")
    List<String> musicLibraryLocations;
    Boolean watchMyLibraryForNewFiles;
    // Library Structure
    // Folder heirarchy
    // File name
    // Preferred format

    // Podcasts
    // ---------
    // Download location: Music
    // Check for new episodes: Every hour

    // Plugins
    // -------
    //


    public boolean isPartyMode() {
        return partyMode;
    }

    public void setPartyMode(boolean partyMode) {
        this.partyMode = partyMode;
    }

    public boolean isSidePane() {
        return sidePane;
    }

    public void setSidePane(boolean sidePane) {
        this.sidePane = sidePane;
    }

    public boolean isPlayQueueInSidePane() {
        return playQueueInSidePane;
    }

    public void setPlayQueueInSidePane(boolean playQueueInSidePane) {
        this.playQueueInSidePane = playQueueInSidePane;
    }

    public boolean isSongPositionSlider() {
        return songPositionSlider;
    }

    public void setSongPositionSlider(boolean songPositionSlider) {
        this.songPositionSlider = songPositionSlider;
    }

    public boolean isAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(boolean albumArt) {
        this.albumArt = albumArt;
    }

    public boolean isFollowPlayingTrack() {
        return followPlayingTrack;
    }

    public void setFollowPlayingTrack(boolean followPlayingTrack) {
        this.followPlayingTrack = followPlayingTrack;
    }

    public boolean isShowPlayControlsToolbar() {
        return showPlayControlsToolbar;
    }

    public void setShowPlayControlsToolbar(boolean showPlayControlsToolbar) {
        this.showPlayControlsToolbar = showPlayControlsToolbar;
    }

    public boolean isShowSourceToolbar() {
        return showSourceToolbar;
    }

    public void setShowSourceToolbar(boolean showSourceToolbar) {
        this.showSourceToolbar = showSourceToolbar;
    }

    public BrowserViews getBrowserView() {
        return browserView;
    }

    public void setBrowserView(BrowserViews browserView) {
        this.browserView = browserView;
    }

    public List<String> getViribleColumns() {
        return viribleColumns;
    }

    public void setViribleColumns(List<String> viribleColumns) {
        this.viribleColumns = viribleColumns;
    }

    public Boolean getCrossFadeBetweenTracks() {
        return crossFadeBetweenTracks;
    }

    public void setCrossFadeBetweenTracks(Boolean crossFadeBetweenTracks) {
        this.crossFadeBetweenTracks = crossFadeBetweenTracks;
    }

    public Double getCrossfadeDurationSeconds() {
        return crossfadeDurationSeconds;
    }

    public void setCrossfadeDurationSeconds(Double crossfadeDurationSeconds) {
        this.crossfadeDurationSeconds = crossfadeDurationSeconds;
    }

    public List<String> getMusicLibraryLocations() {
        return musicLibraryLocations;
    }

    public void setMusicLibraryLocations(List<String> musicLibraryLocations) {
        this.musicLibraryLocations = musicLibraryLocations;
    }

    public Boolean getWatchMyLibraryForNewFiles() {
        return watchMyLibraryForNewFiles;
    }

    public void setWatchMyLibraryForNewFiles(Boolean watchMyLibraryForNewFiles) {
        this.watchMyLibraryForNewFiles = watchMyLibraryForNewFiles;
    }
}
