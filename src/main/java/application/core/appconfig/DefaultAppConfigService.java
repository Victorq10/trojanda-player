package application.core.appconfig;

import static application.core.appconfig.AppConfigDaoXml.appConfigDaoXml;

public class DefaultAppConfigService {
    public static final DefaultAppConfigService appConfigService = new DefaultAppConfigService();

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
