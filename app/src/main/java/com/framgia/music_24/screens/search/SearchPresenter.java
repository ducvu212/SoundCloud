package com.framgia.music_24.screens.search;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.CallBack;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/08.
 */
public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;
    private TracksRepository mRepository;

    public SearchPresenter(TracksRepository repository) {
        mRepository = repository;
    }

    @Override
    public void setView(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void searchData(String query, int limit) {
        mRepository.search(query, limit, new CallBack<List<Track>>() {
            @Override
            public void onSuccess(List<Track> datas) {
                if (datas.size() == 10) {
                    mView.searchSuccess(datas);
                    mView.hideProgress();
                } else {
                    mView.hideProgress();
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
}
