package com.framgia.music_24.screens.trackoffline;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.base.BasePresenter;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/10.
 */
public interface MusicContract {

    /**
     * View
     */
    interface View {
        void OnGetOfflineSuccess(List<Track> tracks);
        void OnError();
    }

    /**
     * Presenter
     */
    interface Presenter extends BasePresenter<View> {
        void getAllTrack();
    }
}
