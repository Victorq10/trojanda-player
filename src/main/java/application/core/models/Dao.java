package application.core.models;

import application.core.utils.DbHelper;

import java.sql.ResultSet;

public interface Dao {

    String getTableName();

    <T> DbHelper.Convertor<ResultSet, T> getConvertor();
}
