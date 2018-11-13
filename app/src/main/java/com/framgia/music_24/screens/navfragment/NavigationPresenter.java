package com.framgia.music_24.screens.navfragment;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;

/**
 * Created by CuD HniM on 18/09/08.
 */
public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.View mView;
    private TracksRepository mRepository;

    public NavigationPresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(NavigationContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void getDataFavorite() {
        mView.loadData(mRepository.getAllFavorite());
    }

    @Override
    public void getDataDownload() {
        mView.loadData(mRepository.getAllDownload());
    }

    @Override
    public void findTrackById(String id, int position) {
        mView.checkData(mRepository.findTrackById(id), position);
    }

    @Override
    public void editFavorite(Track track, int fav) {
        mRepository.updateFavorite(track, fav);
    }
}
