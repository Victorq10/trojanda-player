package application.core.preferences;

import application.core.base.AbstractXmlDao;

import java.io.File;
import java.util.ArrayList;

public class PreferencesXmlDao extends AbstractXmlDao<PreferencesXmlModel> {
    public static final PreferencesXmlDao preferencesDaoXml = new PreferencesXmlDao();

    File preferencesXmlFile = new File("Preferences.xml");

    public void writePreferences(PreferencesXmlModel preferences) {
        super.write(preferencesXmlFile, preferences);
    }

    public PreferencesXmlModel readPreferences() {
        PreferencesXmlModel preferences = super.read(preferencesXmlFile);
        if (preferences == null) {
            preferences = new PreferencesXmlModel();
        }
        if (preferences.getMusicLibraryLocations() == null) {
            preferences.setMusicLibraryLocations(new ArrayList<>());
        }
        return preferences;
    }

}
