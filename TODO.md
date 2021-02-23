TASKS:
+ (2021-02-23) (1h) - Change a Stitch Icon with Rose.
+ (2021-02-23) (1h) - Update last played time and played count.

+ (2021-02-23) (15m) - Add playlist Last played
+ (2021-02-23) (15m) - Add playlist Most played
+ (2021-02-23) (15m) - Add playlist Recently added
+ (2021-02-23) (15m) - Add playlist Most rated
    • refactor dynamic lists
    • implement Service to select song by criteria for dynamic lists
    • implement Service to select playlist songs
-                   Add a boolean column to songs table "Missed"
-                   Add dynamic List “Missed songs” under "Local Music" label

- Implement Splitter between SideBar and Table view.
- Implement scaning directories in separate thread to not hang up UI. 
- Implement gstream to play OPUS files.
- Implement jaudiotagger to read OPUS tags.
- Add context menu on a playlist (delete playlist)
- Add context menu on a song in a table view  (
    Add to playlist,
    Remove from playlist, 
    Properties, 
    Browse this Genre, 
    Browse this Artist, 
    Browse this Album,
    Browse this Folder,
    View in Files)
- Implement update rating.


- BUG: View full song name and artist on current play song.

- Display a current directory name for the played song with a total song count above the table list.
- Implement [Folders], [Artists], [Albums] filter with songs count above the table view.
- Implement search button, search field and dynamic filter.

ENHANCEMENTS:
- Implement Tags from file path dialog
- Implement Rename files dialog.

MINOR:
- Display in the status a song name, artist and album on a mouse hover in the list.
- Highlight lyrics button in the bottom right corner if song has lyrics.
- Implement a dynamic list dialog for adding dynamic playlist.
