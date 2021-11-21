package application.core.playlists;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static application.core.playlists.PlaylistDao.playlistDao;

public class PlaylistService {
    public static final PlaylistService playlistService = new PlaylistService();

    public List<String> getPlaylistNames() {
        List<PlaylistModel> playlists;
        try {
            playlists = playlistDao.getAllPlaylists();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return playlists.stream()
                .map(PlaylistModel::getName)
                .collect(Collectors.toList());
    }

    public void addPlaylist(String playlistName) {
        try {
            playlistDao.addPlaylist(createPlaylist(playlistName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PlaylistModel createPlaylist(String playlistName) {
        PlaylistModel playlist = new PlaylistModel();
        playlist.setName(playlistName);
        return playlist;
    }

}
