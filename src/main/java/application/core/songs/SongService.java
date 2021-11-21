package application.core.songs;

import application.core.utils.LogTime;
import application.core.folders.FolderModel;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static application.core.audiotags.AudioTagsService.audioTagsService;
import static application.core.preferences.PreferencesService.preferencesService;
import static application.core.folders.FolderService.folderService;
import static application.core.songs.SongDao.songDao;


public class SongService {
    public static final SongService songService = new SongService();

    private List<String> supportedFileTypes = List.of("mp3", "ogg", "opus", "m4a");

    public void removeMediaLibrary() {
        // TODO: implement remove library on scan
    }

    public List<SongInfo> getAllSongFiles() {
        return getAllSongFilesFromDatabase();
    }

    private List<SongInfo> getAllSongFilesFromDatabase() {
        List<SongInfo> songInfos = new ArrayList<>();
        try {
            List<SongModel> songs = songDao.getAllMp3Songs();
            LogTime t = new LogTime();
            for (SongModel song : songs) {
                SongInfo songInfo = new SongInfo();
                songInfo.setId(song.getId());
                songInfo.setMusicName(song.getTitle());
                songInfo.setSinger(song.getArtist());
                songInfo.setAlbum(song.getAlbum());
                songInfo.setSize(song.getFileSize());
                songInfo.setSrc(song.getLocation());
                songInfo.setTotalTime(song.getDuration());
                songInfo.setTotalSeconds(toSeconds(song.getDuration()));
                // extra
                songInfo.setTrackNumber(song.getTrackNumber());
                songInfo.setPerformer(song.getPerformer());
                songInfo.setComposer(song.getComposer());
                songInfo.setYear(song.getRecordedYear());
                songInfo.setDiscNumber(song.getDiscNumber());
                songInfo.setQuality(song.getQuality());
                songInfo.setGenre(song.getGenre());
                songInfo.setComment(song.getComment());
                songInfo.setBpm(song.getBpm());
                songInfo.setCoverImage(song.getCoverImage());
                songInfo.setLyrics(song.getLyrics());
                songInfos.add(songInfo);
            }
            t.log("SQL: getAllSongFilesFromDatabase creation time of %d DTO objects", songInfos.size());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return songInfos;
    }

    private int toSeconds(String duration) {
        try {
            String[] parts = duration.split(":");
            if (parts.length == 3) {
                return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
            } else if (parts.length == 2) {
                return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            }
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            System.err.println("Cannot parse duration '" + duration + "'");
        }
        return 0;
    }

    public void scanMusicLibrary() {
        try {
            List<String> musicLibraryLocations = preferencesService.getMusicLibraryLocations();
            if (musicLibraryLocations == null || musicLibraryLocations.isEmpty()) {
                return;
            }
            saveMediaFoldersToDatabase(musicLibraryLocations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMediaFoldersToDatabase(List<String> mediaFolders) {
        List<Path> existedDirs = mediaFolders.stream()
                .map(Paths::get)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        for (Path dirPath : existedDirs) {
            try {
                LogTime logTime = new LogTime();
                List<FolderModel> folders = folderService.addAllFoldersToDatabase(dirPath);
                logTime.log("Collect all " + folders.size() + " folders");
                LogTime t2 = new LogTime();
                addAllSongsToDatabase(folders);
                t2.log("Add All Songs To Database");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<SongModel> addAllSongsToDatabase(List<FolderModel> folders) throws IOException, SQLException {
        List<SongModel> savedSongs = new ArrayList<>();
        for (FolderModel currentFolder : folders) {
            Path currentDirPath = Paths.get(currentFolder.getLocation());
            if (!Files.isDirectory(currentDirPath)) {
                continue;
            }
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(currentDirPath, this::isSupportedFile)) {
                for (Path songPath : dirStream) {
                    SongModel song = songDao.getSongByLocation(songPath.toString());
                    if (song == null) {
                        song = createSongModel(songPath, currentFolder);
                        songDao.addSong(song);
                    }
                    savedSongs.add(song);
                }
            }
        }
        return savedSongs;
    }

    private boolean isSupportedFile(final Path path) {
        if (path == null || !Files.isRegularFile(path)) {
            return false;
        }
        String fileName = path.getFileName().toString();
        return supportedFileTypes.contains(getFileExt(fileName));
    }

    private String getStringOrDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public void updateLastPlayedAndPlayCount(SongInfo songInfo) {
        try {
            SongModel song = songDao.getSongById(songInfo.getId());
            if (song != null) {
                song.setLastPlayed(new Date());
                song.setPlayCount(song.getPlayCount() == null ? 1 : song.getPlayCount().intValue() + 1);
                songDao.updateLastPlayedAndPlayCount(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SongModel createSongModel(Path songPath, FolderModel folder) {
        String fileName = songPath.getFileName().toString();
        String fileType = getFileExt(fileName);
        String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
        SongInfo songInfo = audioTagsService.getSongInfo(songPath);
        SongModel song = new SongModel();
        song.setFolder(folder);
        song.setTrackNumber(songInfo.getTrackNumber());
        song.setTitle(getStringOrDefault(songInfo.getMusicName(), fileNameWithoutExt));
        song.setArtist(songInfo.getSinger());
        song.setPerformer(songInfo.getPerformer());
        song.setComposer(songInfo.getComposer());
        song.setAlbum(songInfo.getAlbum());
        song.setRecordedYear(songInfo.getYear());
        song.setDiscNumber(songInfo.getDiscNumber());
        song.setQuality(songInfo.getQuality());
        song.setGenre(songInfo.getGenre());
        song.setComment(songInfo.getComment());
        song.setBpm(songInfo.getBpm());
        song.setCoverImage(songInfo.getCoverImage());
        song.setLyrics(songInfo.getLyrics());
        // file
        song.setFileType(fileType);
        song.setFileName(fileName);
        song.setLocation(songPath.toString());
        song.setFileSize(songInfo.getSize());
        song.setDuration(songInfo.getTotalTime());
        return song;
    }
}
