package application.playlists;

import application.core.AbstractDao;
import application.core.database.DbHelper;
import application.songs.SongModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static application.playlists.PlaylistConvertor.playlistConvertor;
import static application.songs.SongConvertor.songConvertor;

public class PlaylistDao extends AbstractDao<PlaylistModel> {
    public static final PlaylistDao playlistDao = new PlaylistDao();

    private PlaylistDao() {
        super("PLAYLIST");
    }

    protected DbHelper.Convertor<ResultSet, PlaylistModel> getConvertor() {
        return playlistConvertor;
    }

    //language=Derby
    private static final String playlist_DerbyDb = """
            CREATE TABLE Playlist (
                ID        BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
                NAME      VARCHAR(255)
            )
            """;

    //language=Derby
    private static final String playlistSongRelation_DerbyDb = """
            CREATE TABLE PlaylistSongRelation (
                ID          BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                source      BIGINT NOT NULL,
                target      BIGINT NOT NULL,
                UNIQUE (source, target),
                FOREIGN KEY (source) REFERENCES Playlist (ID),
                FOREIGN KEY (target) REFERENCES Songs (ID)
            )
            """;


    @Override
    public Map<String, String> getCreateTableStatements() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("PLAYLIST", playlist_DerbyDb);
        map.put("PLAYLISTSONGRELATION", playlistSongRelation_DerbyDb);
        return map;
    }

    @Override
    public Map<String, Map<String, String>> getCreateIndexStatements() {
        return Collections.emptyMap();
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
        super.delete("PlaylistSongRelation", "source", playlist.getId());
        super.delete("Playlist", "id", playlist.getId());
    }
}
