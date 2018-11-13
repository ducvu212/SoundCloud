package com.framgia.music_24.screens.playinglist;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.base.BasePresenter;

/**
 * Created by CuD HniM on 18/09/07.
 */
public interface PlayingListContract {

    /**
     * View
     */

    interface View {
        void initData(Track track, int position);
    }

    /**
     * Presenter
     */

    interface Presenter extends BasePresenter<View> {
        void editFavorite(Track track, int fav);

        void findTrackById(String id, int position);
    }
}
