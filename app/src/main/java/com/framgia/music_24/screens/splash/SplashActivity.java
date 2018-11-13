package com.framgia.music_24.screens.splash;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.screens.genre.GenreFragment.mGenres;
import static com.framgia.music_24.screens.main.MainActivity.getProfileIntent;
import static com.framgia.music_24.service.MusicService.EXTRA_ART_URL;
import static com.framgia.music_24.service.MusicService.EXTRA_NAME_TRACK;
import static com.framgia.music_24.service.MusicService.EXTRA_NOTIFY_INTENT;
import static com.framgia.music_24.utils.Constants.ARROW;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_ALL_AUDIO;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_ALL_MUSIC;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_ALTERNATIVE_ROCK;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_AMBIENT;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_CLASSICAL;
import static com.framgia.music_24.utils.Constants.QUERY_GENRE_COUNTRY;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    public static final String EXTRA_GENRE = "com.framgia.music_24.extras.EXTRA_GENRE";
    private SplashContract.Presenter mPresenter;
    private boolean mIsNotify;
    private String mArtUrl;
    private String mNameTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(this, R.color.color_deep_orange_accent_400));
        }
        setContentView(R.layout.activity_splash);
        mIsNotify = getIntent().getBooleanExtra(EXTRA_NOTIFY_INTENT, false);
        mArtUrl = getIntent().getStringExtra(EXTRA_ART_URL);
        mNameTrack = getIntent().getStringExtra(EXTRA_NAME_TRACK);
        initComponents();
    }

    private void initComponents() {
        mPresenter = new SplashPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(this,
                                TrackDatabaseHelper.getInstance(this))));
        mPresenter.setView(this);
        getDataGenre();
    }

    private void getDataGenre() {
        List<Discover> discovers = new ArrayList<>();
        mPresenter.loadDataGenre(QUERY_GENRE_ALL_MUSIC,
                getString(R.string.discover_all_music).concat(ARROW), mGenres[0], discovers);
        mPresenter.loadDataGenre(QUERY_GENRE_ALL_AUDIO,
                getString(R.string.discover_all_audio).concat(ARROW), mGenres[1], discovers);
        mPresenter.loadDataGenre(QUERY_GENRE_ALTERNATIVE_ROCK,
                getString(R.string.discover_alternative_rock).concat(ARROW), mGenres[2], discovers);
        mPresenter.loadDataGenre(QUERY_GENRE_AMBIENT,
                getString(R.string.discover_ambient).concat(ARROW), mGenres[3], discovers);
        mPresenter.loadDataGenre(QUERY_GENRE_CLASSICAL,
                getString(R.string.discover_classical).concat(ARROW), mGenres[4], discovers);
        mPresenter.loadDataGenre(QUERY_GENRE_COUNTRY,
                getString(R.string.discover_country).concat(ARROW), mGenres[5], discovers);
    }

    @Override
    protected void onStart() {
        mPresenter.onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void sendGenreData(List<Discover> discovers) {
        startActivity(getProfileIntent(this, discovers, mIsNotify, mArtUrl, mNameTrack));
        finish();
    }

    @Override
    public void onGetDataError(Exception e) {
        DisplayUtils.makeToast(this, e.toString());
    }
}
