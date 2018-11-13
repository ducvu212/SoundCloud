package com.framgia.music_24.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Setting;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.screens.play.LoopType;
import com.framgia.music_24.screens.play.MusicPlayer;
import com.framgia.music_24.screens.splash.SplashActivity;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_CLEAR;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_NEXT;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_PAUSE;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_PREVIOUS;

public class MusicService extends Service implements OnMusicListener {

    private static final String ACTION_CHANGE_MEDIA_NEXT = "ACTION_CHANGE_MEDIA_NEXT";
    private static final String ACTION_CHANGE_MEDIA_PREVIOUS = "ACTION_CHANGE_MEDIA_PREVIOUS";
    private static final String ACTION_CHANGE_MEDIA_STATE = "ACTION_CHANGE_MEDIA_STATE";
    private static final String ACTION_MEDIA_CLEAR = "ACTION_MEDIA_CLEAR";
    public static final String EXTRA_NOTIFY_INTENT = "EXTRA_NOTIFY_INTENT";
    public static final String EXTRA_NAME_TRACK = "EXTRA_NAME_TRACK";
    public static final String EXTRA_ART_URL = "EXTRA_ART_URL";
    private static final int ID_NOTIFICATION = 112;
    private static final String CHANNEL_ID_NOTIFY = "CHANNEL_ID_NOTIFY";
    private final IBinder mBinder = new LocalBinder();
    private MusicPlayer mMusicPlayer;
    private RemoteViews mRemoteViews;
    private Notification mNotification;
    private boolean mIsKillService;
    private String mUrl;
    private boolean mIsOff;

    public MusicService() {
    }

    public void registerService(MusicPlayer musicPlayer) {
        mMusicPlayer = checkNotNull(musicPlayer);
    }

