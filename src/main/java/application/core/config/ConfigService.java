package application.core.config;

import static application.core.config.ConfigXmlDao.appConfigDaoXml;

public class ConfigService {
    public static final ConfigService appConfigService = new ConfigService();

    private ConfigXmlModel appConfig;

    public ConfigXmlModel getAppConfig() {
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
