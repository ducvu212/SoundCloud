package com.framgia.music_24.service;

/**
 * Created by CuD HniM on 18/08/30.
 */
public interface OnMusicListener {

    void setDataSource(String url, boolean isOff);

    void play();

    void next();

    void previous();

    void seekTo(int position);

    int getCurrentPosition();

    int getDuration();

    void setLoopOne();

    void setLoopAll();

    void setLoopOff();

    void setShuffle(boolean shuffle);
}
