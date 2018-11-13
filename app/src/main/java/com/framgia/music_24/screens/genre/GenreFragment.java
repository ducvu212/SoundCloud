package com.framgia.music_24.screens.genre;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.EndlessScrollListener;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.screens.discover.DiscoverFragment.ARGUMENT_POSITION_ITEM;
import static com.framgia.music_24.screens.discover.DiscoverFragment.ARGUMENT_TITLE_ITEM;
import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_FAVORITE;
import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_UN_FAVORITE;
import static com.framgia.music_24.utils.Constants.ARROW;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreFragment extends Fragment
        implements GenreContract.View, GenreAdapter.OnClickListener {

    public static final String TAG = "GenreDetails";
    public static final String[] mGenres = new String[] {
            "all-music", "all-audio", "alternativerock", "ambient", "classical", "country"
    };
    public static final int LIMIT_PER_CALL = 10;
    public static final int NUMBER_ONE = 1;
    private FragmentActivity mContext;
    private GenreContract.Presenter mPresenter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerGenre;
    private List<Track> mTracks;
    private GenreAdapter mAdapter;
    private Discover mDiscover;
    private String mGenre;
    private int mLimit = 10;
    private int mPosition;

    public static GenreFragment newInstance(int position, Discover discover) {
        Bundle args = new Bundle();
        GenreFragment fragment = new GenreFragment();
        args.putInt(ARGUMENT_POSITION_ITEM, position);
        args.putParcelable(ARGUMENT_TITLE_ITEM, discover);
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

    public GenreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new GenrePresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        mPresenter.setView(this);
        initComponents();
        addTracks(mTracks);
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void setupData(List<Track> tracks) {
        mTracks = tracks;
        if (mTracks != null) {
            setFavorite(mTracks);
        }
        setupRecycleView();
    }

    @Override
    public void onGetDataError(Exception e) {
        DisplayUtils.makeToast(mContext, e.toString());
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadMore(List<Track> tracks) {
        mTracks.remove(mTracks.size() - NUMBER_ONE);
        mAdapter.notifyItemRemoved(mTracks.size());
        tracks.subList(0, mTracks.size()).clear();
        setFavorite(tracks);
        mTracks.addAll(tracks);
        mAdapter.notifyDataSetChanged();
        addTracks(mTracks);
    }

    private void setFavorite(List<Track> tracks) {
        if (tracks != null) {
            for (int i = 0; i < tracks.size(); i++) {
                mPresenter.findTrackById(String.valueOf(tracks.get(i).getId()), i);
            }
        }
    }

    @Override
    public void initData(Track track, int position) {
        if (track != null) {
            mTracks.get(position).setFavorite(track.getFavorite());
            mTracks.set(position, mTracks.get(position));
        }
    }

    @Override
    public void OnItemClick(List<Track> tracks, int position) {
        mContext.getSupportFragmentManager()
                .popBackStack(PlayMusicFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayMusicFragment.newInstance(mTracks, mGenres[mPosition], position, false, false),
                R.id.coordinator_add_play, PlayMusicFragment.TAG);
    }

    @Override
    public void OnFavoriteClick(List<Track> tracks, int position) {
        setFavorite(tracks, position);
    }

    private void setupRecycleView() {
        mRecyclerGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GenreAdapter(getContext(), mTracks, this);
        mRecyclerGenre.setAdapter(mAdapter);
        mRecyclerGenre.addOnScrollListener(
                new EndlessScrollListener((LinearLayoutManager) mRecyclerGenre.getLayoutManager()) {
                    @Override
                    public void OnLoadMore() {
                        loadMore();
                    }
                });
    }

    private void addTracks(List<Track> tracks) {
        for (Track track : tracks) {
            if (!mPresenter.isExistRow(track)) {
                mPresenter.addTracks(track);
            }
        }
    }

    private void initViews(View view) {
        mProgressBar = view.findViewById(R.id.progress_genre);
        mRecyclerGenre = view.findViewById(R.id.recycler_gender);
    }

    private void initComponents() {
        mTracks = new ArrayList<>();
        getDataFromActivity();
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mGenre);
        }
    }

    private void getDataFromActivity() {
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARGUMENT_POSITION_ITEM);
            mDiscover = getArguments().getParcelable(ARGUMENT_TITLE_ITEM);
            mGenre = mDiscover.getGender().replace(ARROW, "");
            mPresenter.loadDataGenre(mDiscover.getType(), mGenre, mLimit);
        }
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

    private void loadMore() {
        mTracks.add(null);
        mLimit += LIMIT_PER_CALL;
        mAdapter.notifyItemInserted(mTracks.size() - NUMBER_ONE);
        mPresenter.loadDataGenre(mDiscover.getType(), "", mLimit);
    }
}
