package com.framgia.music_24.screens.search;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.base.BasePresenter;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/08.
 */
public interface SearchContract {

    /**
     * View
     */

    interface View {
        void searchSuccess(List<Track> tracks);

        void onGetDataError(Exception e);

        void showProgress();

        void hideProgress();

        void loadMore(List<Track> tracks);
    }

    /**
     * Presenter
     */

    interface Presenter extends BasePresenter<View> {
        void searchData(String query, int limit);
    }
}
