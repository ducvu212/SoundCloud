package com.framgia.music_24.screens.main;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.CallBack;
import java.util.List;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private TracksRepository mRepository;

    public MainPresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void saveTrackPlayingData(Track track, String url, String type) {
        mRepository.saveTrackPlayingData(track, url, type);
    }

    @Override
    public boolean isExistRow(Track track) {
        return mRepository.isExistRow(track);
    }

    @Override
    public String getTrackUrl() {
        return mRepository.getTrackUrl();
    }

    @Override
    public String getTrackType() {
        return mRepository.getTrackType();
    }

    @Override
    public int getTrackId() {
        return mRepository.getTrackID();
    }

    @Override
    public void searchData(String query) {
        mRepository.search(query, 10, new CallBack<List<Track>>() {
            @Override
            public void onSuccess(List<Track> datas) {
                mView.onSearchSuccess(datas);
            }

            @Override
            public void onError(Exception e) {
                mView.onSearchError(e);
            }

            @Override
            public void onNetWorkError() {

            }
        });
    }
}
