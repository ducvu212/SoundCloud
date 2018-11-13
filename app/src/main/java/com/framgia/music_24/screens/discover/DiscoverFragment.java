package com.framgia.music_24.screens.discover;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.discover.adapter.DiscoverAdapter;
import com.framgia.music_24.screens.discover.adapter.TrackAdapter;
import com.framgia.music_24.screens.genre.GenreFragment;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.utils.Constants.ARGUMENT_GENRE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment
        implements DiscoverContract.View, DiscoverAdapter.OnClickListener,
        TrackAdapter.OnItemClickListener {

    public static final String TAG = "DiscoverFragment";
    public static final String ARGUMENT_POSITION_ITEM = "ARGUMENT_POSITION_ITEM";
    public static final String ARGUMENT_TITLE_ITEM = "ARGUMENT_TITLE_ITEM";
    private FragmentActivity mContext;
    private DiscoverContract.Presenter mPresenter;
    private RecyclerView mRecyclerAllGenders;
    private List<Discover> mDiscovers;

    public DiscoverFragment() {

    }

    public static DiscoverFragment newInstance(List<Discover> discovers) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARGUMENT_GENRE, (ArrayList<? extends Parcelable>) discovers);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = (FragmentActivity) activity;
        super.onAttach(activity);
        if (((AppCompatActivity) mContext).getSupportActionBar() != null) {
            ((AppCompatActivity) mContext).getSupportActionBar().show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initComponents();
    }

    private void initViews(View view) {
        mRecyclerAllGenders = view.findViewById(R.id.recycler_gender);
    }

    private void initComponents() {
        mDiscovers = new ArrayList<>();
        if (getArguments() != null) {
            mDiscovers = getArguments().getParcelableArrayList(ARGUMENT_GENRE);
        }
        setupRecycleView();
    }

    private void setupRecycleView() {
        DiscoverAdapter adapter = new DiscoverAdapter(getContext(), mDiscovers, this, this);
        mRecyclerAllGenders.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAllGenders.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new DiscoverPresenter();
        mPresenter.setView(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void OnGenreClick(int position, String genre) {
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                GenreFragment.newInstance(position, mDiscovers.get(position)), R.id.frame_discover, GenreFragment.TAG);
    }

    @Override
    public void OnTrackClick(List<Track> tracks, String type, int position) {
        mContext.getSupportFragmentManager().popBackStack (PlayMusicFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);;
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayMusicFragment.newInstance(tracks, type, position, false, false),
                R.id.coordinator_add_play, PlayMusicFragment.TAG);
    }
}
