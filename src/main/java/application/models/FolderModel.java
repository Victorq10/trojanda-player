package application.models;

import java.util.List;

public class FolderModel extends AbstractModel {
    Long id;
    String location;
    String folderSize;
    Integer songCount;
    FolderModel parent;
    List<SongModel> songs;

    public FolderModel() {
    }
    
    public FolderModel(long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFolderSize() {
        return folderSize;
    }

    public void setFolderSize(String folderSize) {
        this.folderSize = folderSize;
    }

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public FolderModel getParent() {
        return parent;
    }

    public void setParent(FolderModel parent) {
        this.parent = parent;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}
