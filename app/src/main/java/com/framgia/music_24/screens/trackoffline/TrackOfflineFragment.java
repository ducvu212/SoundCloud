package com.framgia.music_24.screens.trackoffline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.screens.search.SearchFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackOfflineFragment extends Fragment
        implements MusicContract.View, View.OnClickListener, MusicAdapter.OnItemClick {

    public static final String TAG = "TrackOfflineFragment";
    private MusicContract.Presenter mPresenter;
    private FragmentActivity mContext;
    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private ImageView mImageViewBack;
    private ImageView mImageViewPlay;
    private ImageView mImageViewPlayShuffle;
    private ImageView mImageViewSearch;
    private List<Track> mTracks;
    private ProgressBar mProgressBar;
    private static TrackOfflineFragment sInstance;

    public TrackOfflineFragment() {
        // Required empty public constructor
    }

    public static TrackOfflineFragment newInstance() {
        Bundle args = new Bundle();
        sInstance = new TrackOfflineFragment();
        sInstance.setArguments(args);
        return sInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void handleBackKey() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().beginTransaction().hide(sInstance).commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    return true;
                }
                return true;
            }
        });
    }

    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_offline);
        mImageViewBack = view.findViewById(R.id.imageview_offline_back);
        mImageViewPlay = view.findViewById(R.id.imageview_offline_play);
        mImageViewPlayShuffle = view.findViewById(R.id.imageview_offline_shuffle);
        mImageViewSearch = view.findViewById(R.id.imageview_offline_search);
        mProgressBar = view.findViewById(R.id.progress_bar_offline);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        mPresenter = new MusicPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        mPresenter.setView(this);
        handleBackKey();
        mTracks = new ArrayList<>();
        mPresenter.getAllTrack();
        setupListener();
    }

    private void setupListener() {
        mImageViewSearch.setOnClickListener(this);
        mImageViewPlayShuffle.setOnClickListener(this);
        mImageViewPlay.setOnClickListener(this);
        mImageViewBack.setOnClickListener(this);
    }

    @Override
    public void OnGetOfflineSuccess(List<Track> tracks) {
        mProgressBar.setVisibility(View.GONE);
        mTracks = tracks;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MusicAdapter(mContext, mTracks, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void OnError() {
        mProgressBar.setVisibility(View.GONE);
        DisplayUtils.makeToast(mContext, getString(R.string.navigation_error));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_offline_back:
                getFragmentManager().beginTransaction().hide(sInstance).commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                break;

            case R.id.imageview_offline_play:
                if (mTracks.size() > 0) {
                    sendDataPlay(mTracks, 0, false);
                } else {
                    DisplayUtils.makeToast(mContext, getString(R.string.navigation_error));
                }
                break;

            case R.id.imageview_offline_search:
                mContext.getSupportFragmentManager()
                        .beginTransaction()
                        .hide(sInstance)
                        .replace(R.id.frame_main_layout, SearchFragment.newInstance(null, "", 0),
                                SearchFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            case R.id.imageview_offline_shuffle:
                if (mTracks.size() > 0) {
                    Random random = new Random();
                    sendDataPlay(mTracks, random.nextInt(mTracks.size()), true);
                } else {
                    DisplayUtils.makeToast(mContext, getString(R.string.navigation_error));
                }
                break;
        }
    }

    @Override
    public void OnItemOfflineClick(List<Track> tracks, int position) {
        mTracks = tracks;
        sendDataPlay(mTracks, position, false);
    }

    private void sendDataPlay(List<Track> tracks, int position, boolean isShuffle) {
        mContext.getSupportFragmentManager()
                .popBackStack(PlayMusicFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayMusicFragment.newInstance(tracks, "", position, false, isShuffle),
                R.id.coordinator_add_play, PlayMusicFragment.TAG);
    }
}
