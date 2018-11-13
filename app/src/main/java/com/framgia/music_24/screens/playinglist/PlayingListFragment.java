package com.framgia.music_24.screens.playinglist;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.discover.DiscoverFragment;
import com.framgia.music_24.screens.genre.GenreAdapter;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_FAVORITE;
import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_UN_FAVORITE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayingListFragment extends Fragment
        implements PlayingListContract.View, GenreAdapter.OnClickListener, View.OnClickListener {

    public static final String TAG = "PlayingListFragment";
    private static final String ARGUMENT_LIST_PLAYING = "ARGUMENT_LIST_PLAYING";
    private static final String ARGUMENT_PLAYING_TRACK = "ARGUMENT_PLAYING_TRACK";
    private static final String ARGUMENT_PLAYING_TYPE = "ARGUMENT_PLAYING_TYPE";
    private PlayingListContract.Presenter mPresenter;
    private FragmentActivity mContext;
    private String mType;
    private ImageView mImageViewBack;
    private RecyclerView mRecyclerPlaying;
    private List<Track> mTracks;
    private String mName;
    private PlayingListAdapter mAdapter;

    public PlayingListFragment() {
        // Required empty public constructor
    }

    public static PlayingListFragment newInstance(List<Track> tracks, String type, String name) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PLAYING_TRACK, name);
        args.putString(ARGUMENT_PLAYING_TYPE, type);
        args.putParcelableArrayList(ARGUMENT_LIST_PLAYING,
                (ArrayList<? extends Parcelable>) tracks);
        PlayingListFragment fragment = new PlayingListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playing_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        mPresenter = new PlayingListPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        mPresenter.setView(this);
        mTracks = new ArrayList<>();
        loadData();
        mImageViewBack.setOnClickListener(this);
        handleBackKey();
    }

    private void handleBackKey() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mContext.getSupportFragmentManager()
                            .popBackStack(PlayingListFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Fragment fragment = mContext.getSupportFragmentManager().findFragmentByTag(PlayMusicFragment.TAG);
                    if (fragment != null) {
                        mContext.getSupportFragmentManager().beginTransaction()
                                .hide(mContext.getSupportFragmentManager().findFragmentByTag(DiscoverFragment.TAG))
                                .show(fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    } else {
                        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                                PlayMusicFragment.newInstance(null, mType, 0, true, false),
                                R.id.coordinator_add_play, PlayMusicFragment.TAG);
                    }
                    mContext.onBackPressed();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    return true;
                }
                return true;
            }
        });
    }

    private void initViews(View view) {
        mRecyclerPlaying = view.findViewById(R.id.recycler_play_list);
        mImageViewBack = view.findViewById(R.id.imageview_playlist_back);
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    public void loadData() {
        if (getArguments() != null) {
            mTracks = getArguments().getParcelableArrayList(ARGUMENT_LIST_PLAYING);
            updateFavorite(mTracks);
            mName = getArguments().getString(ARGUMENT_PLAYING_TRACK);
            mType = getArguments().getString(ARGUMENT_PLAYING_TYPE);
            mRecyclerPlaying.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new PlayingListAdapter(getContext(), mTracks, mName, this);
            mRecyclerPlaying.setAdapter(mAdapter);
        }
    }

    private void updateFavorite(List<Track> tracks) {
        for (int i = 0; i < tracks.size(); i++) {
            mPresenter.findTrackById(String.valueOf(tracks.get(i).getId()), i);
        }
    }

    @Override
    public void OnItemClick(List<Track> tracks, int position) {
        mContext.getSupportFragmentManager()
                .popBackStack(PlayingListFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayMusicFragment.newInstance(tracks, mType, position, false, false),
                R.id.coordinator_add_play, PlayMusicFragment.TAG);
    }

    @Override
    public void OnFavoriteClick(List<Track> tracks, int position) {
        setFavorite(tracks, position);
    }

    private void setFavorite(List<Track> tracks, int position) {
        Track track = tracks.get(position);
        if (track.getFavorite() == PLAY_UN_FAVORITE) {
            setUiFavorite(position, PLAY_FAVORITE, getString(R.string.play_favorite));
        } else {
            setUiFavorite(position, PLAY_UN_FAVORITE, getString(R.string.play_un_favorite));
        }
    }

    private void setUiFavorite(int position, int fav, String toast) {
        Track track = mTracks.get(position);
        track.setFavorite(fav);
        mPresenter.editFavorite(mTracks.get(position), fav);
        mTracks.set(position, track);
        mAdapter.notifyDataSetChanged();
        DisplayUtils.makeToast(mContext, toast);
    }

    @Override
    public void onClick(View view) {
        mContext.getSupportFragmentManager()
                .popBackStack(PlayingListFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment fragment = mContext.getSupportFragmentManager().findFragmentByTag(PlayMusicFragment.TAG);
        if (fragment != null) {
            mContext.getSupportFragmentManager().beginTransaction()
                    .hide(mContext.getSupportFragmentManager().findFragmentByTag(DiscoverFragment.TAG))
                    .show(fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                    PlayMusicFragment.newInstance(null, mType, 0, true, false),
                    R.id.coordinator_add_play, PlayMusicFragment.TAG);
        }
        mContext.onBackPressed();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void initData(Track track, int position) {
        mTracks.get(position).setFavorite(track.getFavorite());
        mTracks.set(position, mTracks.get(position));
    }
}
