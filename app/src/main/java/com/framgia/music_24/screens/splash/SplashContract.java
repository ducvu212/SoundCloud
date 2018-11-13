package com.framgia.music_24.screens.splash;

import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.screens.base.BasePresenter;
import java.util.List;

/**
 * Created by CuD HniM on 18/08/24.
 */
public interface SplashContract {

    /**
     * View
     */

    interface View {
        void sendGenreData(List<Discover> discovers);

        void onGetDataError(Exception e);
    }

    /**
     * Presenter
     */

    interface Presenter extends BasePresenter<View> {
        void loadDataGenre(String genre, String genreTitle, String type, List<Discover> discovers);
    }
}
