package com.framgia.music_24.data.source;

import com.framgia.music_24.data.model.Setting;

/**
 * Created by CuD HniM on 18/09/02.
 */
public interface PlaySettingDataSource {
    void saveSetting(Setting setting);

    Setting getSetting();
}
