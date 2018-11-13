package com.framgia.music_24.screens.navfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.genre.GenreAdapter;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.screens.search.SearchFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.List;
import java.util.Random;

import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_UN_FAVORITE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment
        implements NavigationContract.View, View.OnClickListener, GenreAdapter.OnClickListener,
        NavigationAdapter.OnLongClickListener {

    public static final String TAG = "NavigationFragment";
    private static final String ARGUMENT_TYPE = "ARGUMENT_TYPE";
    public static final int FAVORITE_TYPE = 1;
    private FragmentActivity mContext;
    private ImageView mImageViewBack;
    private ImageView mImageViewPlay;
    private ImageView mImageViewPlayShuffle;
    private ImageView mImageViewSearch;
    private TextView mTextViewTitle;
    private RecyclerView mRecyclerFavorite;
    private List<Track> mTracks;
    private NavigationPresenter mPresenter;
    private static NavigationFragment sInstance;
    private NavigationAdapter mAdapter;

    public NavigationFragment() {
        // Required empty public constructor
    }

    public static NavigationFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_TYPE, type);
        NavigationFragment fragment = new NavigationFragment();
        sInstance = fragment;
        fragment.setArguments(bundle);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        mRecyclerFavorite = view.findViewById(R.id.recycler_offline);
        mImageViewBack = view.findViewById(R.id.imageview_offline_back);
        mImageViewPlay = view.findViewById(R.id.imageview_offline_play);
        mImageViewPlayShuffle = view.findViewById(R.id.imageview_offline_shuffle);
        mTextViewTitle = view.findViewById(R.id.textview_offline);
        mImageViewSearch = view.findViewById(R.id.imageview_offline_search);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        mPresenter = new NavigationPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        mPresenter.setView(this);
        if (getArguments() != null) {
            if (getArguments().getInt(ARGUMENT_TYPE) == FAVORITE_TYPE) {
                mPresenter.getDataFavorite();
                mTextViewTitle.setText(getResources().getString(R.string.title_favorite));
            } else {
                mPresenter.getDataDownload();
                mTextViewTitle.setText(getResources().getString(R.string.title_download));
            }
        }
        setupListener();
        handleBackKey();
    }

    private void setupListener() {
        mImageViewBack.setOnClickListener(this);
        mImageViewPlay.setOnClickListener(this);
        mImageViewPlayShuffle.setOnClickListener(this);
        mImageViewSearch.setOnClickListener(this);
    }

    private void updateFullname(List<Track> tracks) {
        if (tracks != null) {
            for (int i = 0; i < tracks.size(); i++) {
                mPresenter.findTrackById(String.valueOf(tracks.get(i).getId()), i);
            }
        }
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

    @Override
    public void loadData(List<Track> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            updateFullname(tracks);
            mRecyclerFavorite.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new NavigationAdapter(mContext, tracks, this, this);
            mRecyclerFavorite.setAdapter(mAdapter);
        }
    }

    @Override
    public void checkData(Track track, int position) {
        mTracks.get(position).getUser().setUsername(track.getUser().getUsername());
        mTracks.set(position, mTracks.get(position));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_offline_play:
                if (mTracks.size() > 0) {
                    sendDataPlay(mTracks, 0);
                } else {
                    DisplayUtils.makeToast(mContext, getString(R.string.navigation_error));
                }
                break;

            case R.id.imageview_offline_shuffle:
                if (mTracks.size() > 0) {
                    Random random = new Random();
                    sendDataPlay(mTracks, random.nextInt(mTracks.size()));
                } else {
                    DisplayUtils.makeToast(mContext, getString(R.string.navigation_error));
                }
                break;

            case R.id.imageview_offline_back:
                getFragmentManager().beginTransaction().hide(sInstance).commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                break;

            case R.id.imageview_offline_search:
                mContext.getSupportFragmentManager()
                        .beginTransaction()
                        .hide(sInstance)
                        .replace(R.id.frame_main_layout, SearchFragment.newInstance(null, "", 0),
                                SearchFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
        }
    }

    @Override
    public void OnItemClick(List<Track> tracks, int position) {
        mTracks = tracks;
        sendDataPlay(mTracks, position);
    }

    private void sendDataPlay(List<Track> tracks, int position) {
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayMusicFragment.newInstance(tracks, "", position, false, true),
                R.id.coordinator_add_play, PlayMusicFragment.TAG);
    }

    @Override
    public void OnFavoriteClick(List<Track> tracks, int position) {

    }

    @Override
    public void OnLongClick(int position) {
        if(getArguments().getInt(ARGUMENT_TYPE) == FAVORITE_TYPE) {
            showAlertDialog(mTracks.get(position), position);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showAlertDialog(final Track track, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.favorite_dialog_title));
        builder.setMessage(getResources().getString(R.string.favorite_dialog_messsage));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.favorite_dialog_possitive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.editFavorite(track, PLAY_UN_FAVORITE);
                mTracks.remove(position);
                mAdapter.notifyDataSetChanged();
                DisplayUtils.makeToast(mContext, getString(R.string.favorite_favorite_remove));
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.favorite_dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
