package application.core.appconfig;

import static application.core.appconfig.AppConfigDaoXml.appConfigDaoXml;

public class AppConfigService {
    public static final AppConfigService appConfigService = new AppConfigService();

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
