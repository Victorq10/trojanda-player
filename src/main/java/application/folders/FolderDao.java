package application.folders;

import application.core.AbstractDao;
import application.core.database.DbHelper;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static application.folders.FolderConvertor.folderConvertor;

public class FolderDao extends AbstractDao<FolderModel> {
    public static final FolderDao folderDao = new FolderDao();

    private FolderDao() {
        super("FOLDERS");
    }

    protected DbHelper.Convertor<ResultSet, FolderModel> getConvertor() {
        return folderConvertor;
    }

    //language=Derby
    private static final String folders_DerbyDb = """
            CREATE TABLE folders (
                ID         BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                Location   VARCHAR(1000),
                FolderSize VARCHAR(50),
                SongCount  INTEGER,
                Parent     BIGINT
            )
            """;

    //language=Derby
    private static final String folderLocationIdx_DerbyDb = """
        CREATE INDEX folder_Location_Idx ON Folders (Location)
        """;

    @Override
    public Map<String, String> getCreateTableStatements() {
        return Map.of("FOLDERS", folders_DerbyDb);
    }

    @Override
    public Map<String, Map<String, String>> getCreateIndexStatements() {
        return Map.of("FOLDERS", Map.of("folder_Location_Idx", folderLocationIdx_DerbyDb));
    }

    public FolderModel addFolder(FolderModel folder) throws SQLException {
        DbHelper.InsertQuery insertQuery = db().createInsertQuery("Folders");
        insertQuery.setString("Location", folder.getLocation());
        insertQuery.setString("FolderSize", folder.getFolderSize());
        insertQuery.setInt("SongCount", folder.getSongCount());
        if (folder.getParent() != null && folder.getParent().getId() != null) {
            insertQuery.setLong("Parent", folder.getParent().getId());
        }
        long folderId = db().executeQueryGenKeys(insertQuery);
        folder.setId(folderId);
        return folder;
    }

    public FolderModel addFolder(Path location, String folderSize, int songCount, Long parent) throws SQLException {
        DbHelper.InsertQuery insertQuery = db().createInsertQuery("Folders");
        insertQuery.setString("Location", location.toString());
        insertQuery.setString("FolderSize", folderSize);
        insertQuery.setInt("SongCount", songCount);
        insertQuery.setLong("Parent", parent);
        long folderId = db().executeQueryGenKeys(insertQuery);
        return getFolderById(folderId);
    }

    public FolderModel getFolderById(long id) throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM Folders WHERE ID = ?ID");
        sqlQuery.setLong("ID", id);
        List<FolderModel> result = db().selectQuery(sqlQuery, folderConvertor);
        return result.isEmpty() ? null : result.get(0);
    }

    public FolderModel getFolderByLocation(String location) throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM Folders WHERE Location = ?Location");
        sqlQuery.setString("Location", location);
        List<FolderModel> result = db().selectQuery(sqlQuery, folderConvertor);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<FolderModel> getAllFolders() throws SQLException {
        //return super.findAll();
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM Folders");
        List<FolderModel> result = db().selectQuery(sqlQuery, folderConvertor);
        return result;
    }

    public void updateFolder(FolderModel folder) throws SQLException {
        DbHelper.UpdateQuery updateQuery = db().createUpdateQuery("Folders", "Id");
        updateQuery.setLong("Id", folder.getId());
        updateQuery.setString("Location", folder.getLocation());
        updateQuery.setString("FolderSize", folder.getFolderSize());
        updateQuery.setInt("SongCount", folder.getSongCount());
        updateQuery.setLong("Parent", folder.getParent() == null ? null : folder.getParent().getId());
        db().executeQuery(updateQuery);
    }

    public void deleteFolder(FolderModel folder) throws SQLException {
        super.delete("Folders", "Id", folder.getId());
    }

}
