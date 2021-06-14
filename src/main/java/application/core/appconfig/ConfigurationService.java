package application.core.appconfig;

import application.TrojandaApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static application.core.appconfig.AppConfigService.appConfigService;
import static application.core.appconfig.PreferencesService.preferencesService;

/**
 * Three level of configuration:<br>
 * 1) Application level of configuration {@link ConfigurationService}.<br>
 * 2) User level of configuration {@link AppConfigService}.<br>
 * 3) Application preferences {@link PreferencesService}.<br><br>
 *
 * <p><b>Configuration.properties</b> — it can contain NOT editable configuration as image resources, paddings.</p>
 * <p><b>AppConfig.xml</b> — it is stored in the DataDir and can be changed by user by editing config file.</p>
 * <p><b>Preferences.xml</b> — it is stored in the DataDir and can be changed by user by UI Preferences dialog.</p>
 */
public class ConfigurationService {
    public static final ConfigurationService configurationService = new ConfigurationService();

    private Properties properties = null;
    private String dbName;

    static {
        loadConfiguration();
    }

    public Path getDataDir() {
        Path configDir = Paths.get(System.getProperty("user.home", "."), ".trojandaMusicPlayer");
        if (!Files.exists(configDir)) {
            configDir.toFile().mkdirs();
            //Files.createDirectories(configDir);
        }
        return configDir;
    }

    public AppConfigService getAppConfigService() {
        return appConfigService;
    }

    public PreferencesService getPreferencesService() {
        return preferencesService;
    }

    private static final void loadConfiguration() {
        configurationService.properties = new Properties();
        InputStream dbPropInputStream = TrojandaApplication.class.getResourceAsStream("/Configuration.properties");
        try {
            configurationService.properties.load(dbPropInputStream);
            configurationService.dbName = configurationService.properties.getProperty("dbName", "MusicLibraryDb");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDbName() {
        return dbName;
    }


}
