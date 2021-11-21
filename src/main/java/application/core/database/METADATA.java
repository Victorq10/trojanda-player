package application.core.database;

import application.core.folders.FolderModel;
import application.core.songs.SongModel;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * EXPERIMENTAL
 */
public abstract class METADATA {

    public static METADATA FOLDER = new METADATA() {
        void init() {
            modelClass(FolderModel.class);
            table("FOLDER", "ID");
            //column("ID", "BIGINT", Types.BIGINT, FolderModel::getId, FolderModel::setId);
            column("ID", "BIGINT", Types.BIGINT);
            column("LOCATION", "VARCHAR(1000)", Types.VARCHAR);
            column("FOLDER_SIZE", "VARCHAR(50)", Types.VARCHAR);
            column("SONG_COUNT", "INTEGER", Types.INTEGER);
            column("PARENT", "BIGINT", Types.BIGINT);
            referenceField("PARENT", "FOLDER");
        }
    };

    public static METADATA SONG = new METADATA() {
        void init() {
            modelClass(SongModel.class);
            table("SONG", "ID");
            column("ID", "BIGINT", Types.BIGINT);
            column("LOCATION", "VARCHAR(1000)", Types.VARCHAR);
            column("FOLDER_SIZE", "VARCHAR(50)", Types.VARCHAR);
            referenceField("FOLDER", "FOLDER");
        }
    };

    abstract void init();

    METADATA() {
        init();
    }

    public static class COLUMN {
        String name;
        String typeName;
        int type;
        COLUMN(String name, String typeName, int type) {
            this.name = name;
            this.typeName = typeName;
            this.type = type;
        }
    }

    Class modelClass;
    String tableName;
    List<String> pkNames = new ArrayList<>();
    Map<String, COLUMN> fields = new LinkedHashMap<>();
    Map<String, String> foringFields = new HashMap<>();


    <T> void modelClass(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    void table(String tableName, String pkName) {
        this.tableName = tableName;
        pkNames.add(pkName);
    }

    void column(String fieldName, String typeName, int type, Supplier<?> getter, Consumer<?> setter) {
        this.fields.put(fieldName, new COLUMN(fieldName, typeName, type));
    }

    void column(String fieldName, String typeName, int type) {
        this.fields.put(fieldName, new COLUMN(fieldName, typeName, type));
    }

    void referenceField(String fieldName, String tableName) {
        this.foringFields.put(fieldName, tableName);
    }

    void primaryField(String... pkName) {
        Collections.addAll(pkNames, pkName);
    }

}
