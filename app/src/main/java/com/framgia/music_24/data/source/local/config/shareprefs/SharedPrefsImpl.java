package com.framgia.music_24.data.source.local.config.shareprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Preconditions;

/**
 * Created by CuD HniM on 18/09/02.
 */

public class SharedPrefsImpl implements SharedPrefsApi {

    private static final String PREFS_NAME = "com.framgia.music_24";
    private static SharedPrefsImpl mInstance;

    private SharedPreferences mSharedPreferences;

    public SharedPrefsImpl(Context context) {
        mSharedPreferences = Preconditions.checkNotNull(context)
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefsImpl getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefsImpl(context);
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (clazz == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (clazz == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        }
        return null;
    }

    @Override
    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        }
        editor.apply();
    }

    @Override
    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
