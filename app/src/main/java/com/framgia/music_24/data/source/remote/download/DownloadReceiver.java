package com.framgia.music_24.data.source.remote.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.os.ResultReceiver;
import com.framgia.music_24.R;
import com.framgia.music_24.data.source.TracksDataSource;
import com.framgia.music_24.utils.StringUtils;

import static com.framgia.music_24.data.source.remote.download.DownloadService.BUNDLE_ERROR;
import static com.framgia.music_24.data.source.remote.download.DownloadService.BUNDLE_PROGRESS;
import static com.framgia.music_24.data.source.remote.download.DownloadService.BUNDLE_SUCCESS;

/**
 * Created by CuD HniM on 18/09/06.
 */
public class DownloadReceiver extends ResultReceiver {

    private static final long PLAY_TIME_WAKE_LOCK = 5000;
    private static final int PLAY_MAX_PERCENT = 100;
    private static final String CHANNEL_ID = "111";
    private Context mContext;
    private static String mTitle;
    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat mNotificationManager;
    private static TracksDataSource.TrackRemoteDataSource.OnDownloadListener mListener;

    public DownloadReceiver(Handler handler) {
        super(handler);
    }

    public void setContext(Context context) {
        mContext = context;
        createNotify();
    }

    public void setData(String title,
            TracksDataSource.TrackRemoteDataSource.OnDownloadListener onDownloadListener) {
        mTitle = title;
        mListener = onDownloadListener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == DownloadService.UPDATE_PROGRESS) {
            int progress = resultData.getInt(BUNDLE_PROGRESS);
            String success = resultData.getString(BUNDLE_SUCCESS);
            String error = resultData.getString(BUNDLE_ERROR);
            updateProgress(progress, success);
            if (error != null && !StringUtils.isBlank(error)) {
                mListener.OnError(error);
            }
        }
    }

    private void createNotify() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock =
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire(PLAY_TIME_WAKE_LOCK);
        mNotificationManager = NotificationManagerCompat.from(mContext);
        mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        mBuilder.setContentTitle(mContext.getString(R.string.play_down_load))
                .setSmallIcon(R.drawable.ic_image_place_holder)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    private void updateProgress(int progress, String success) {
        mBuilder.setContentTitle(mTitle);
        mBuilder.setProgress(PLAY_MAX_PERCENT, progress, false);
        mBuilder.setSubText(progress + "%");
        mNotificationManager.notify(Integer.parseInt(CHANNEL_ID), mBuilder.build());
        if (progress == PLAY_MAX_PERCENT) {
            mNotificationManager.cancel(Integer.parseInt(CHANNEL_ID));
            mListener.OnSuccess(success);
        }
    }
}
