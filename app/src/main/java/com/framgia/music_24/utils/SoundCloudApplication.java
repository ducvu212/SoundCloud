package com.framgia.music_24.utils;

import android.app.Application;
import com.framgia.music_24.data.repository.TracksRepository;

/**
 * Created by CuD HniM on 18/09/07.
 */
public class SoundCloudApplication extends Application {

    private static SoundCloudApplication sInstance;

    public static synchronized SoundCloudApplication getInstance() {
        if (sInstance == null) {
            synchronized (TracksRepository.class) {
                if (sInstance == null) {
                    sInstance = new SoundCloudApplication();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
