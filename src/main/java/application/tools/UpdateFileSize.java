package application.tools;

import application.core.database.DbHelper;
import application.songs.SongModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static application.core.database.DatabaseService.databaseService;
import static application.songs.SongDao.songDao;

public class UpdateFileSize {

    public static void main(String[] args) throws IOException, SQLException {
        try {
            databaseService.initConnection();
            UpdateFileSize t = new UpdateFileSize();
            t.formatFileSize();
        } finally {
            databaseService.stop();
        }
    }

    DbHelper db() {
        return databaseService.dbHelper;
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
