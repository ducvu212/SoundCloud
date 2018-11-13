package com.framgia.music_24.screens.genre;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.CallBack;
import java.util.List;

/**
 * Created by CuD HniM on 18/08/28.
 */
public class GenrePresenter implements GenreContract.Presenter {

    private GenreContract.View mView;
    private TracksRepository mRepository;

    GenrePresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(GenreContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void loadDataGenre(final String genre, final String genreTitle, int limit) {
        mRepository.getTrack(genre, limit, new CallBack<List<Track>>() {
            @Override
            public void onSuccess(List<Track> datas) {
                if (!genreTitle.equals("")) {
                    mView.setupData(datas);
                    mView.hideProgress();
                } else {
                    mView.loadMore(datas);
                }
            }

            @Override
            public void onError(Exception e) {
                mView.onGetDataError(e);
            }

            @Override
            public void onNetWorkError() {

            }
        });
    }

    @Override
    public void editFavorite(Track track, int fav) {
        mRepository.updateFavorite(track, fav);
    }

    @Override
    public void addTracks(Track track) {
        mRepository.addTrack(track);
    }

    @Override
    public boolean isExistRow(Track track) {
        return mRepository.isExistRow(track);
    }

    @Override
    public void findTrackById(String id, int position) {
        mView.initData(mRepository.findTrackById(id), position);
    }
}
