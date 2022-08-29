package application.core.config;

import application.core.utils.XStreamUtils;

import java.io.File;
import java.util.ArrayList;

public class PreferencesXmlDao {
    public static final PreferencesXmlDao preferencesDaoXml = new PreferencesXmlDao();

    File preferencesXmlFile = new File("Preferences.xml");

    public void writePreferences(PreferencesXmlModel preferences) {
        XStreamUtils.write(preferencesXmlFile, preferences);
    }

    public PreferencesXmlModel readPreferences() {
        PreferencesXmlModel preferences = XStreamUtils.read(preferencesXmlFile);
        if (preferences == null) {
            preferences = new PreferencesXmlModel();
        }
        if (preferences.getMusicLibraryLocations() == null) {
            preferences.setMusicLibraryLocations(new ArrayList<>());
        }
        return preferences;
    }


}
