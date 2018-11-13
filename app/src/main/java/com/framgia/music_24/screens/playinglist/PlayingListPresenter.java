package com.framgia.music_24.screens.playinglist;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;

/**
 * Created by CuD HniM on 18/09/07.
 */
public class PlayingListPresenter implements PlayingListContract.Presenter {

    private PlayingListContract.View mView;
    private TracksRepository mRepository;

    public PlayingListPresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(PlayingListContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void editFavorite(Track track, int fav) {
        mRepository.updateFavorite(track, fav);
    }

    @Override
    public void findTrackById(String id, int position) {
        mView.initData(mRepository.findTrackById(id), position);
    }
}
