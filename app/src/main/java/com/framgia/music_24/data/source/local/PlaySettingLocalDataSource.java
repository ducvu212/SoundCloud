package com.framgia.music_24.data.source.local;

import android.content.Context;
import com.framgia.music_24.data.model.Setting;
import com.framgia.music_24.data.source.PlaySettingDataSource;
import com.framgia.music_24.data.source.local.config.shareprefs.SharedPrefsImpl;

/**
 * Created by CuD HniM on 18/09/02.
 */
public class PlaySettingLocalDataSource implements PlaySettingDataSource {

    private static final String PREF_IS_SHUFFLE = "PREF_IS_SHUFFLE";
    private static final String PREF_LOOP_MODE = "PREF_LOOP_MODE";
    private static PlaySettingLocalDataSource sInstance;
    private SharedPrefsImpl mSharedPrefs;

    public PlaySettingLocalDataSource(Context context) {
        mSharedPrefs = SharedPrefsImpl.getInstance(context);
    }

    public static synchronized PlaySettingLocalDataSource getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PlaySettingLocalDataSource.class) {
                if (sInstance == null) {
                    sInstance = new PlaySettingLocalDataSource(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void saveSetting(Setting setting) {
        if (setting == null){
            return;
        }
        mSharedPrefs.put(PREF_IS_SHUFFLE, setting.isShuffle());
        mSharedPrefs.put(PREF_LOOP_MODE, setting.getLoopMode());
    }

    @Override
    public Setting getSetting() {
        Setting setting = new Setting();
        setting.setLoopMode(mSharedPrefs.get(PREF_LOOP_MODE, Integer.class));
        setting.setShuffle(mSharedPrefs.get(PREF_IS_SHUFFLE, Boolean.class));
        return setting;
    }
}
