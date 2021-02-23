package application.dao;

import application.TrojandaApplication;
import application.models.AbstractModel;
import application.services.impl.DefaultDatabaseService;
import application.services.impl.DefaultSongService;
import application.utils.DbHelper;

import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractDao<T extends AbstractModel> {
    DbHelper db() {
        return DefaultDatabaseService.databaseService.dbHelper;
    }
    
    abstract Map<String, String> getCreateTableStatements();
    abstract Map<String, Map<String, String>> getCreateIndexStatements();

    public boolean delete(String tableName, String pkName, long pkValue) throws SQLException {
        DbHelper.DeleteQuery deleteQuery = db().createDeleteQuery(tableName, pkName);
        deleteQuery.setLong(pkName, pkValue);
        return db().executeQuery(deleteQuery);
    }

}
