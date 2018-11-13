package com.framgia.music_24.screens.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Setting;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.PlaySettingRepository;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.local.PlaySettingLocalDataSource;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import com.framgia.music_24.data.source.remote.download.DownloadReceiver;
import com.framgia.music_24.data.source.remote.download.DownloadService;
import com.framgia.music_24.screens.discover.DiscoverFragment;
import com.framgia.music_24.screens.playinglist.PlayingListFragment;
import com.framgia.music_24.screens.trackoffline.TrackOfflineFragment;
import com.framgia.music_24.service.MusicService;
import com.framgia.music_24.service.OnMusicListener;
import com.framgia.music_24.utils.DisplayUtils;
import com.framgia.music_24.utils.SoundCloudApplication;
import com.framgia.music_24.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.music_24.data.source.remote.TracksRemoteDataSource.buildStreamUrl;
import static com.framgia.music_24.screens.discover.DiscoverFragment.ARGUMENT_POSITION_ITEM;
import static com.framgia.music_24.screens.main.MainActivity.getTrackIntent;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayMusicFragment extends Fragment
        implements PlayMusicContract.View, View.OnClickListener, OnUpdateUiListener,
        SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "PlayMusicFragment";
    public static final String EXTRA_TRACK_TITLE = "EXTRA_TRACK_TITLE";
    public static final String EXTRA_TRACK_RECEIVER = "EXTRA_TRACK_RECEIVER";
    public static final String EXTRA_TRACK_URL = "EXTRA_TRACK_URL";
    public static final int PLAY_FAVORITE = 1;
    public static final int PLAY_UN_FAVORITE = 0;
    public static final int PLAY_DOWNLOADED = 1;
    private static final int TIME_UPDATE_SEEKBAR = 1000;
    private static final int TIME_UPDATE_SEEKBAR_LOOP = 300;
    private static final String ARGUMENT_LIST_PLAY = "LIST_TRACKS_PLAYING";
    private static final String ARGUMENT_TYPE = "ARGUMENT_TYPE";
    private static final String ARGUMENT_MAIN = "ARGUMENT_MAIN";
    public static final String ACTION_UPDATE_BUTTON = "ACTION_UPDATE_BUTTON";
    public static final String EXTRA_UPDATE_BUTTON = "EXTRA_UPDATE_BUTTON";
    private static final String ARGUMENT_SHUFFLE = "ARGUMENT_SHUFFLE";
    public static final String EXTRA_POSSITION = "EXTRA_POSSITION";
    private static final String ACTION_FAVORITE = "ACTION_FAVORITE";
    private static MusicService sService;
    public static Bitmap sBitmapTrack;
    private static PlayMusicFragment sInstance;
    private FragmentActivity mContext;
    private PlayMusicContract.Presenter mPresenter;
    private ImageView mImageViewPlay;
    private ImageView mImageViewNext;
    private ImageView mImageViewPrevious;
    private ImageView mImageViewLoop;
    private ImageView mImageViewShuffle;
    private ImageView mImageViewFavorite;
    private ImageView mImageViewDownload;
    private ImageView mImageViewTrack;
    private ImageView mImageViewBack;
    private ImageView mImageViewPlaylist;
    private TextView mTextViewName;
    private TextView mTextViewSinger;
    private TextView mTextViewTimeRunning;
    private TextView mTextViewTotalTime;
    private SeekBar mSeekBar;
    private MusicPlayer mPlayer;
    private Track mCurrentTrack;
    private int mId;
    private int mSeekbarPosition;
    private String mType;
    private boolean mIsShuffle;
    private Handler mHandler;
    private OnMusicListener mMediaListener;
    private Runnable mRunnable;
    private boolean mIsMain;
    private boolean mIsSendShuffle;
    private String mUrl;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
            sService = binder.getService();
            if (sService != null) {
                if (!mIsMain) {
                    sService.registerService(mPlayer);
                    if (mCurrentTrack.isOffline()) {
                        mUrl = mCurrentTrack.getDownloadUri();
                    }
                    sService.setDataSource(mUrl, mCurrentTrack.isOffline());
                } else {
                    setupPlayFromMain();
                }
                if (mIsSendShuffle) {
                    setShuffle();
                }
                setupPlaySetting();
                showTrackInfo(mCurrentTrack);
                mMediaListener = sService.getListener();
                mHandler.postDelayed(mRunnable, TIME_UPDATE_SEEKBAR_LOOP);
                if (!mCurrentTrack.isOffline()) {
                    mPresenter.convertBitmap(mCurrentTrack.getArtworkUrl());
                } else {
                    setNotification();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connectService();
        }
    };

    private void setNotification() {
        if (mCurrentTrack.getBitmap() == null) {
            Bitmap bitmap =
                    BitmapFactory.decodeResource(SoundCloudApplication.getInstance().getResources(),
                            R.drawable.ic_load_image_error);
            sBitmapTrack = bitmap;
            mCurrentTrack.setBitmap(bitmap);
        } else {
            sBitmapTrack = mCurrentTrack.getBitmap();
        }
        sService.createNotification(mCurrentTrack.getTitle(), mCurrentTrack.getUser().getUsername(),
                mCurrentTrack.getBitmap());
    }

    public PlayMusicFragment() {
        // Required empty public constructor
    }

    public static PlayMusicFragment newInstance(List<Track> tracks, String type, int position,
            boolean isMain, boolean isShuffle) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_POSITION_ITEM, position);
        bundle.putString(ARGUMENT_TYPE, type);
        bundle.putBoolean(ARGUMENT_MAIN, isMain);
        bundle.putBoolean(ARGUMENT_SHUFFLE, isShuffle);
        bundle.putParcelableArrayList(ARGUMENT_LIST_PLAY, (ArrayList<? extends Parcelable>) tracks);
        sInstance = new PlayMusicFragment();
        sInstance.setArguments(bundle);
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
        connectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return inflater.inflate(R.layout.fragment_play, container, false);
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
        getDataFromActivity();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageview_play:
                mMediaListener.play();
                break;

            case R.id.imageview_next:
                mMediaListener.next();
                break;

            case R.id.imageview_previous:
                mMediaListener.previous();
                break;

            case R.id.imageview_loop:
                setLoop();
                break;

            case R.id.imageview_shuffle:
                setShuffle();
                mMediaListener.setShuffle(mIsShuffle);
                break;

            case R.id.imageview_favorite_genre:
                setFavorite();
                break;

            case R.id.imageview_download:
                download();
                break;

            case R.id.frame_play:
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                break;

            case R.id.imageview_back:
                handleBack();
                break;

            case R.id.imageview_playlist:
                handlePlayList();
                break;

            default:
        }
    }

    private void handleBack() {
        mContext.getSupportFragmentManager().beginTransaction().hide(sInstance).commit();
        mContext.getSupportFragmentManager()
                .beginTransaction()
                .show(mContext.getSupportFragmentManager().findFragmentByTag(DiscoverFragment.TAG))
                .commit();
        ((AppCompatActivity) mContext).getSupportActionBar().show();
        setAction(findFragment(PlayMusicFragment.TAG));
    }

    private int findFragment(String tag) {
        int pos = 0;
        List<Fragment> fragments = mContext.getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) != null && fragments.get(i).getTag() != null) {
                if (fragments.get(i).getTag().equals(tag)) {
                    pos = i;
                }
            }
        }
        return pos;
    }

    private void setAction(int i) {
        List<Fragment> fragments = mContext.getSupportFragmentManager().getFragments();
        if (fragments.get(i - 1).getTag() != null) {
            if (fragments.get(i - 1).getTag().equals(DiscoverFragment.TAG)) {
                ((AppCompatActivity) mContext).getSupportActionBar().show();
            }
            if (fragments.get(i - 1).getTag().equals(TrackOfflineFragment.TAG)) {
                {
                    ((AppCompatActivity) mContext).getSupportActionBar().show();
                }
            }
        }
    }

    @Override
    public void initData(Track track) {
        if (track.getDownloaded() == PLAY_DOWNLOADED) {
            mImageViewDownload.setImageResource(R.drawable.ic_downloaded);
            mUrl = track.getDownloadUri();
        } else {
            mImageViewDownload.setImageResource(R.drawable.ic_download);
            mUrl = buildStreamUrl(mId);
        }
        if (track.getFavorite() == PLAY_FAVORITE) {
            mImageViewFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            mImageViewFavorite.setImageResource(R.drawable.ic_un_favorite);
        }
    }

    @Override
    public void downloadError(String error) {
        DisplayUtils.makeToast(mContext, error);
        mImageViewDownload.setClickable(true);
    }

    @Override
    public void downloadSuccess(String url) {
        mCurrentTrack.setDownloaded(PLAY_DOWNLOADED);
        mPresenter.editDownload(mCurrentTrack, PLAY_DOWNLOADED, url);
        updateDownloadButton();
        DisplayUtils.makeToast(mContext, mContext.getString(R.string.play_complete));
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void updateStateButton(boolean isPlaying) {
        if (isPlaying) {
            mImageViewPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mImageViewPlay.setImageResource(R.drawable.ic_play);
        }
        sendUpdateButtonBroadcast(isPlaying);
    }

    private void sendUpdateButtonBroadcast(boolean isPlaying) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_BUTTON, isPlaying);
        intent.setAction(ACTION_UPDATE_BUTTON);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void OnUpdateUiPlay(Track track) {
        boolean isOff;
        mCurrentTrack = track;
        mId = mCurrentTrack.getId();
        mPresenter.findTrackById(String.valueOf(mId));
        if (!mCurrentTrack.isOffline()) {
            mPresenter.convertBitmap(mCurrentTrack.getArtworkUrl());
            isOff = false;
        } else {
            setNotification();
            isOff = true;
        }
        mContext.sendBroadcast(getTrackIntent(mCurrentTrack, mType, true, isOff));
        showTrackInfo(mCurrentTrack);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mSeekbarPosition = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mTextViewTimeRunning.setText(StringUtils.convertMilisecToMinute(mSeekbarPosition));
        mMediaListener.seekTo(mSeekbarPosition);
    }

    @Override
    public void convertSuccess(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap =
                    BitmapFactory.decodeResource(SoundCloudApplication.getInstance().getResources(),
                            R.drawable.ic_load_image_error);
        }
        sService.createNotification(mCurrentTrack.getTitle(), mCurrentTrack.getUser().getUsername(),
                bitmap);
    }

    @Override
    public void OnPlayComplete() {
        if (mMediaListener != null) {
            Setting setting = mPresenter.getSetting();
            sService.checkStatus(setting);
        }
    }

    @Override
    public void OnBuffer(int position) {
        mSeekBar.setSecondaryProgress(position);
    }

    public void connectService() {
        final Intent intent = new Intent(getContext(), MusicService.class);
        mContext.getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void destroyMedia() {
        if (sService != null) {
            sService.destroyMedia();
        }
    }

    private void initViews(View view) {
        mImageViewDownload = view.findViewById(R.id.imageview_download);
        mImageViewFavorite = view.findViewById(R.id.imageview_favorite_genre);
        mImageViewShuffle = view.findViewById(R.id.imageview_shuffle);
        mImageViewLoop = view.findViewById(R.id.imageview_loop);
        mImageViewPrevious = view.findViewById(R.id.imageview_previous);
        mImageViewNext = view.findViewById(R.id.imageview_next);
        mImageViewPlay = view.findViewById(R.id.imageview_play);
        mImageViewTrack = view.findViewById(R.id.imageview_track);
        mImageViewBack = view.findViewById(R.id.imageview_back);
        mImageViewPlaylist = view.findViewById(R.id.imageview_playlist);
        mTextViewTimeRunning = view.findViewById(R.id.textview_time_running);
        mTextViewSinger = view.findViewById(R.id.textview_singer);
        mTextViewName = view.findViewById(R.id.textview_name_track);
        mTextViewTotalTime = view.findViewById(R.id.textview_time_total);
        mSeekBar = view.findViewById(R.id.seekBar);
        FrameLayout frameLayout = view.findViewById(R.id.frame_play);
        frameLayout.setOnClickListener(this);
    }

    private void initComponents() {
        mPresenter = new PlayMusicPresenter(
                PlaySettingRepository.getInstance(PlaySettingLocalDataSource.getInstance(mContext)),
                TracksRepository.getInstance(TracksRemoteDataSource.getInstance(),
                        TrackLocalDataSource.getInstance(mContext,
                                TrackDatabaseHelper.getInstance(mContext))));
        setupListener();
        mHandler = new Handler();
        updateSeekBar();
        handleBackKey();
    }

    private void handleBackKey() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mContext.getSupportFragmentManager().beginTransaction().hide(sInstance).commit();
                    mContext.getSupportFragmentManager()
                            .beginTransaction()
                            .show(mContext.getSupportFragmentManager().findFragmentByTag(DiscoverFragment.TAG))
                            .commit();
                    ((AppCompatActivity) mContext).getSupportActionBar().show();
                }
                return true;
            }
        });
    }

    private void setupListener() {
        mPresenter.setView(this);
        mImageViewDownload.setOnClickListener(this);
        mImageViewFavorite.setOnClickListener(this);
        mImageViewShuffle.setOnClickListener(this);
        mImageViewLoop.setOnClickListener(this);
        mImageViewPrevious.setOnClickListener(this);
        mImageViewNext.setOnClickListener(this);
        mImageViewPlay.setOnClickListener(this);
        mImageViewBack.setOnClickListener(this);
        mImageViewPlaylist.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void setupPlaySetting() {
        Setting setting = mPresenter.getSetting();
        mIsShuffle = setting.isShuffle();
        if (mIsShuffle) {
            mImageViewShuffle.setImageResource(R.drawable.ic_shuffle_on);
        } else {
            mImageViewShuffle.setImageResource(R.drawable.ic_shuffle_off);
        }
        switch (setting.getLoopMode()) {
            case LoopType.NO_LOOP:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_off);
                break;

            case LoopType.LOOP_ONE:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_one_on);
                break;

            case LoopType.LOOP_ALL:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_on);
                break;
        }
        sService.checkStatus(setting);
    }

    private void checkShuffle(boolean isShuffle) {
        if (isShuffle) {
            mImageViewShuffle.setImageResource(R.drawable.ic_shuffle_off);
            DisplayUtils.makeToast(mContext, getString(R.string.play_shuffle_off));
        } else {
            mImageViewShuffle.setImageResource(R.drawable.ic_shuffle_on);
            DisplayUtils.makeToast(mContext, getString(R.string.play_shuffle_on));
        }
    }

    private void setShuffle() {
        Setting setting = mPresenter.getSetting();
        checkShuffle(mIsShuffle);
        mIsShuffle = !mIsShuffle;
        setting.setShuffle(mIsShuffle);
        mPresenter.saveSetting(setting);
    }

    private void setLoop() {
        Setting setting = mPresenter.getSetting();
        switch (setting.getLoopMode()) {
            case LoopType.NO_LOOP:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_one_on);
                mMediaListener.setLoopOne();
                setting.setLoopMode(LoopType.LOOP_ONE);
                DisplayUtils.makeToast(mContext, getString(R.string.play_loop_one));
                break;

            case LoopType.LOOP_ONE:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_on);
                mMediaListener.setLoopAll();
                setting.setLoopMode(LoopType.LOOP_ALL);
                DisplayUtils.makeToast(mContext, getString(R.string.play_loop_all));
                break;

            case LoopType.LOOP_ALL:
                mImageViewLoop.setImageResource(R.drawable.ic_loop_off);
                mMediaListener.setLoopOff();
                setting.setLoopMode(LoopType.NO_LOOP);
                DisplayUtils.makeToast(mContext, getString(R.string.play_loop_off));
                break;
        }
        mPresenter.saveSetting(setting);
    }

    private void getDataFromActivity() {
        if (getArguments() != null) {
            mIsMain = getArguments().getBoolean(ARGUMENT_MAIN);
            mIsSendShuffle = getArguments().getBoolean(ARGUMENT_SHUFFLE);
            if (!mIsMain) {
                destroyMedia();
                int position = getArguments().getInt(ARGUMENT_POSITION_ITEM);
                List<Track> tracks = getArguments().getParcelableArrayList(ARGUMENT_LIST_PLAY);
                mType = getArguments().getString(ARGUMENT_TYPE);
                mCurrentTrack = tracks.get(position);
                mId = mCurrentTrack.getId();
                if (mPlayer != null) {
                    mPlayer.destroyMedia();
                }
                mPlayer = new MusicPlayer(getContext(), tracks, position, this);
                addTracks(tracks);
                mPresenter.findTrackById(String.valueOf(mId));
                mContext.sendBroadcast(sendFavoritePlaylist(position));
                mContext.sendBroadcast(getTrackIntent(mCurrentTrack, mType, true, false));
            }
        }
    }

    private void setupPlayFromMain() {
        int position = sService.getPosition();
        List<Track> tracks = sService.getListPlaying();
        mType = getArguments().getString(ARGUMENT_TYPE);
        mCurrentTrack = tracks.get(position);
        mId = mCurrentTrack.getId();
        showTrackInfo(mCurrentTrack);
        mContext.sendBroadcast(sendFavoritePlaylist(position));
    }

    private void addTracks(List<Track> tracks) {
        for (Track track : tracks) {
            if (!mPresenter.isExistRow(track)) {
                mPresenter.addTracks(track);
            }
        }
    }

    private void showTrackInfo(Track track) {
        if (track.getBitmap() == null && !track.isOffline()) {
            setImageTrack(track.getArtworkUrl());
            mImageViewDownload.setVisibility(View.VISIBLE);
        } else {
            mImageViewTrack.setImageBitmap(track.getBitmap());
            mImageViewDownload.setVisibility(View.INVISIBLE);
        }
        mTextViewName.setText(track.getTitle());
        mTextViewSinger.setText(track.getUser().getUsername());
        if (track.isOffline()) {
            mTextViewTotalTime.setText(StringUtils.convertMilisecToMinute(sService.getDuration()));
            mSeekBar.setMax(sService.getDuration());
        } else {
            mTextViewTotalTime.setText(
                    StringUtils.convertMilisecToMinute(mCurrentTrack.getDuration()));
            mSeekBar.setMax(mCurrentTrack.getDuration());
        }
        if (track.getDownloaded() == 1) {
            mImageViewDownload.setClickable(false);
        }
    }

    private void setImageTrack(String url) {
        Glide.with(SoundCloudApplication.getInstance())
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                        .error(R.drawable.ic_load_image_error).dontTransform())
                .into(mImageViewTrack);
    }

    private void setFavorite() {
        if (mCurrentTrack.getFavorite() == PLAY_UN_FAVORITE) {
            mImageViewFavorite.setImageResource(R.drawable.ic_favorite);
            mCurrentTrack.setFavorite(PLAY_FAVORITE);
            DisplayUtils.makeToast(mContext, getString(R.string.play_favorite));
            mPresenter.editFavorite(mCurrentTrack, PLAY_FAVORITE);
        } else {
            mImageViewFavorite.setImageResource(R.drawable.ic_un_favorite);
            mCurrentTrack.setFavorite(PLAY_UN_FAVORITE);
            DisplayUtils.makeToast(mContext, getString(R.string.play_un_favorite));
            mPresenter.editFavorite(mCurrentTrack, PLAY_UN_FAVORITE);
        }
    }

    private void download() {
        if(mCurrentTrack.getDownloaded() != 1) {
            DisplayUtils.makeToast(mContext, getString(R.string.download_start));
            Intent intent = new Intent(mContext, DownloadService.class);
            DownloadReceiver receiver = new DownloadReceiver(new Handler());
            receiver.setContext(mContext);
            intent.putExtra(EXTRA_TRACK_TITLE, mCurrentTrack.getTitle());
            intent.putExtra(EXTRA_TRACK_URL, buildStreamUrl(mCurrentTrack.getId()));
            intent.putExtra(EXTRA_TRACK_RECEIVER, receiver);
            mPresenter.downloadTrack(mCurrentTrack.getTitle());
            mContext.startService(intent);
            mImageViewDownload.setClickable(false);
        }
    }

    private void updateDownloadButton() {
        mCurrentTrack.setDownloaded(PLAY_DOWNLOADED);
        mImageViewDownload.setImageResource(R.drawable.ic_downloaded);
    }

    private void updateSeekBar() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                long timeRunning = mMediaListener.getCurrentPosition();
                mSeekBar.setProgress((int) timeRunning);
                mTextViewTimeRunning.setText(StringUtils.convertMilisecToMinute(timeRunning));
                mHandler.postDelayed(mRunnable, TIME_UPDATE_SEEKBAR);
            }
        };
    }

    private void handlePlayList() {
        mContext.getSupportFragmentManager()
                .popBackStack(PlayMusicFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DisplayUtils.addFragment(mContext.getSupportFragmentManager(),
                PlayingListFragment.newInstance(sService.getListPlaying(), mType,
                        sService.getNameTrack()), R.id.coordinator_add_play,
                PlayingListFragment.TAG);
    }

    private Intent sendFavoritePlaylist(int position){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_POSSITION, position);
        intent.setAction(ACTION_FAVORITE);
        return intent;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
