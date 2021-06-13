package application.core.database;

import application.folders.FolderModel;
import application.songs.SongModel;

import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DefaultDatabaseService extends AbstractDatabaseService {
    public static final DefaultDatabaseService INSTANCE = new DefaultDatabaseService();

    private DefaultDatabaseService() {
        super();
    }

    @Override
    protected String getConnectionUrl() {
        // Set the db system directory.
        System.setProperty("derby.system.home", configurationService.getDataDir().toString());
        String connectionUrl = "jdbc:derby:derbydb;create=true"; //+ ";user=dbuser;password=dbuserpwd";
        return connectionUrl;
    }

    @Override
    protected boolean createTablesAndIndexes() {
        boolean createdTables = false;
        String[] dropTables = {
                "DROP TABLE \"APP\".\"PLAYLISTSONGRELATION\"",
                "DROP TABLE \"APP\".\"SONGS\"",
                "DROP TABLE \"APP\".\"FOLDERS\"",
                "DROP TABLE \"APP\".\"PLAYLIST\""
        };
        try (Statement statement = getConnection().createStatement()) {
            logMetaDataTables();
            createTableIfNotExists(statement, folderDao.getCreateTableStatements());
            createTableIfNotExists(statement, songDao.getCreateTableStatements());
            createTableIfNotExists(statement, playlistDao.getCreateTableStatements());

            createIndexIfNotExists(statement, folderDao.getCreateIndexStatements());
            createIndexIfNotExists(statement, songDao.getCreateIndexStatements());
            createIndexIfNotExists(statement, playlistDao.getCreateIndexStatements());
            createdTables = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return createdTables;
    }

    protected void insertRecords() {
        try (Statement statement = getConnection().createStatement()) {
            if (this.isTableExists(null, "ADDRESS")) {
                statement.execute("INSERT INTO ADDRESS (LASTNAME) VALUES('Viktor') ");
                statement.execute("INSERT INTO ADDRESS (LASTNAME) VALUES('Tetjana') ");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public FolderModel addFolder(Path location, String folderSize, int songCount, Long parent) throws SQLException {
        return folderDao.addFolder(location, folderSize, songCount, parent);
    }

    public FolderModel getFolderById(long id) throws SQLException {
        return folderDao.getFolderById(id);
    }

    public List<FolderModel> getAllFolders() throws SQLException {
        return folderDao.getAllFolders();
    }

    public void updateFolder(FolderModel folder) throws SQLException {
        folderDao.updateFolder(folder);
    }

    public void deleteFolder(FolderModel folder) throws SQLException {
        folderDao.deleteFolder(folder);
    }


    public void addSong(SongModel song) throws SQLException {
        songDao.addSong(song);
    }

    public List<SongModel> getAllSongs() throws SQLException {
        return songDao.getAllSongs();
    }

    public void updateSong(SongModel song) throws SQLException {
        songDao.updateSong(song);
    }

    public void deleteSong(SongModel song) throws SQLException {
        songDao.deleteSong(song);
    }
}
