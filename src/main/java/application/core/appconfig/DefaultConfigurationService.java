package application.core.appconfig;

import application.TrojandaApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static application.core.appconfig.AppConfigDaoXml.appConfigDaoXml;

/**
 * Three level of configuration:<br>
 * 1) Application level of configuration.<br>
 * 2) User level of configuration.<br>
 * 3) Application preferences.<br><br>
 *
 * <p><b>Configuration.properties</b> — it can contain NOT editable configuration as image resources, paddings.</p>
 * <p><b>AppConfig.xml</b> — it is stored in the DataDir and can be changed by user by editing config file.</p>
 * <p><b>Preferences.xml</b> — it is stored in the DataDir and can be changed by user by UI Preferences dialog.</p>
 */
public class DefaultConfigurationService {
    public static DefaultConfigurationService configurationService;

    private AppConfig appConfig;
    private Properties dbProperties = null;
    private String dbName;

    static {
        DefaultConfigurationService.configurationService = new DefaultConfigurationService();
        loadConfiguration();
    }

    private AppConfig getAppConfig() {
        // NOTE: Maybe using '/Configuration.properties' is better
        if (appConfig == null) {
            appConfig = appConfigDaoXml.readAppConfig();
        }
        return appConfig;
    }

    private void saveAppConfig() {
        appConfigDaoXml.writeAppConfig(getAppConfig());
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
        configurationService.dbProperties = new Properties();
        InputStream dbPropInputStream = TrojandaApplication.class.getResourceAsStream("/Configuration.properties");
        try {
            configurationService.dbProperties.load(dbPropInputStream);
            configurationService.dbName = configurationService.dbProperties.getProperty("dbName", "MusicLibraryDb");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDbName() {
        return dbName;
    }


}
