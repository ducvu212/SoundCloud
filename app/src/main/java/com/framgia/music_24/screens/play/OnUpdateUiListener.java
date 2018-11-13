package com.framgia.music_24.screens.play;

import com.framgia.music_24.data.model.Track;

/**
 * Created by CuD HniM on 18/08/30.
 */
public interface OnUpdateUiListener {
    void updateStateButton(boolean isPlaying);

    void OnUpdateUiPlay(Track track);

    void OnPlayComplete();

    void OnBuffer(int position);
}
