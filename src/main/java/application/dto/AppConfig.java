package application.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Config")
public class AppConfig {
    
    public String dbName;
    public String playMode;
    public int sidePanelWidth;
    public String lastSelectedList;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
