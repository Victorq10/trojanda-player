package application.songs;

import application.folders.FolderModel;
import application.core.database.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SongConvertor implements DbHelper.Convertor<ResultSet, SongModel> {
    public static final SongConvertor songConvertor = new SongConvertor();

    private SongConvertor() {}

    @Override
    public SongModel convert(ResultSet rs) throws SQLException {
        boolean folderIsNull = rs.getObject("FOLDER") == null;
        SongModel song = new SongModel();
        song.setId(rs.getLong("ID"));
        song.setFolder(folderIsNull ? new FolderModel() : new FolderModel(rs.getLong("FOLDER")));
        song.setTrackNumber(rs.getString("TRACKNUMBER"));
        song.setTitle(rs.getString("TITLE"));
        song.setArtist(rs.getString("ARTIST"));
        song.setPerformer(rs.getString("PERFORMER"));
        song.setComposer(rs.getString("COMPOSER"));
        song.setAlbum(rs.getString("ALBUM"));
        song.setRecordedYear(rs.getString("RECORDEDYEAR"));
        song.setDiscNumber(rs.getString("DISCNUMBER"));
        song.setQuality(rs.getString("QUALITY"));
        song.setGenre(rs.getString("GENRE"));
        song.setComment(rs.getString("COMMENT"));
        song.setBpm(rs.getString("BPM"));
        song.setCoverImage(rs.getString("COVERIMAGE"));
        song.setLyrics(rs.getString("LYRICS"));
        song.setFileType(rs.getString("FILETYPE"));
        song.setFileName(rs.getString("FILENAME"));
        song.setLocation(rs.getString("LOCATION"));
        song.setFileSize(rs.getString("FILESIZE"));
        song.setDuration(rs.getString("DURATION"));
        song.setRating(rs.getInt("RATING"));
        song.setDateAdded(rs.getDate("DATEADDED"));
        song.setLastPlayed(rs.getDate("LASTPLAYED"));
        song.setPlayCount(rs.getInt("PLAYCOUNT"));
        return song;
    }
}