    public OnMusicListener getListener() {
        return this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_CHANGE_MEDIA_NEXT:
                    mMusicPlayer.next();
                    break;

                case ACTION_CHANGE_MEDIA_PREVIOUS:
                    mMusicPlayer.previous();
                    break;

                case ACTION_CHANGE_MEDIA_STATE:
                    mMusicPlayer.play();
                    updateNotification();
                    break;

                case ACTION_MEDIA_CLEAR:
                    if (!mMusicPlayer.isPlaying()) {
                        mMusicPlayer.destroyMedia();
                        stopSelf();
                        stopForeground(true);
                        mIsKillService = true;
                    }
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void setDataSource(String url, boolean isOff) {
        mUrl = url;
        mMusicPlayer.setDataSource(url, isOff);
    }

    @Override
    public void play() {
        if (mIsKillService) {
            setDataSource(mUrl, mIsOff);
        } else {
            mMusicPlayer.play();
        }
        updateNotification();
    }

    @Override
    public void next() {
        mMusicPlayer.next();
    }

    @Override
    public void previous() {
        mMusicPlayer.previous();
    }

    @Override
    public void seekTo(int position) {
        mMusicPlayer.seekTo(position);
    }

    public void destroyMedia() {
        mMusicPlayer.destroyMedia();
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int getCurrentPosition() {
        return mMusicPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMusicPlayer.getDuration();
    }

    @Override
    public void setLoopOne() {
        mMusicPlayer.setLoopOne();
    }

    @Override
    public void setLoopAll() {
        mMusicPlayer.setLoopAll();
    }

    @Override
    public void setLoopOff() {
        mMusicPlayer.setLoopOff();
    }

    @Override
    public void setShuffle(boolean isShuffle) {
        if (isShuffle) {
            shuffle();
        } else {
            unShuffle();
        }
    }

    public void checkStatus(Setting setting) {
        if (setting.isShuffle()) {
            shuffle();
        } else {
            unShuffle();
        }
        switch (setting.getLoopMode()) {
            case LoopType.LOOP_ALL:
                setLoopAll();
                break;

            case LoopType.LOOP_ONE:
                setLoopOne();
                break;

            case LoopType.NO_LOOP:
                setLoopOff();
                break;
        }
    }

    private void unShuffle() {
        mMusicPlayer.unShuffle();
    }

    private void shuffle() {
        mMusicPlayer.shuffle();
    }

    public boolean isPlayingMusic() {
        return mMusicPlayer.isPlaying();
    }

    public void createNotification(String title, String singer, Bitmap bitmap) {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_notication);
        setDataNotify(title, singer, bitmap);
        createIntentNotify();
        buildNotify();
    }

    private void createIntentNotify() {
        createIntent(R.id.imageview_notify_next, ACTION_CHANGE_MEDIA_NEXT, REQUEST_CODE_NEXT);
        createIntent(R.id.imageview_notify_previous, ACTION_CHANGE_MEDIA_PREVIOUS,
                REQUEST_CODE_PREVIOUS);
        createIntent(R.id.imageview_notify_pause, ACTION_CHANGE_MEDIA_STATE, REQUEST_CODE_PAUSE);
        createIntent(R.id.imageview_notify_clear, ACTION_MEDIA_CLEAR, REQUEST_CODE_CLEAR);
    }

    private void createIntent(int id, String action, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(getApplicationContext(), MusicService.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(getApplicationContext(), requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(id, pendingIntent);
    }

    private void buildNotify() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(EXTRA_NOTIFY_INTENT, true);
        intent.putExtra(EXTRA_NAME_TRACK, mMusicPlayer.getNameTrack());
        intent.putExtra(EXTRA_ART_URL, mMusicPlayer.getTrackUrl());
        PendingIntent pendingIntent =
                PendingIntent.getActivities(this, (int) System.currentTimeMillis(),
                        new Intent[] { intent }, 0);
        Notification.Builder notificationBuilder =
                new Notification.Builder(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotification = notificationBuilder.setSmallIcon(R.drawable.ic_image_place_holder)
                    .setContentIntent(pendingIntent)
                    .setContent(mRemoteViews)
                    .setDefaults(Notification.FLAG_NO_CLEAR)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_NOTIFY, name, importance);
            mNotification = notificationBuilder.setSmallIcon(R.drawable.ic_image_place_holder)
                    .setChannelId(CHANNEL_ID_NOTIFY)
                    .build();
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(ID_NOTIFICATION , mNotification);
        }
        startForeground(ID_NOTIFICATION, mNotification);
    }

    public List<Track> getListPlaying() {
        return mMusicPlayer.getTracks();
    }

    public int getPosition() {
        return mMusicPlayer.getPosition();
    }

    public Track getCurrentTrack() {
        return mMusicPlayer.getCurrentTrack();
    }

    public String getNameTrack() {
        return mMusicPlayer.getNameTrack();
    }

    public void setDataNotify(String title, String singer, Bitmap bitmap) {
        mRemoteViews.setImageViewBitmap(R.id.imageview_notify_avatar, bitmap);
        mRemoteViews.setTextViewText(R.id.textview_notify_name, title);
        mRemoteViews.setTextViewText(R.id.textview_notify_singer, singer);
        mRemoteViews.setImageViewResource(R.id.imageview_notify_clear, R.drawable.ic_clear);
        mRemoteViews.setImageViewResource(R.id.imageview_notify_next, R.drawable.ic_next);
        mRemoteViews.setImageViewResource(R.id.imageview_notify_pause, R.drawable.ic_pause);
        mRemoteViews.setImageViewResource(R.id.imageview_notify_previous, R.drawable.ic_previous);
    }

    public void updateNotification() {
        if (mMusicPlayer.isPlaying()) {
            mRemoteViews.setImageViewResource(R.id.imageview_notify_pause, R.drawable.ic_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.imageview_notify_pause, R.drawable.ic_play);
        }
        if (mIsKillService) {
            mRemoteViews.setImageViewResource(R.id.imageview_notify_pause, R.drawable.ic_pause);
            mIsKillService = false;
        }
        startForeground(ID_NOTIFICATION, mNotification);
    }
}
