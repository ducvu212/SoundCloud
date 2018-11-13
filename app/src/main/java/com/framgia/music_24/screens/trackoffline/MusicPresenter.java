package com.framgia.music_24.screens.trackoffline;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.TracksDataSource;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class MusicPresenter implements MusicContract.Presenter {

    private MusicContract.View mView;
    private TracksRepository mRepository;

    public MusicPresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(MusicContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void getAllTrack() {
        mRepository.getAllOfflineTrack(new TracksDataSource
                .TrackLocalDataSource.OnGetOfflineTrackListener() {
            @Override
            public void OnSuccess(List<Track> tracks) {
                mView.OnGetOfflineSuccess(tracks);
            }

            @Override
            public void OnError() {
                mView.OnError();
            }
        });
    }
}
