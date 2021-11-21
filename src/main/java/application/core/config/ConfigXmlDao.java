package application.core.config;

import application.core.base.AbstractXmlDao;

import java.io.File;

public class ConfigXmlDao extends AbstractXmlDao<ConfigXmlModel> {
    public static final ConfigXmlDao appConfigDaoXml = new ConfigXmlDao();

    File configXmlFile = new File("AppConfig.xml");

    public void writeAppConfig(ConfigXmlModel appConfig) {
        super.write(configXmlFile, appConfig);
    }

    public ConfigXmlModel readAppConfig() {
        ConfigXmlModel config = super.read(configXmlFile);
        if (config == null) {
            config = new ConfigXmlModel();
        }
        return config;
    }

}
