package com.framgia.music_24.data.source;

/**
 * Created by CuD HniM on 18/08/25.
 */
public interface CallBack<T> {

    void onSuccess(T datas);

    void onError(Exception e);

    void onNetWorkError();
}
