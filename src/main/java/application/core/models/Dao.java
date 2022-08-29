package application.core.models;

import application.core.utils.DbHelper;

import java.sql.ResultSet;
import java.util.Map;

public interface Dao {

    String getTableName();

    Map<String, String> getCreateTableStatements();
    Map<String, Map<String, String>> getCreateIndexStatements();

    <T> DbHelper.Convertor<ResultSet, T> getConvertor();
}
