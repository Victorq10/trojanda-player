package application.core.models.songs;

import application.core.models.Dao;
import application.core.utils.DbHelper;
import application.core.utils.LogTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
