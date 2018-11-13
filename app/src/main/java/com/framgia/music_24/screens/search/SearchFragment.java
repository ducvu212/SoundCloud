package com.framgia.music_24.screens.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.EndlessScrollListener;
import com.framgia.music_24.screens.genre.GenreAdapter;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.screens.genre.GenreFragment.LIMIT_PER_CALL;
import static com.framgia.music_24.screens.genre.GenreFragment.NUMBER_ONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment
        implements SearchContract.View, GenreAdapter.OnClickListener {

    public static final String TAG = "SearchFragment";
    private static final String ARGUMENT_SEARCH_LIST = "ARGUMENT_SEARCH_LIST";
    private static final String ARGUMENT_QUERY = "ARGUMENT_QUERY";
    private static final String ARGUMENT_LIMIT = "ARGUMENT_LIMIT";
    private RecyclerView mRecyclerSearch;
    private SearchAdapter mAdapter;
    private List<Track> mTracks;
    private SearchContract.Presenter mPresenter;
    private FragmentActivity mContext;
    private int mLimit;
    private String mQuery;
    private ProgressBar mProgressBar;
    private static SearchFragment sInstance;
    private FragmentManager mManager;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(List<Track> tracks, String query, int limit) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGUMENT_SEARCH_LIST, (ArrayList<? extends Parcelable>) tracks);
        args.putString(ARGUMENT_QUERY, query);
        args.putInt(ARGUMENT_LIMIT, limit);
        sInstance = new SearchFragment();
        sInstance.setArguments(args);
        return sInstance;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
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
                    DisplayUtils.hideFragment(mManager, sInstance);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    return true;
                }
                return true;
            }
        });
    }

    private void initViews(View view) {
        mRecyclerSearch = view.findViewById(R.id.recycle_search);
        mProgressBar = view.findViewById(R.id.search_progress);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        mPresenter = new SearchPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        mPresenter.setView(this);
        mManager = mContext.getSupportFragmentManager();
        handleBackKey();
        getDataSearch();
    }

    private void getDataSearch() {
        if (getArguments() != null) {
            mTracks = getArguments().getParcelableArrayList(ARGUMENT_SEARCH_LIST);
            mQuery = getArguments().getString(ARGUMENT_QUERY);
            mLimit = getArguments().getInt(ARGUMENT_LIMIT);
            mRecyclerSearch.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new SearchAdapter(getContext(), mTracks, this);
            mRecyclerSearch.setAdapter(mAdapter);
            mRecyclerSearch.addOnScrollListener(new EndlessScrollListener(
                    (LinearLayoutManager) mRecyclerSearch.getLayoutManager()) {
                @Override
                public void OnLoadMore() {
                    loadMore();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void OnItemClick(List<Track> tracks, int position) {
        DisplayUtils.popFragmentBackstack(mManager,
                PlayMusicFragment.TAG);
    }

    @Override
    public void OnFavoriteClick(List<Track> tracks, int position) {

    }

    private void loadMore() {
        mTracks.add(null);
        mLimit += LIMIT_PER_CALL;
        mAdapter.notifyItemInserted(mTracks.size() - NUMBER_ONE);
        mPresenter.searchData(mQuery, mLimit);
    }

    @Override
    public void searchSuccess(List<Track> tracks) {

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
        mTracks.addAll(tracks);
        mAdapter.notifyDataSetChanged();
    }
}
