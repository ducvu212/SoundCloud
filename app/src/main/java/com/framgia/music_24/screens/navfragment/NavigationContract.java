package com.framgia.music_24.screens.navfragment;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.base.BasePresenter;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/08.
 */
public interface NavigationContract {

    /**
     * View
     */

    interface View {
        void loadData(List<Track> tracks);

        void checkData(Track track, int position);
    }

    /**
     * Presenter
     */

    interface Presenter extends BasePresenter<View> {
        void getDataFavorite();

        void getDataDownload();

        void findTrackById(String id, int position);

        void editFavorite(Track track, int fav);
    }
}
