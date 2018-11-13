package com.framgia.music_24.data.model;

/**
 * Created by CuD HniM on 18/09/02.
 */
public class Setting {
    private boolean mIsShuffle;
    private int mLoopMode;

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean shuffle) {
        mIsShuffle = shuffle;
    }

    public int getLoopMode() {
        return mLoopMode;
    }

    public void setLoopMode(int loopMode) {
        mLoopMode = loopMode;
    }
}
