package application.core;

import application.archive.dto.MediaFile;
import application.archive.dto.MediaFolder;
import application.archive.dto.MediaLibrary;
import application.archive.dto.Playlist;
import application.archive.dto.PlaylistList;
import application.songs.SongInfo;
import application.core.appconfig.AppConfig;
import application.core.preferences.Preferences;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import static application.core.appconfig.DefaultConfigurationService.configurationService;

public abstract class AbstractDaoXml<T> {

    protected static XStream xstream;

    public static final ReentrantLock lock = new ReentrantLock();

    public AbstractDaoXml() {
        initXStream();
    }

    protected Path baseDir() {
        return configurationService.getDataDir();
    }

    private void initXStream() {
        if (xstream != null) {
            return;
        }
        lock.lock();
        try {
            if (xstream != null) {
                return;
            }
            xstream = new XStream();
            xstream.autodetectAnnotations(true);
            xstream.processAnnotations(new Class[]{
                    MediaFile.class,
                    MediaFolder.class,
                    MediaLibrary.class,
                    Playlist.class,
                    PlaylistList.class,
                    SongInfo.class,
                    Preferences.class,
                    AppConfig.class
            });

            // clear out existing permissions and set own ones
            xstream.addPermission(NoTypePermission.NONE);
            // allow some basics
            xstream.addPermission(NullPermission.NULL);
            xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
            xstream.allowTypeHierarchy(Collection.class);
//        xstream.allowTypes(new Class[]{ChoseFolderList.class, ChoseFolder.class});
            xstream.allowTypesByWildcard(new String[]{
                    "application.dto.**"
            });
//        xstream.alias("ChoseFolderList", ChoseFolderList.class);
//        xstream.addImplicitCollection(ChoseFolderList.class, "choseFolders");
//        xstream.alias("ChoseFolder", ChoseFolder.class);
//        xstream.aliasAttribute(ChoseFolder.class, "path", "path");
//        xstream.addImplicitCollection(ChoseFolder.class, "songs", "song", String.class);
        } finally {
            lock.unlock();
        }
    }

    protected void write(File xmlFile, T xmlObject) {
        write(baseDir().resolve(xmlFile.getName()), xmlObject);
    }
    private void write(Path xmlPath, T xmlObject) {
        long t1 = System.currentTimeMillis();
        try (Writer writer = Files.newBufferedWriter(xmlPath)) {
            xstream.toXML(xmlObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Write time (" + xmlPath.getFileName() + "): " + ((t2 - t1)) + " ms");
    }

    protected T read(File xmlFile) {
        return read(baseDir().resolve(xmlFile.getName()));
    }
    private T read(Path xmlPath) {
        File xmlFile = xmlPath.toFile();
        long t1 = System.currentTimeMillis();
        T xmlObject = null;
        if (xmlFile.exists()) {
            xmlObject = (T) xstream.fromXML(xmlFile);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Read time (" + xmlFile.getName() + "): " + ((t2 - t1)) + " ms "/* + obj2*/);
        return xmlObject;
    }

    protected void delete(File xmlFile) {
        System.out.println("Deleted " + xmlFile.getName() + " ...");
        if (xmlFile.exists()) {
            xmlFile.delete();
        }
    }

}
