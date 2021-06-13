package application.core.appconfig;

public class DefaultAppConfigService {
    public static final DefaultAppConfigService INSTANCE = new DefaultAppConfigService();

    AppConfigDaoXml appConfigDaoXml = AppConfigDaoXml.INSTANCE;
    AppConfig appConfig;

    private AppConfig getAppConfig() {
        if (appConfig == null) {
            appConfig = appConfigDaoXml.readAppConfig();
        }
        return appConfig;
    }

    private void saveAppConfig() {
        if (appConfig != null) {
            appConfigDaoXml.writeAppConfig(appConfig);
        }
    }

    public String getDbName() {
        if (getAppConfig().getDbName() != null) {
            return getAppConfig().getDbName();
        }
        return null;
    }

}
