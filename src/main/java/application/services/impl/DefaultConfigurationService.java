package application.services.impl;

import application.TrojandaApplication;
import application.daoxml.AppConfigDaoXml;
import application.dto.AppConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * <p>Configuration.properties — it can contain not editable configuration as image resources, paddings.</p>
 * <p>AppConfig — it is stored in the DataDir and can be changed by user </p>
 */
public class DefaultConfigurationService {
    public static DefaultConfigurationService INSTANCE;
    private AppConfigDaoXml configDaoXml = AppConfigDaoXml.INSTANCE;

    private AppConfig appConfig;
    private Properties dbProperties = null;
    private String dbName;

    static {
        DefaultConfigurationService.INSTANCE = new DefaultConfigurationService();
        loadConfiguration();
    }

    private AppConfig getAppConfig() {
        // NOTE: Maybe using '/Configuration.properties' is better
        if (appConfig == null) {
            appConfig = configDaoXml.readAppConfig();
        }
        return appConfig;
    }

    private void saveAppConfig() {
        configDaoXml.writeAppConfig(getAppConfig());
    }

    public Path getDataDir() {
        Path configDir = Paths.get(System.getProperty("user.home", "."), ".trojandaMusicPlayer");
        if (!Files.exists(configDir)) {
            configDir.toFile().mkdirs();
            //Files.createDirectories(configDir);
        }
        return configDir;
    }

    private static final void loadConfiguration() {
        INSTANCE.dbProperties = new Properties();
        InputStream dbPropInputStream = TrojandaApplication.class.getResourceAsStream("/Configuration.properties");
        try {
            INSTANCE.dbProperties.load(dbPropInputStream);
            INSTANCE.dbName = INSTANCE.dbProperties.getProperty("dbName", "MusicLibraryDb");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDbName() {
        return dbName;
    }


}
