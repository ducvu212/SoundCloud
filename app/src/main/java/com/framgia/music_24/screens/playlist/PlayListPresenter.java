package com.framgia.music_24.screens.playlist;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class PlayListPresenter implements PlayListContract.Presenter {

    private PlayListContract.View mView;

    @Override
    public void setView(PlayListContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
