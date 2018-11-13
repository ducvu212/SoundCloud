package com.framgia.music_24.data.source.local.config.shareprefs;

/**
 * Created by CuD HniM on 18/09/02.
 */

public interface SharedPrefsApi {
    <T> T get(String key, Class<T> clazz);

    <T> void put(String key, T data);

    void clear();
}
