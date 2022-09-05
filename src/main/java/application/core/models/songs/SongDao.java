package application.core.models.songs;

import application.core.models.Dao;
import application.core.utils.DbHelper;
import application.core.utils.LogTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static application.core.models.songs.SongConvertor.songConvertor;
import static application.core.utils.DatabaseService.db;

public class SongDao implements Dao {
    public static final SongDao songDao = new SongDao();

    public String getTableName() {
        return "SONGS";
    }

    public DbHelper.Convertor<ResultSet, SongModel> getConvertor() {
        return songConvertor;
    }

    //language=Derby
    private static final String songs_DerbyDb = """
            CREATE TABLE songs (
                ID            BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                Folder        BIGINT NOT NULL,
                TrackNumber   VARCHAR(50),
                Title         VARCHAR(255),
                Artist        VARCHAR(255),
                Performer     VARCHAR(255),     -- AlbumArtist
                Composer      VARCHAR(255),
                Album         VARCHAR(255),
                RecordedYear  VARCHAR(50),      -- Year
                DiscNumber    VARCHAR(50),
                Quality       VARCHAR(50),
                Genre         VARCHAR(255),
                Comment       VARCHAR(3000),
                BPM           VARCHAR(255),
                CoverImage    VARCHAR(3000),    -- Images are saved in the folder CoverImages
                Lyrics        VARCHAR(3000),
                -- file  characteristic
                FileType      VARCHAR(50),      -- mp3, opus, ogg, m4a (mp4, m4v), flac, wma, wav
                FileName      VARCHAR(255),     -- Just file name
                Location      VARCHAR(1000),    -- Location to file
                FileSize      VARCHAR(50),      -- 3.4MB
                Duration      VARCHAR(50),      -- 3:32
                Rating        INTEGER,          -- from 0 to 5
                DateAdded     TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP,
                LastPlayed    TIMESTAMP,
                PlayCount     INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (Folder) REFERENCES FOLDERS(ID)
            )""".replaceAll("\\s*+--.*?\\n", "\n");

    //language=Derby
    private static final String songLocationIdx_DerbyDb = """
            CREATE INDEX song_Location_Idx ON Songs (Location)
            """;
    //language=Derby
    private static final String songFileTypeIdx_DerbyDb = """
            CREATE INDEX song_FileType_Idx ON Songs (FileType)
            """;
/*
ALTER TABLE APP.SONGS ALTER Comment SET DATA TYPE VARCHAR(3000);
ALTER TABLE APP.SONGS ALTER CoverImage SET DATA TYPE VARCHAR(3000);
*/

    @Override
    public Map<String, String> getCreateTableStatements() {
        return Map.of("SONGS", songs_DerbyDb);
    }

    @Override
    public Map<String, Map<String, String>> getCreateIndexStatements() {
        return Map.of("SONGS", Map.of(
                "song_Location_Idx", songLocationIdx_DerbyDb,
                "song_FileType_Idx", songFileTypeIdx_DerbyDb
        ));
    }

    public void addSong(SongModel song) throws SQLException {
        Date now = new Date();
        DbHelper.InsertQuery insertQuery = db().createInsertQuery("Songs");
        insertQuery.setLong("Folder", song.getFolder() == null ? null : song.getFolder().getId());
        insertQuery.setString("TrackNumber", song.getTrackNumber());
        insertQuery.setString("Title", song.getTitle());
        insertQuery.setString("Artist", song.getArtist());
        insertQuery.setString("Performer", song.getPerformer());
        insertQuery.setString("Composer", song.getComposer());
        insertQuery.setString("Album", song.getAlbum());
        insertQuery.setString("RecordedYear", song.getRecordedYear());
        insertQuery.setString("DiscNumber", song.getDiscNumber());
        insertQuery.setString("Quality", song.getQuality());
        insertQuery.setString("Genre", song.getGenre());
        insertQuery.setString("Comment", song.getComment());
        insertQuery.setString("BPM", song.getBpm());
        insertQuery.setString("CoverImage", song.getCoverImage());
        insertQuery.setString("Lyrics", song.getLyrics());
        insertQuery.setString("FileType", song.getFileType());
        insertQuery.setString("FileName", song.getFileName());
        insertQuery.setString("Location", song.getLocation());
        insertQuery.setString("FileSize", song.getFileSize());
        insertQuery.setString("Duration", song.getDuration());
        insertQuery.setInt("Rating", -1);
        insertQuery.setDate("DateAdded", now);
        insertQuery.setDate("LastPlayed", now);
        insertQuery.setInt("PlayCount", 0);
        long id = db().executeQueryGenKeys(insertQuery);
        song.setId(id);
    }

