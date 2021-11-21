package application.core.folders;

import application.core.database.DbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderConvertor implements DbHelper.Convertor<ResultSet, FolderModel> {
    public static final FolderConvertor folderConvertor = new FolderConvertor();

    private FolderConvertor() {}

    @Override
    public FolderModel convert(ResultSet rs) throws SQLException {
        boolean parentIsNull = rs.getObject("PARENT") == null;
        FolderModel item = new FolderModel();
        item.setId(rs.getLong("ID"));
        item.setLocation(rs.getString("LOCATION"));
        item.setFolderSize(rs.getString("FOLDERSIZE"));
        item.setSongCount(rs.getInt("SONGCOUNT"));
        item.setParent(parentIsNull ? null : new FolderModel(rs.getLong("PARENT")));
        return item;
    }
}
