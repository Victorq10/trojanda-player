package application.core.appconfig;

import static application.core.appconfig.AppConfigDaoXml.appConfigDaoXml;

public class AppConfigService {
    public static final AppConfigService appConfigService = new AppConfigService();

    private AppConfig appConfig;

    public AppConfig getAppConfig() {
        // NOTE: Maybe using '/Configuration.properties' is better
        if (appConfig == null) {
            appConfig = appConfigDaoXml.readAppConfig();
        }
        return appConfig;
    }

    public void saveAppConfig() {
        appConfigDaoXml.writeAppConfig(getAppConfig());
    }

    /**
     * This properties is duplication of {@link ConfigurationService#getDbName()}
     *
     * @return
     */
    public String getDbName() {
        if (getAppConfig().getDbName() != null) {
            return getAppConfig().getDbName();
        }
        return null;
    }

}
