package application.daoxml;

import application.dto.AppConfig;

import java.io.File;

public class AppConfigDaoXml extends AbstractDaoXml<AppConfig> {
    public static final AppConfigDaoXml INSTANCE = new AppConfigDaoXml();
    
    File configXmlFile = new File("AppConfig.xml");

    public void writeAppConfig(AppConfig appConfig) {
        super.write(configXmlFile, appConfig);
    }

    public AppConfig readAppConfig() {
        AppConfig config = super.read(configXmlFile);
        if (config == null) {
            config = new AppConfig();
        }
        return config;
    }

}
