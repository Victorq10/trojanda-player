package application.core.preferences;

import application.core.AbstractDaoXml;

import java.io.File;
import java.util.ArrayList;

public class PreferencesDaoXml extends AbstractDaoXml<Preferences> {
    public static final PreferencesDaoXml INSTANCE = new PreferencesDaoXml();

    File preferencesXmlFile = new File("Preferences.xml");

    public void writePreferences(Preferences preferences) {
        super.write(preferencesXmlFile, preferences);
    }

    public Preferences readPreferences() {
        Preferences preferences = super.read(preferencesXmlFile);
        if (preferences == null) {
            preferences = new Preferences();
        }
        if (preferences.getMusicLibraryLocations() == null) {
            preferences.setMusicLibraryLocations(new ArrayList());
        }
        return preferences;
    }

}
