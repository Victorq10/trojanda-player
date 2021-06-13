package application.playlists;

import application.core.utils.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistConvertor implements DbHelper.Convertor<ResultSet, PlaylistModel> {
    public static final PlaylistConvertor INSTANCE = new PlaylistConvertor();

    private PlaylistConvertor() {
    }

    @Override
    public PlaylistModel convert(ResultSet rs) throws SQLException {
        PlaylistModel playlist = new PlaylistModel();
        playlist.setId(rs.getLong("ID"));
        playlist.setName(rs.getString("NAME"));
        return playlist;
    }
}
