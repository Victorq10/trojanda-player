package application.core.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DbHelper {

    //public static final DbHelper INSTANCE = new DbHelper();

    //private DataSource dataSource;
    private Connection connection;

    public DbHelper(Connection connection) {
        this.connection = connection;
    }


    public InsertQuery createInsertQuery(String tableName) {
        return new InsertQuery(tableName);
    }

    public UpdateQuery createUpdateQuery(String tableName, String...pkName) {
        return new UpdateQuery(tableName, pkName);
    }

    public DeleteQuery createDeleteQuery(String tableName, String...pkName) {
        return new DeleteQuery(tableName, pkName);
    }

    public SqlQuery createSqlQuery(String sql, Map<String, Object> parameters) {
        return new SqlQuery(sql, parameters);
    }

    public SqlQuery createSqlQuery(String sql) {
        return new SqlQuery(sql);
    }



    // ==================================== //
    //    GENERIC IMPLEMENTATION            //
    // ==================================== //

    public static class ParameterValueType {
        public String name;
        public Object value;
        public int type; // see; java.sql.Types
        public List<Integer> indexes = new ArrayList<>();

        public ParameterValueType(String name, Object value, int type) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    /**
     * idia from https://stefanbangels.blogspot.com/2013/09/named-placeholders-for-sql-parameters.html
     */
    public static class SqlQuery {
        protected String sql;
        protected List<String> placeHolders = new LinkedList<>();
        //protected Map<String, ParameterValueType> params = new HashMap<>();
        protected List<ParameterValueType> params = new ArrayList<>();

        private final Pattern placeHolderPattern = Pattern.compile(
                "(?:\\?|:)([A-Za-z0-9_]+)"
        );

        private SqlQuery() { // can use private as it is used in the nested class
            // for inheritance of UpdateQuery, InsertQuery, DeleteQuery
        }

        public SqlQuery(String sql, Map<String, Object> parameters) {
            this(sql);
            this.setParameters(parameters);
        }

        public SqlQuery(String sql) {
            List<String> placeHolders = new LinkedList<>();
            Matcher matcher = placeHolderPattern.matcher(sql);
            while (matcher.find()) {
                placeHolders.add(matcher.group(1));
            }
            this.placeHolders = placeHolders;
            this.sql = matcher.replaceAll("?");
        }


        public String getSql() {
            return sql;
        }

        public void setString(String name, String value) {
            params.add(new ParameterValueType(name, value, Types.VARCHAR));
        }

        public void setDouble(String name, Double value) {
            params.add(new ParameterValueType(name, value, Types.DOUBLE));
        }

        public void setBigDecimal(String name, BigDecimal value) {
            params.add(new ParameterValueType(name, value, Types.DECIMAL));
        }

        public void setLong(String name, Long value) {
            params.add(new ParameterValueType(name, value, Types.BIGINT));
        }

        public void setInt(String name, Integer value) {
            params.add(new ParameterValueType(name, value, Types.INTEGER));
        }

        public void setDate(String name, java.util.Date value) {
            params.add(new ParameterValueType(name, value, Types.TIMESTAMP));
        }

        public void setParameters(Map<String, Object> parameters) {
            if (parameters == null || parameters.isEmpty()) {
                return;
            }
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    setString(name, (String) value);
                } else if (value instanceof Double) {
                    setDouble(name, (Double) value);
                } else if (value instanceof BigDecimal) {
                    setBigDecimal(name, (BigDecimal) value);
                } else if (value instanceof Long) {
                    setLong(name, (Long) value);
                } else if (value instanceof Integer) {
                    setInt(name, (Integer) value);
                } else if (value instanceof Date) {
                    setDate(name, (Date) value);
                } else {
                    throw new RuntimeException("Unknown parameter type:" + value);
                }
            }
        }

        protected void applyParams(PreparedStatement statement) throws SQLException {
            for (ParameterValueType param : params) {
                findPlaceHolderIndexes(param);
                for (Integer index : param.indexes) {
                    statement.setObject(index, param.value, param.type);
                }
            }
        }

        protected void findPlaceHolderIndexes(ParameterValueType param) {
            if (!param.indexes.isEmpty()) {
                return;
            }
            int i = 1;
            for (String placeHolder : placeHolders) {
                if (param.name.equalsIgnoreCase(placeHolder)) {
                    param.indexes.add(i);
                }
                i++;
            }
        }

        protected List<Integer> findPlaceHolderIndexes(String paramName) {
            List<Integer> indexes = new LinkedList<>();
            int n = 1;
            for (String placeHolder : placeHolders) {
                if (paramName.equalsIgnoreCase(placeHolder)) {
                    indexes.add(n);
                }
                n++;
            }
            return indexes;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement preparedStatement = con.prepareStatement(getSql());
            applyParams(preparedStatement);
            return preparedStatement;
        }

/*
        public boolean executeQuery() {

        }

        public <T> List<T> selectQuery(Convertor<ResultSet, T> converter) throws SQLException {
            return DbHelper.this.selectQuery(this, converter);
        }
*/
    }

    public static class InsertQuery extends SqlQuery {
        protected String tableName;

        private InsertQuery(String tableName) {
            this.tableName = tableName;
        }

        @Override
        public String getSql() {
            List<String> fieldNames = new ArrayList<>();
            List<String> fieldPlaceholders = new ArrayList<>();
            int i = 1;
            for (DbHelper.ParameterValueType e : params) {
                fieldNames.add(e.name);
                fieldPlaceholders.add("?");
                placeHolders.add(e.name);
                e.indexes.add(i);
                i++;
            }
            StringBuilder s = new StringBuilder();
            s.append("INSERT INTO " + this.tableName + " (" + String.join(",", fieldNames) + ")\n");
            s.append("VALUES (" + String.join(",", fieldPlaceholders) + ")");
            this.sql = s.toString();
            return this.sql;
        }
    }

    public static class UpdateQuery extends SqlQuery {
        protected String tableName;
        List<String> pkNames = new ArrayList<>();

        private UpdateQuery(String tableName, String... pkName) {
            this.tableName = tableName;
            Collections.addAll(this.pkNames, pkName);
        }

        @Override
        public String getSql() {
            List<String> updateFields = new ArrayList<>();
            List<String> identityFields = new ArrayList<>();
            int i = 1;
            for (DbHelper.ParameterValueType e : getUpdateParams()) {
                updateFields.add(e.name + " = ?");
                placeHolders.add(e.name);
                e.indexes.add(i);
                i++;
            }
            for (DbHelper.ParameterValueType e : getIdentityParams()) {
                identityFields.add(e.name + " = ?");
                placeHolders.add(e.name);
                e.indexes.add(i);
                i++;
            }
            StringBuilder s = new StringBuilder();
            s.append("UPDATE " + this.tableName + " SET\n");
            s.append("    " + String.join(",\n    ", updateFields) + "\n");
            s.append("WHERE " + String.join("\n    AND ", identityFields));
            this.sql = s.toString();
            return this.sql;
        }

        private List<ParameterValueType> getUpdateParams() {
            return params.stream().filter(p -> !pkNames.contains(p.name)).collect(Collectors.toList());
        }

        private List<ParameterValueType> getIdentityParams() {
            return params.stream().filter(p -> pkNames.contains(p.name)).collect(Collectors.toList());
        }
    }

    public static class DeleteQuery extends SqlQuery {
        String tableName;
        List<String> pkNames = new ArrayList<>();

        private DeleteQuery(String tableName, String... pkName) {
            this.tableName = tableName;
            Collections.addAll(this.pkNames, pkName);
        }

        @Override
        public String getSql() {
            List<String> identityFields = new ArrayList<>();
            int i = 1;
            for (DbHelper.ParameterValueType e : getIdentityParams()) {
                identityFields.add(e.name + " = ?");
                placeHolders.add(e.name);
                e.indexes.add(i);
                i++;
            }
            StringBuilder s = new StringBuilder();
            s.append("DELETE FROM " + this.tableName + " WHERE " + String.join("\n    AND ", identityFields));
            this.sql = s.toString();
            return this.sql;
        }

        private List<ParameterValueType> getIdentityParams() {
            return params.stream().filter(p -> pkNames.contains(p.name)).collect(Collectors.toList());
        }
    }

    public boolean executeQuery(String sql) throws SQLException {
        DbHelper.SqlQuery sqlQuery = new SqlQuery(sql);
        return executeQuery(sqlQuery);
    }

    public boolean executeQuery(String sql, Map<String, Object> parameters) throws SQLException {
        SqlQuery sqlQuery = new SqlQuery(sql, parameters);
        return this.executeQuery(sqlQuery);
    }

    public boolean executeQuery(SqlQuery sqlQuery) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery.getSql())) {
            sqlQuery.applyParams(statement);
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public long executeQueryGenKeys(SqlQuery sqlQuery) throws SQLException {
        long genKey = 0;
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery.getSql(), Statement.RETURN_GENERATED_KEYS)) {
            sqlQuery.applyParams(statement);
            statement.execute();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                genKey = keys.getLong(1);
            }
            keys.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return genKey;
    }

    public <T> List<T> selectQuery(String sql, Map<String, Object> parameters, Convertor<ResultSet, T> converter) throws SQLException {
        SqlQuery sqlQuery = new SqlQuery(sql, parameters);
        return this.selectQuery(sqlQuery, converter);
    }

    public <T> List<T> selectQuery(SqlQuery sqlQuery, Convertor<ResultSet, T> converter) throws SQLException {
        List<T> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = sqlQuery.createPreparedStatement(connection);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(converter.convert(resultSet));
            }
        } catch (SQLException e) {
            throw e;
        }
        return result;
    }

    public <T> List<T> selectQuery(String sql, Convertor<ResultSet, T> converter) throws SQLException {
        List<T> result = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                result.add(converter.convert(resultSet));
            }
        } catch (SQLException e) {
            throw e;
        }
        return result;
    }

    public interface Convertor<T, R> {
        R convert(T t) throws SQLException;
    }

    public interface RecordSetToMapConvertor
            extends Convertor<ResultSet, Map<String, Object>> {
    }

}
