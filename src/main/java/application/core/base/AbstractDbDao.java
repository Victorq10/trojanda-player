package application.core.base;

import application.core.database.DatabaseService;
import application.core.database.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class AbstractDbDao<T extends AbstractDbModel> {
    protected DbHelper db() {
        return DatabaseService.databaseService.dbHelper;
    }

    protected String tableName;
    protected AbstractDbDao(String tableName) {
        this.tableName = tableName;
    }
    protected abstract Map<String, String> getCreateTableStatements();
    protected abstract Map<String, Map<String, String>> getCreateIndexStatements();

    protected boolean delete(String tableName, String pkName, long pkValue) throws SQLException {
        DbHelper.DeleteQuery deleteQuery = db().createDeleteQuery(tableName, pkName);
        deleteQuery.setLong(pkName, pkValue);
        return db().executeQuery(deleteQuery);
    }

    protected abstract DbHelper.Convertor<ResultSet, T> getConvertor();

    public List<T> findAll() throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM " + tableName);
        List<T> result = db().selectQuery(sqlQuery, getConvertor());
        return result;
    }

}
