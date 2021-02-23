package application.services.impl;

import application.dao.FolderDao;
import application.dao.PlaylistDao;
import application.dao.SongDao;
import application.utils.DbHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDatabaseService {

    DefaultConfigurationService configurationService = DefaultConfigurationService.INSTANCE;
    FolderDao folderDao = FolderDao.INSTANCE;
    PlaylistDao playlistDao = PlaylistDao.INSTANCE;
    SongDao songDao = SongDao.INSTANCE;

    private Connection dbConnection;
    public DbHelper dbHelper;

    public AbstractDatabaseService() throws IOException {
        openConnection();
    }

    abstract String getConnectionUrl() throws IOException;

    abstract boolean createTablesAndIndexes() throws IOException;

    protected Connection getConnection() {
        return dbConnection;
    }

    public final void openConnection() throws IOException {
        try {
            dbConnection = DriverManager.getConnection(getConnectionUrl());
            dbHelper = new DbHelper(dbConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTablesAndIndexes();
    }

    //@Override
    public void stop() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
        }
    }

    protected void createTableIfNotExists(Statement statement, Map<String, String> createTableStatements) throws SQLException {
        for (Map.Entry<String, String> entry : createTableStatements.entrySet()) {
            String tableName = entry.getKey();
            String createTableStatement = entry.getValue();
            if (!this.isTableExists(null, tableName)) {
                statement.execute(createTableStatement);
            }
        }
    }

    protected void createIndexIfNotExists(Statement statement, Map<String, Map<String, String>> createTableStatements) throws SQLException {
        for (Map.Entry<String, Map<String, String>> entry : createTableStatements.entrySet()) {
            String tableName = entry.getKey();
            Map<String, String> indexes = entry.getValue();
            for (Map.Entry<String, String> index : indexes.entrySet()) {
                String indexName = index.getKey();
                String createIndexStatement = index.getValue();
                if (!this.isIndexExists(null, tableName, indexName)) {
                    statement.execute(createIndexStatement);
                }
            }
        }
    }

    protected boolean isTableExists(String schema, String table) throws SQLException {
        ResultSet tableNames = dbConnection.getMetaData().getTables(null, schema, table, null);
        if (tableNames.next()) {
            return tableNames.getString("TABLE_NAME").equalsIgnoreCase(table);
        }
/*
        String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES as C " +
                "WHERE " +
                "    lower(C.TABLE_SCHEMA)    = '" + schema.toLowerCase() + "' " +
                "    AND lower(C.TABLE_NAME) = '" + table.toLowerCase() + "'" +
                "";
        Connection con = dbConnection;
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw e;
        }
*/
        return false;

    }

    protected boolean isIndexExists(String schema, String tableName, String indexName) throws SQLException {
        ResultSet tableNames = dbConnection.getMetaData().getIndexInfo(null, schema, tableName, false, false);
        while (tableNames.next()) {
            if (tableNames.getString("INDEX_NAME").equalsIgnoreCase(indexName)) {
                return true;
            }
        }
        return false;
    }

    protected void logMetaDataTables() throws SQLException {
        //ResultSet tableNames = dbConnection.getMetaData().getTables(null, "PUBLIC", "ADDRESS", null);
        ResultSet tables = dbConnection.getMetaData().getTables(null, "APP", null, null);
        System.out.println("————————————————— ALL TABLES —————————————————");
        logRecords(tables);
        tables.close();
        tables = dbConnection.getMetaData().getTables(null, "APP", null, null);
        List<String> tableNames = new ArrayList<>();
        while (tables.next()) {
            tableNames.add(tables.getString("TABLE_NAME"));
        }
        tables.close();
        for (String tableName : tableNames) { //List.of("FOLDERS", "SONGS", "PLAYLIST", "PLAYLISTSONGRELATION")) {
            System.out.println("————————————————— INDEXES FOR '" + tableName + "' TABLE —————————————————");
            try (ResultSet indexes = dbConnection.getMetaData().getIndexInfo(null, null, tableName, false, false)) {
                logRecords(indexes);
            }
        }
    }

    protected void logRecords(ResultSet rs) throws SQLException {
        List<String> skipColumnNames = List.of(
                "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME",
                "REF_GENERATION",
                "TABLE_CAT", "INDEX_QUALIFIER", "CARDINALITY", "PAGES", "FILTER_CONDITION");

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        List<List<String>> table = new ArrayList<>();
        int[] columnWidth = new int[columnCount + 1];
        boolean[] skip = new boolean[columnCount + 1];
        // resultSet column names
        List<String> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String value = meta.getColumnName(i);
            skip[i] = skipColumnNames.contains(value);
            if (skip[i]) {
                continue;
            }
            int k = row.size();
            columnWidth[k] = Math.max(columnWidth[k], value == null ? 1 : value.length());
            row.add(value);
        }
        table.add(row);

        // resultSet data
        while (rs.next()) {
            List<String> row1 = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                if (skip[i]) {
                    continue;
                }
                String value = rs.getString(i);
                int k = row1.size();
                columnWidth[k] = Math.max(columnWidth[k], value == null ? 1 : value.length());
                row1.add(value);
            }
            table.add(row1);
        }
        // output formatted table
        for (List<String> row3 : table) {
            for (int k = 0; k < row3.size(); k++) {
                row3.set(k, formatValue(row3.get(k), columnWidth[k]));
            }
            System.out.println("" + String.join("|", row3) + "");
        }
        System.out.println();
    }

    private String formatValue(String value, int width) {
        StringBuilder sb = new StringBuilder(value == null ? "" : value);
        while (sb.length() < width) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
