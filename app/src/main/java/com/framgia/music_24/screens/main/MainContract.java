package com.framgia.music_24.screens.main;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.base.BasePresenter;
import java.util.List;

public interface MainContract {

    /**
     * Interface for View
     */

    interface View {
        void onSearchSuccess(List<Track> tracks);

        void onSearchError(Exception e);
    }

    /**
     * Interface for Presenter
     */

    interface Presenter extends BasePresenter<View>{
        void saveTrackPlayingData(Track track, String url, String type);

        boolean isExistRow(Track track);

        String getTrackUrl();

        String getTrackType();

        int getTrackId();

        void searchData(String query);
    }

}
