package application.core.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static application.core.config.PreferencesXmlDao.preferencesDaoXml;

public class PreferencesService {
    public static final PreferencesService preferencesService = new PreferencesService();

    PreferencesXmlModel preferences;

    private PreferencesXmlModel getPreferences() {
        if (preferences == null) {
            preferences = preferencesDaoXml.readPreferences();
        }
        return preferences;
    }

    public List<String> getMusicLibraryLocations() {
        if (getPreferences().getMusicLibraryLocations() != null) {
            List<String> folders = getPreferences().getMusicLibraryLocations().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return folders;
        }
        return Collections.emptyList();
    }

    public boolean isFolderExists(final String folderPath) {
        return getPreferences().getMusicLibraryLocations().stream()
                .anyMatch(location -> folderPath.equals(location));
    }

    public void addMediaFolder(String folderPath) {
        if (!isFolderExists(folderPath)) {
            getPreferences().getMusicLibraryLocations().add(folderPath);
            preferencesDaoXml.writePreferences(getPreferences());
        }
    }

    public void removeMediaFolder(String locationPath) {
        Iterator<String> it = getPreferences().getMusicLibraryLocations().iterator();
        boolean removed = false;
        while (it.hasNext()) {
            String location = it.next();
            if (Objects.equals(location, locationPath)) {
                it.remove();
                removed = true;
            }
        }
        if (removed) {
            preferencesDaoXml.writePreferences(getPreferences());
        }
    }

    private <T> void removeItem(List<T> list, Predicate<T> predicate) {
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T choseFolder = it.next();
            if (predicate.test(choseFolder)) {
                it.remove();
            }
        }
    }
}
