package application.dao;

import application.models.AbstractModel;
import application.models.PlaylistModel;
import application.services.impl.DefaultDatabaseService;
import application.utils.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class AbstractDao<T extends AbstractModel> {
    DbHelper db() {
        return DefaultDatabaseService.INSTANCE.dbHelper;
    }
    
    protected String tableName;
    protected AbstractDao(String tableName) {
        this.tableName = tableName;
    }
    abstract Map<String, String> getCreateTableStatements();
    abstract Map<String, Map<String, String>> getCreateIndexStatements();

    public boolean delete(String tableName, String pkName, long pkValue) throws SQLException {
        DbHelper.DeleteQuery deleteQuery = db().createDeleteQuery(tableName, pkName);
        deleteQuery.setLong(pkName, pkValue);
        return db().executeQuery(deleteQuery);
    }
    
    abstract DbHelper.Convertor<ResultSet, T> getConvertor();
    
    public List<T> findAll() throws SQLException {
        DbHelper.SqlQuery sqlQuery = db().createSqlQuery("SELECT * FROM " + tableName);
        List<T> result = db().selectQuery(sqlQuery, getConvertor());
        return result;
    }

}
