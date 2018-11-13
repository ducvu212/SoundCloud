package com.framgia.music_24.screens.splash;

import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.CallBack;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_COUNTRY;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class SplashPresenter implements SplashContract.Presenter {

    private SplashContract.View mView;
    private TracksRepository mTracksRepository;

    SplashPresenter(TracksRepository repository) {
        mTracksRepository = checkNotNull(repository);
    }

    @Override
    public void setView(SplashContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void loadDataGenre(final String genre, final String genderTitle, final String type,
            final List<Discover> datas) {
        mTracksRepository.getTrack(genre, genderTitle, type, new CallBack<List<Discover>>() {
            @Override
            public void onSuccess(List<Discover> discovers) {
                datas.addAll(discovers);
                if (genre.equals(QUERY_GENRE_COUNTRY)) {
                    mView.sendGenreData(datas);
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
