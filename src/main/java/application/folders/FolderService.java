package application.folders;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static application.folders.FolderDao.folderDao;


public class FolderService {
    public static final FolderService folderService = new FolderService();

    public List<FolderModel> addAllFoldersToDatabase(Path dirPath) throws IOException, SQLException {
        List<FolderModel> savedFolders = new ArrayList<>();
        LinkedList<FolderModel> queue = new LinkedList<>();
        queue.offer(createFolderModel(dirPath, null));
        for (FolderModel currentFolder = queue.poll(); currentFolder != null; currentFolder = queue.poll()) {
            FolderModel existedFolder = folderDao.getFolderByLocation(currentFolder.getLocation());
            if (existedFolder == null) {
                folderDao.addFolder(currentFolder);
            } else {
                currentFolder = existedFolder;
            }
            savedFolders.add(currentFolder);

            Path currentPath = Paths.get(currentFolder.getLocation());
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(currentPath, (p) -> Files.isDirectory(p))) {
                for (Path subPath : dirStream) {
                    queue.offer(createFolderModel(subPath, currentFolder));
                }
            }
        }
        return savedFolders;
    }


    private FolderModel createFolderModel(Path path, FolderModel parentFolder) throws IOException {
        FolderModel folder = new FolderModel();
        folder.setLocation(path.toString());
        folder.setFolderSize("" + Files.size(path));
        folder.setSongCount(0);
        folder.setParent(parentFolder);
        return folder;
    }

}
