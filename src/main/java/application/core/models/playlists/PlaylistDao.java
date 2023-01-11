package application.core.models.playlists;

import application.core.models.Dao;
import application.core.models.songs.SongModel;
import application.core.utils.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static application.core.models.playlists.PlaylistConvertor.playlistConvertor;
import static application.core.models.songs.SongConvertor.songConvertor;
import static application.core.utils.DatabaseService.db;

public class PlaylistDao implements Dao {
    public static final PlaylistDao playlistDao = new PlaylistDao();

    public String getTableName() {
        return "PLAYLIST";
    }

    public DbHelper.Convertor<ResultSet, PlaylistModel> getConvertor() {
        return playlistConvertor;
    }

    public PlaylistModel addPlaylist(PlaylistModel playlist) throws SQLException {
        DbHelper.InsertQuery insertQuery = db().createInsertQuery("Playlist");
        insertQuery.setString("Name", playlist.getName());
        long playlistId = db().executeQueryGenKeys(insertQuery);
        playlist.setId(playlistId);
        return playlist;
    }

    public void addPlaylistSongRelation(long playlistId, long songId) throws SQLException {
        DbHelper.InsertQuery insertQuery = db().createInsertQuery("PlaylistSongRelation");
        insertQuery.setLong("source", playlistId);
        insertQuery.setLong("target", songId);
        db().executeQueryGenKeys(insertQuery);
    }

    public List<PlaylistModel> getAllPlaylists() throws SQLException {
        //return super.findAll();
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM Playlist");
        List<PlaylistModel> result = db().selectQuery(sqlQuery, playlistConvertor);
        return result;
    }

    public PlaylistModel loadPlaylistSongs(PlaylistModel playlist) throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("""
                SELECT s.* 
                FROM PlaylistSongRelation AS l
                   JOIN Songs AS s ON l.target = s.id
                WHERE l.source = ?playlistId
                """);
        sqlQuery.setLong("playlistId", playlist.getId());
        List<SongModel> result = db().selectQuery(sqlQuery, songConvertor);
        playlist.setSongs(result);
        return playlist;
    }

    public void deletePlaylist(PlaylistModel playlist) throws SQLException {
        db().delete("PlaylistSongRelation", "source", playlist.getId());
        db().delete("Playlist", "id", playlist.getId());
    }
}
