package com.framgia.music_24.data.source;

import com.framgia.music_24.data.model.Playlist;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/10.
 */
public interface PlaylistDataSource {
    void addPlaylist(Playlist playlist);

    List<Playlist> getAllPlaylist();

    void removePlayList(Playlist playlist);
}
