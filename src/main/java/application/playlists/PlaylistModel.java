package application.playlists;

import application.core.AbstractModel;
import application.songs.SongModel;

import java.util.List;

public class PlaylistModel extends AbstractModel {
    Long id;
    String name;
    List<SongModel> songs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}