    public List<SongModel> getAllSongs() throws SQLException {
        LogTime t = new LogTime();
        // List<SongModel> result = super.findAll();
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM Songs");
        List<SongModel> result = db().selectQuery(sqlQuery, songConvertor);
        t.log("SQL: getAllSongs");
        return result;
    }

    public List<SongModel> getAllMp3Songs() throws SQLException {
        LogTime t = new LogTime();
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("""
                        SELECT * FROM Songs
                        WHERE filetype = 'mp3'
                        """
                // AND Location like '/home/viktor/Music/SLUXATI/Diskoteka/%'
        );
        List<SongModel> result = db().selectQuery(sqlQuery, songConvertor);
        t.log("SQL: getAllMp3Songs");
        return result;
    }

    public SongModel getSongByLocation(String location) throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery(
                "SELECT * FROM Songs WHERE Location = ?Location");
        sqlQuery.setString("Location", location);
        List<SongModel> result = db().selectQuery(sqlQuery, songConvertor);
        return result.isEmpty() ? null : result.get(0);
    }

    public SongModel getSongById(Long id) throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery(
                "SELECT * FROM Songs WHERE Id = ?Id");
        sqlQuery.setLong("Id", id);
        List<SongModel> result = db().selectQuery(sqlQuery, songConvertor);
        return result.isEmpty() ? null : result.get(0);
    }

    public void updateSong(SongModel song) throws SQLException {
        DbHelper.UpdateQuery updateQuery = db().createUpdateQuery("Songs", "Id");
        updateQuery.setLong("Id", song.getId());
        updateQuery.setLong("Folder", song.getFolder() == null ? null : song.getFolder().getId());
        updateQuery.setString("TrackNumber", song.getTrackNumber());
        updateQuery.setString("Title", song.getTitle());
        updateQuery.setString("Artist", song.getArtist());
        updateQuery.setString("Performer", song.getPerformer());
        updateQuery.setString("Composer", song.getComposer());
        updateQuery.setString("Album", song.getAlbum());
        updateQuery.setString("RecordedYear", song.getRecordedYear());
        updateQuery.setString("DiscNumber", song.getDiscNumber());
        updateQuery.setString("Quality", song.getQuality());
        updateQuery.setString("Genre", song.getGenre());
        updateQuery.setString("Comment", song.getComment());
        updateQuery.setString("BPM", song.getBpm());
        updateQuery.setString("CoverImage", song.getCoverImage());
        updateQuery.setString("Lyrics", song.getLyrics());
        updateQuery.setString("FileType", song.getFileType());
        updateQuery.setString("FileName", song.getFileName());
        updateQuery.setString("Location", song.getLocation());
        updateQuery.setString("FileSize", song.getFileSize());
        updateQuery.setString("Duration", song.getDuration());
        updateQuery.setInt("Rating", song.getRating());
        updateQuery.setDate("DateAdded", song.getDateAdded());
        updateQuery.setDate("LastPlayed", song.getLastPlayed());
        updateQuery.setInt("PlayCount", song.getPlayCount());
        db().executeQuery(updateQuery);
    }

    public void updateLastPlayedAndPlayCount(SongModel song) throws SQLException {
        song.setPlayCount((song.getPlayCount() == null ? 0 : song.getPlayCount().intValue()) + 1);
        song.setLastPlayed(new Date());
        DbHelper.UpdateQuery updateQuery = db().createUpdateQuery("Songs", "Id");
        updateQuery.setLong("Id", song.getId());
        updateQuery.setDate("LastPlayed", song.getLastPlayed());
        updateQuery.setInt("PlayCount", song.getPlayCount());
        db().executeQuery(updateQuery);
    }

    public void updateRating(SongModel song, int rating) throws SQLException {
        song.setRating(rating);
        DbHelper.UpdateQuery updateQuery = db().createUpdateQuery("Songs", "Id");
        updateQuery.setLong("Id", song.getId());
        updateQuery.setInt("Rating", song.getRating());
        db().executeQuery(updateQuery);
    }


    public void deleteSong(SongModel song) throws SQLException {
        db().delete("Songs", "Id", song.getId());
    }
}
