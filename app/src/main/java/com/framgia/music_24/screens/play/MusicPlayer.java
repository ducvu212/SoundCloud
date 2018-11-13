package com.framgia.music_24.screens.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.framgia.music_24.data.model.Track;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.framgia.music_24.data.source.remote.TracksRemoteDataSource.buildStreamUrl;

/**
 * Created by CuD HniM on 18/08/29.
 */
public class MusicPlayer
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener {

    private static final float MAX_VOLUME_INDEX = 1.0f;
    private static final String NOTIFY_NAME_UNKNOW_SINGER = "Unknown";
    private String mUrl;
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private List<Track> mTracks;
    private List<Track> mUnShuffleTracks;
    private int mPosition;
    private boolean mIsLoopOne;
    private boolean mIsLoopAll;
    private boolean mIsOff;
    private OnUpdateUiListener mMediaState;

    MusicPlayer(Context context, List<Track> tracks, int position, OnUpdateUiListener onListener) {
        mContext = context;
        mUnShuffleTracks = new ArrayList<>();
        mTracks = new ArrayList<>();
        mTracks.addAll(tracks);
        mUnShuffleTracks.addAll(tracks);
        mPosition = position;
        mMediaState = onListener;
    }

    public void setDataSource(String url, boolean isOff) {
        mIsOff = isOff;
        mUrl = url;
        try {
            if (mMediaPlayer != null) {
                destroyMedia();
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, Uri.parse(url));
            mMediaPlayer.setOnCompletionListener(this);
            if (isOff) {
                mMediaPlayer.prepare();
            } else {
                mMediaPlayer.prepareAsync();
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(MAX_VOLUME_INDEX, MAX_VOLUME_INDEX);
            mMediaState.updateStateButton(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void play() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mMediaState.updateStateButton(true);
            } else {
                mMediaPlayer.pause();
                mMediaState.updateStateButton(false);
            }
        }
    }

    public void next() {
        destroyMedia();
        if (mIsLoopAll) {
            if (mPosition == mTracks.size() - 1) {
                mPosition = 0;
            } else {
                mPosition++;
            }
        } else {
            if (mPosition == mTracks.size() - 1) {
                return;
            } else {
                mPosition++;
            }
        }
        updateUiState(mTracks.get(mPosition));
    }

    private void updateUiState(Track track) {
        if (!track.isOffline()) {
            setDataSource(buildStreamUrl(track.getId()), mIsOff);
        } else {
            setDataSource(track.getDownloadUri(), mIsOff);
        }
        mMediaState.OnUpdateUiPlay(track);
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    public int getPosition() {
        return mPosition;
    }

    public Track getCurrentTrack() {
        return mTracks.get(mPosition);
    }

    public String getTrackUrl() {
        return mTracks.get(mPosition).getArtworkUrl();
    }

    public void destroyMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mMediaPlayer = new MediaPlayer();
            mMediaState.updateStateButton(false);
        }
    }

    public void previous() {
        destroyMedia();
        if (mPosition > 0) {
            mPosition--;
        } else {
            mPosition = 0;
        }
        updateUiState(mTracks.get(mPosition));
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public int getCurrentPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void setLoopOne() {
        mMediaPlayer.setLooping(true);
        mIsLoopOne = true;
        mIsLoopAll = false;
    }

    public void setLoopAll() {
        mMediaPlayer.setLooping(false);
        mIsLoopOne = false;
        mIsLoopAll = true;
    }

    public void setLoopOff() {
        mIsLoopAll = false;
        mIsLoopOne = false;
    }

    public void shuffle() {
        Track track = mTracks.get(mPosition);
        mTracks.remove(mPosition);
        swapList();
        mTracks.add(mPosition, track);
    }

    private void swapList() {
        Set<Track> newTracks = new HashSet<>();
        while (newTracks.size() != mTracks.size()) {
            Random random = new Random();
            newTracks.add(mTracks.get(random.nextInt(mTracks.size())));
        }
        mTracks.clear();
        mTracks.addAll(newTracks);
    }

    public void unShuffle() {
        mTracks.clear();
        mTracks.addAll(mUnShuffleTracks);
        mPosition = mTracks.indexOf(mTracks.get(mPosition));
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public String getNameTrack() {
        return mTracks.get(mPosition).getTitle();
    }

    @Override

    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        mMediaState.OnBuffer(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mMediaState.OnPlayComplete();
        if (mIsLoopOne) {
            setDataSource(mUrl, mIsOff);
        } else {
            next();
        }
    }
}
