package com.framgia.music_24.data.repository;

import com.framgia.music_24.data.model.Setting;
import com.framgia.music_24.data.source.PlaySettingDataSource;
import com.framgia.music_24.data.source.local.PlaySettingLocalDataSource;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by CuD HniM on 18/09/02.
 */
public class PlaySettingRepository implements PlaySettingDataSource {

    private static PlaySettingRepository sInstance;
    private PlaySettingLocalDataSource mDataSource;

    public PlaySettingRepository(PlaySettingLocalDataSource dataSource) {
        mDataSource = dataSource;
    }

    public static PlaySettingRepository getInstance(PlaySettingLocalDataSource dataSource) {
        if (sInstance == null) {
            synchronized (PlaySettingRepository.class) {
                if (sInstance == null) {
                    sInstance = new PlaySettingRepository(checkNotNull(dataSource));
                }
            }
        }
        return sInstance;
    }

    @Override
    public void saveSetting(Setting setting) {
        mDataSource.saveSetting(setting);
    }

    @Override
    public Setting getSetting() {
        return mDataSource.getSetting();
    }
}
