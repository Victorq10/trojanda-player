package application.tools;

import application.songs.SongDao;
import application.songs.SongModel;
import application.core.database.DefaultDatabaseService;
import application.core.utils.DbHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UpdateFileSize {

    SongDao songDao = SongDao.INSTANCE;

    public static void main(String[] args) throws IOException, SQLException {
        try {
            DefaultDatabaseService.INSTANCE.initConnection();
            UpdateFileSize t = new UpdateFileSize();
            t.formatFileSize();
        } finally {
            DefaultDatabaseService.INSTANCE.stop();
        }
    }

    DbHelper db() {
        return DefaultDatabaseService.INSTANCE.dbHelper;
    }

    private void formatFileSize() throws SQLException {
        List<SongModel> songs = songDao.getAllSongs();
        for (SongModel song : songs) {
            String fileSize = formatFileSize(song.getFileSize());
            if (!Objects.equals(fileSize, song.getFileSize())) {
                DbHelper.UpdateQuery updateQuery = db().createUpdateQuery("Songs", "Id");
                updateQuery.setLong("Id", song.getId());
                updateQuery.setString("FileSize", fileSize);
                db().executeQuery(updateQuery);
            }
        }
    }

    private String formatFileSize(String fileSize) {
        if (fileSize.indexOf("B") > -1) {
            return fileSize;
        }
        return formatSize(Long.parseLong(fileSize));
    }

    private String formatSize(long fileSize) {
        if (fileSize >= (1_000_000_000)) {
            return String.format("%,.1fGB", fileSize / 1_000_000_000.);
        } else if (fileSize >= (1_000_000)) {
            return String.format("%.1fMB", fileSize / 1_000_000.);
        } else if (fileSize >= (1_000)) {
            return String.format("%.1fKB", fileSize / 1_000.);
        }
        return String.format("%dB", fileSize);
    }

}
