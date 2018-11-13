package com.framgia.music_24.screens;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by CuD HniM on 18/08/29.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager mManager;
    private boolean mIsLoading;

    protected EndlessScrollListener(LinearLayoutManager manager) {
        mManager = manager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        assert mManager != null;
        int totalItemCount = mManager.getItemCount();
        int lastVisibleItem = mManager.findLastVisibleItemPosition();
        int firstVisiableItem = mManager.findFirstVisibleItemPosition();
        int visibleItemCount = mManager.getChildCount();
        int visibleThreshold = 5;
        if (mIsLoading && totalItemCount > (lastVisibleItem + visibleThreshold)) {
            mIsLoading = false;
        }
        if (!mIsLoading
                && (visibleItemCount + firstVisiableItem) >= totalItemCount
                && firstVisiableItem >= 0
                && totalItemCount >= 10) {
            if (mManager != null) {
                OnLoadMore();
            }
            mIsLoading = true;
        }
    }

    protected abstract void OnLoadMore();
}
