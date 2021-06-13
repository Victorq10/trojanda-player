package application.core.audiotags;

import application.songs.SongInfo;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultAudioTagsService {
    public static final DefaultAudioTagsService INSTANCE = new DefaultAudioTagsService();

    public SongInfo getSongInfo(Path songPath) {
        SongInfo songInfo = null;
        try {
            File songFile = songPath.toFile();

            //创建MP3文件对象，设置歌名，歌手，专辑等信息
            songInfo = new SongInfo();
            songInfo.setSrc(songPath.toString());
            songInfo.setSize(formatSize(getFileSize(songPath)));

            Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);
            Logger.getLogger("org.jaudiotagger.tag").setLevel(Level.OFF);
            Logger.getLogger("org.jaudiotagger.audio.mp3.MP3File").setLevel(Level.OFF);
            Logger.getLogger("org.jaudiotagger.tag.id3.ID3v23Tag").setLevel(Level.OFF);

            boolean useNew = true;
            if (useNew) {
                AudioFile audioFile = AudioFileIO.read(songFile);
                AudioHeader audioHeader = audioFile.getAudioHeader();
                Tag tag = audioFile.getTag();
                songInfo.setMusicName(tag.getFirst(FieldKey.TITLE));
                songInfo.setSinger(tag.getFirst(FieldKey.ARTIST));
                songInfo.setAlbum(tag.getFirst(FieldKey.ALBUM));
                songInfo.setTotalSeconds(audioHeader.getTrackLength());
                songInfo.setTotalTime(formatHMS(audioHeader.getTrackLength()));

                // additional tags
                songInfo.setYear(tag.getFirst(FieldKey.YEAR));
                songInfo.setTrackNumber(tag.getFirst(FieldKey.TRACK));
                songInfo.setPerformer(tag.getFirst(FieldKey.ALBUM_ARTIST));
                songInfo.setComposer(tag.getFirst(FieldKey.COMPOSER));
                songInfo.setDiscNumber(tag.getFirst(FieldKey.DISC_NO));
                songInfo.setQuality(audioHeader.getBitRate());
                songInfo.setGenre(tag.getFirst(FieldKey.GENRE));
                songInfo.setComment(tag.getFirst(FieldKey.COMMENT));
                songInfo.setBpm(tag.getFirst(FieldKey.BPM));
                songInfo.setCoverImage(tag.getFirst(FieldKey.COVER_ART));
                songInfo.setLyrics(tag.getFirst(FieldKey.LYRICS));

            } else {
                MP3File mp3File = (MP3File) AudioFileIO.read(songFile);
                MP3AudioHeader mp3AudioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
                if (mp3File.hasID3v2Tag()) {
                    songInfo.setMusicName(mp3File.getID3v2Tag().getFirst("TIT2")); //读取歌名
                    songInfo.setSinger(mp3File.getID3v2Tag().getFirst("TPE1")); //读取歌手
                    songInfo.setAlbum(mp3File.getID3v2Tag().getFirst("TALB"));  //读取专辑名
                } else if (mp3File.hasID3v1Tag()) {
                    ID3v1Tag id3v1Tag = mp3File.getID3v1Tag();
                    songInfo.setMusicName(id3v1Tag.getFirst(FieldKey.TITLE));
                    songInfo.setSinger(id3v1Tag.getFirst(FieldKey.ARTIST));
                    songInfo.setAlbum(id3v1Tag.getFirst(FieldKey.ALBUM));
                }
                songInfo.setTotalSeconds(mp3AudioHeader.getTrackLength());
                songInfo.setTotalTime(mp3AudioHeader.getTrackLengthAsString()); // as 04：30
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (TagException e1) {
            e1.printStackTrace();
        } catch (ReadOnlyFileException e1) {
            e1.printStackTrace();
        } catch (CannotReadException e1) {
            e1.printStackTrace();
        } catch (InvalidAudioFrameException e1) {
            e1.printStackTrace();
        }
        return songInfo;
    }

    private long getFileSize(Path songPath) {
        try {
            return Files.size(songPath);
        } catch (IOException e) {
            System.err.println("Error on determining a size of a '" + songPath + "' file:" + e.getMessage());
        }
        return 0;
    }


    private String formatSize(long fileSize) {
        if (fileSize >= (1_000_000_000)) {
            return String.format("%,.1fGB", fileSize / 1_000_000_000.);
        } else if (fileSize >= (1_000_000)) {
            return String.format("%.1fMB", fileSize / 1_000_000.);
        } else if (fileSize >= (1_000)) {
            return String.format("%.1fKB", fileSize / 1_000.);
        }
        return String.format("%dB", fileSize);
    }

    private String formatHMS(int time) {
        int hours = time / 3600;
        int minutes = (time - hours * 3600) / 60;
        int seconds = time - hours * 3600 - minutes * 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
