package com.framgia.music_24.screens.main;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.screens.discover.DiscoverFragment;
import com.framgia.music_24.screens.navfragment.NavigationFragment;
import com.framgia.music_24.screens.play.PlayMusicFragment;
import com.framgia.music_24.screens.search.SearchFragment;
import com.framgia.music_24.screens.trackoffline.TrackOfflineFragment;
import com.framgia.music_24.service.MusicService;
import com.framgia.music_24.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.framgia.music_24.data.source.remote.TracksRemoteDataSource.buildStreamUrl;
import static com.framgia.music_24.screens.genre.GenreFragment.LIMIT_PER_CALL;
import static com.framgia.music_24.screens.navfragment.NavigationFragment.FAVORITE_TYPE;
import static com.framgia.music_24.screens.play.PlayMusicFragment.ACTION_UPDATE_BUTTON;
import static com.framgia.music_24.screens.play.PlayMusicFragment.EXTRA_UPDATE_BUTTON;
import static com.framgia.music_24.screens.play.PlayMusicFragment.sBitmapTrack;
import static com.framgia.music_24.screens.splash.SplashActivity.EXTRA_GENRE;
import static com.framgia.music_24.service.MusicService.EXTRA_ART_URL;
import static com.framgia.music_24.service.MusicService.EXTRA_NAME_TRACK;
import static com.framgia.music_24.service.MusicService.EXTRA_NOTIFY_INTENT;
import static com.framgia.music_24.utils.SearchViewAnimate.animateSearchToolbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener,
        View.OnClickListener {

    private static final int NUM_OF_MENU_ICON = 1;
    private static final long WAITING_TIME = 500;
    public static final String ACTION_OPEN_PLAY_SCREEN = "ACTION_OPEN_PLAY_SCREEN";
    private static final String EXTRA_TRACK = "EXTRA_TRACK";
    private static final String EXTRA_SERVICE_RUNNING = "EXTRA_SERVICE_RUNNING";
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int DOWNLOAD_TYPE = 0;
    private static final String EXTRA_OFF_LINE = "EXTRA_OFF_LINE";
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private MainContract.Presenter mPresenter;
    private MenuItem mSearchItem;
    private BroadcastReceiver mBroadcast;
    private BroadcastReceiver mBroadcastButton;
    private ImageView mImageViewAvatar;
    private ImageView mImageViewNext;
    private ImageView mImageViewPrevious;
    private ImageView mImageViewPause;
    private TextView mTextViewName;
    private ConstraintLayout mConstraintLayout;
    private FrameLayout mFrameLayoutMain;
    private boolean mIsServiceRunning;
    private String mType;
    private String mQuery;
    private List<Discover> mDiscovers;
    private int mId;
    private Track mTrack;
    private FragmentManager mManager;
    private MusicService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private CountDownTimer mCountDownTimer = new CountDownTimer(WAITING_TIME, WAITING_TIME) {

        public void onTick(long millisUntilFinished) {
            // no ops
        }

        public void onFinish() {
            mPresenter.searchData(mQuery);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        if (!mIsServiceRunning) {
            String trackUrl = mPresenter.getTrackUrl();
            mType = mPresenter.getTrackType();
            mId = mPresenter.getTrackId();
            if (mTrack != null) {
                mTrack.setId(mId);
            }
        }
        if (getIntent().getBooleanExtra(EXTRA_NOTIFY_INTENT, false)) {
            mIsServiceRunning = true;
            String artUrl = getIntent().getStringExtra(EXTRA_ART_URL);
            String nameTrack = getIntent().getStringExtra(EXTRA_NAME_TRACK);
            updateUiBottom(new Track.TrackBuilder().ArtworkUrl(artUrl).Title(nameTrack).build(),
                    false);
            connectService();
        }
        setupUi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_primary));
        }
        setContentView(R.layout.activity_main);
        initViews();
        initComponents();
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        // Called when SearchView is collapsing
        if (mSearchItem.isActionViewExpanded()) {
            animateSearchToolbar(getApplicationContext(), NUM_OF_MENU_ICON, false, false, mToolbar,
                    mDrawerLayout);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        // Called when SearchView is expanding
        animateSearchToolbar(getApplicationContext(), NUM_OF_MENU_ICON, true, true, mToolbar,
                mDrawerLayout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSupportActionBar().hide();
        int id = item.getItemId();
        int layoutId = 0;
        String tag = null;
        android.support.v4.app.Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                getSupportActionBar().show();
                fragment = DiscoverFragment.newInstance(mDiscovers);
                tag = DiscoverFragment.TAG;
                break;

            case R.id.nav_down_load:
                fragment = NavigationFragment.newInstance(DOWNLOAD_TYPE);
                tag = NavigationFragment.TAG;
                break;

            case R.id.nav_favorite:
                fragment = NavigationFragment.newInstance(FAVORITE_TYPE);
                tag = NavigationFragment.TAG;
                break;

            case R.id.nav_search:
                fragment = SearchFragment.newInstance(null, "", 10);
                tag = SearchFragment.TAG;
                break;

            case R.id.nav_music:
                tag = TrackOfflineFragment.TAG;
                fragment = TrackOfflineFragment.newInstance();
                break;
        }
        popFragmentExceptTag(tag);
        if (fragment != null) {
            if (!getSupportActionBar().isShowing()) {
                layoutId = R.id.coordinator_add_play;
                getSupportActionBar().hide();
            } else {
                layoutId = R.id.frame_main_layout;
            }
        }
        DisplayUtils.replaceFragment(mManager, fragment, layoutId, tag);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void popFragmentExceptTag(String tag) {
        List<Fragment> fragments = mManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.getTag() != null) {
                if (!tag.equals(SearchFragment.TAG)) {
                    DisplayUtils.hideFragment(mManager,
                            mManager.findFragmentByTag(DiscoverFragment.TAG));
                }
                if (!fragment.getTag().equals(DiscoverFragment.TAG) && !fragment.getTag()
                        .equals(tag)) {
                    DisplayUtils.popFragmentBackstack(mManager, fragment.getTag());
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mPresenter.searchData(query);
        mQuery = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mQuery = query;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (!query.isEmpty()) {
            assert mCountDownTimer != null;
            mCountDownTimer.start();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearchItem = menu.findItem(R.id.search_action);
        MenuItemCompat.setOnActionExpandListener(mSearchItem, this);
        initSearchView();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_bottom_sheet:
                showPlayScreen();
                break;

            case R.id.imageview_bottom_next:
                if (mService != null) {
                    mService.next();
                }
                break;

            case R.id.imageview_bottom_previous:
                if (mService != null) {
                    mService.previous();
                }
                break;

            case R.id.imageview_bottom_play:
                if (mService != null) {
                    mService.play();
                    updateButtonState(mService.isPlayingMusic());
                }
                break;
        }
    }

    private void showPlayScreen() {
        Fragment fragment = mManager.findFragmentByTag(PlayMusicFragment.TAG);
        if (fragment != null) {
            DisplayUtils.hideFragment(mManager, mManager.findFragmentByTag(DiscoverFragment.TAG));
        } else {
            DisplayUtils.addFragment(mManager,
                    PlayMusicFragment.newInstance(null, mType, 0, true, false),
                    R.id.coordinator_add_play, PlayMusicFragment.TAG);
        }
        getSupportActionBar().hide();
    }

    private void setupUi() {
        int actionBarHeight = 0;
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) mFrameLayoutMain.getLayoutParams();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        if (mIsServiceRunning) {
            params.setMargins(0, actionBarHeight, 0,
                    getResources().getDimensionPixelOffset(R.dimen.dp_72));
            mConstraintLayout.setVisibility(View.VISIBLE);
        } else {
            params.setMargins(0, actionBarHeight, 0, 0);
            mConstraintLayout.setVisibility(GONE);
        }
        mFrameLayoutMain.setLayoutParams(params);
    }

    private void connectService() {
        final Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initComponents() {
        mPresenter = new MainPresenter(
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(this,
                                TrackDatabaseHelper.getInstance(this))));
        mPresenter.setView(this);
        mManager = getSupportFragmentManager();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_discover));
        }
        setupNavigation();
        sendDataToFragment();
        receiveDataFromPlay();
        receiveBroadcastButton();
        setupListener();
    }

    private void setupListener() {
        mConstraintLayout.setOnClickListener(this);
        mImageViewPrevious.setOnClickListener(this);
        mImageViewPause.setOnClickListener(this);
        mImageViewNext.setOnClickListener(this);
    }

    private void setupNavigation() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(true);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void sendDataToFragment() {
        mDiscovers = getIntent().getParcelableArrayListExtra(EXTRA_GENRE);
        DisplayUtils.addFragment(mManager, DiscoverFragment.newInstance(mDiscovers),
                R.id.frame_main_layout, DiscoverFragment.TAG);
    }

    private void receiveBroadcastButton() {
        mBroadcastButton = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateButtonState(!intent.getBooleanExtra(EXTRA_UPDATE_BUTTON, true));
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_BUTTON);
        registerReceiver(mBroadcastButton, filter);
    }

    private void updateButtonState(boolean isPlaying) {
        if (isPlaying) {
            mImageViewPause.setImageResource(R.drawable.ic_play);
        } else {
            mImageViewPause.setImageResource(R.drawable.ic_pause);
        }
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mConstraintLayout = findViewById(R.id.layout_bottom_sheet);
        mImageViewAvatar = findViewById(R.id.imageview_bottom_avatar);
        mImageViewNext = findViewById(R.id.imageview_bottom_next);
        mImageViewPause = findViewById(R.id.imageview_bottom_play);
        mImageViewPrevious = findViewById(R.id.imageview_bottom_previous);
        mTextViewName = findViewById(R.id.textview_bottom_name);
        mFrameLayoutMain = findViewById(R.id.frame_main_layout);
    }

    public static Intent getProfileIntent(Context context, List<Discover> discovers,
            boolean isServiceRunning, String artUrl, String trackName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_NOTIFY_INTENT, isServiceRunning);
        intent.putExtra(EXTRA_ART_URL, artUrl);
        intent.putExtra(EXTRA_NAME_TRACK, trackName);
        intent.putParcelableArrayListExtra(EXTRA_GENRE,
                (ArrayList<? extends Parcelable>) discovers);
        return intent;
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        SearchView.SearchAutoComplete searchAutoComplete =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.color_gray_400));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.color_gray_900));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
    }

    public static Intent getTrackIntent(Track track, String type, boolean isServiceRunning,
            boolean isOff) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TRACK, track);
        intent.putExtra(EXTRA_SERVICE_RUNNING, isServiceRunning);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_OFF_LINE, isOff);
        intent.setAction(ACTION_OPEN_PLAY_SCREEN);
        return intent;
    }

    private void receiveDataFromPlay() {
        mBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Track track = intent.getParcelableExtra(EXTRA_TRACK);
                mTrack = track;
                mIsServiceRunning = intent.getBooleanExtra(EXTRA_SERVICE_RUNNING, true);
                mType = intent.getStringExtra(EXTRA_TYPE);
                mId = track.getId();
                updateUiBottom(track, intent.getBooleanExtra(EXTRA_OFF_LINE, false));
                saveTrackInfo(mTrack, mType);
                setupUi();
                connectService();
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_OPEN_PLAY_SCREEN);
        registerReceiver(mBroadcast, filter);
    }

    private void saveTrackInfo(Track track, String type) {
        if (!mPresenter.isExistRow(track)) {
            mPresenter.saveTrackPlayingData(track, buildStreamUrl(track.getId()), type);
        }
    }

    private void updateUiBottom(Track track, boolean isOff) {
        mConstraintLayout.setVisibility(View.VISIBLE);
        mTrack = track;
        if (isOff) {
            loadImage(sBitmapTrack, "");
        } else {
            loadImage(null, track.getArtworkUrl());
        }
        mTextViewName.setText(track.getTitle());
    }

    private void loadImage(Bitmap bitmap, String url) {
        Glide.with(this)
                .load(bitmap == null ? url : bitmap)
                .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                        .error(R.drawable.ic_load_image_error))
                .into(mImageViewAvatar);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcast);
        unregisterReceiver(mBroadcastButton);
        super.onDestroy();
    }

    @Override
    public void onSearchSuccess(List<Track> tracks) {
        popFragmentExceptTag(SearchFragment.TAG);
        DisplayUtils.replaceFragment(mManager,
                SearchFragment.newInstance(tracks, mQuery, LIMIT_PER_CALL), R.id.frame_main_layout,
                SearchFragment.TAG);
    }

    @Override
    public void onSearchError(Exception e) {
        DisplayUtils.makeToast(this, e.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
