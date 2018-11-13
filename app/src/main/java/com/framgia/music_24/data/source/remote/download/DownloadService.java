package com.framgia.music_24.data.source.remote.download;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.ResultReceiver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.framgia.music_24.screens.play.PlayMusicFragment.EXTRA_TRACK_RECEIVER;
import static com.framgia.music_24.screens.play.PlayMusicFragment.EXTRA_TRACK_TITLE;
import static com.framgia.music_24.screens.play.PlayMusicFragment.EXTRA_TRACK_URL;

public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public static final String BUNDLE_SUCCESS = "BUNDLE_SUCCESS";
    public static final String BUNDLE_PROGRESS = "BUNDLE_PROGRESS";
    public static final String BUNDLE_ERROR = "BUNDLE_ERROR";
    private static final String SOUND_CLOUD_PATH_DOWNLOAD = "sdcard/Download/Soundcloud";
    private static final String FILE_HEADER = "Location";
    private static final String PATH_DOWNLOAD = "sdcard/Download/";
    private static final String SOUND_CLOUD_DIRECTORY = "/SoundCloud/";
    private static final Object SOUND_CLOUD_FILE_EXTENSION = ".mp3";
    private static final long MAX_PERCENT = 100;
    private static final int NOTIFY_UPDATE = 10;
    private String mPathFile;
    private HttpURLConnection mConnection;
    private String mDirectPath;

    public DownloadService() {
        super(DownloadService.class.getSimpleName());
    }

    private static String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();
        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField(FILE_HEADER);
            return getFinalURL(redirectUrl);
        }
        con.disconnect();
        return url;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createForderDownload(intent);
        Bundle resultData = new Bundle();
        InputStream input = null;
        OutputStream output = null;
        String urlToDownload = intent.getStringExtra(EXTRA_TRACK_URL);
        ResultReceiver receiver = intent.getParcelableExtra(EXTRA_TRACK_RECEIVER);
        try {
            URL url = new URL(getFinalURL(urlToDownload));
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.connect();
            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = mConnection.getInputStream();
                output = new FileOutputStream(mDirectPath);
                downloadFile(resultData, receiver, input, output);
            }
        } catch (Exception e) {
            sendErrorString(resultData, receiver, e.toString());
        } finally {
            finishDownload(resultData, receiver, input, output);
        }
    }

    private void createForderDownload(Intent intent) {
        File cacheDownloadFile = new File(SOUND_CLOUD_PATH_DOWNLOAD);
        String mTitle = intent.getStringExtra(EXTRA_TRACK_TITLE);
        StringBuilder builder = new StringBuilder();
        builder.append(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .append(SOUND_CLOUD_DIRECTORY)
                .append(mTitle)
                .append(SOUND_CLOUD_FILE_EXTENSION);
        mDirectPath = builder.toString();
        File dir = new File(SOUND_CLOUD_PATH_DOWNLOAD);
        File cacheDir = new File(PATH_DOWNLOAD);
        if (!cacheDir.isDirectory()) {
            cacheDir.mkdirs();
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(mDirectPath);
        if (file.exists()) {
            mPathFile = file.getPath();
        } else{
            mPathFile = mDirectPath;
        }
        if (!cacheDownloadFile.exists()) {
            try {
                cacheDownloadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadFile(Bundle resultData, ResultReceiver receiver, InputStream input,
            OutputStream output) throws IOException {
        int fileLength = mConnection.getContentLength();
        byte data[] = new byte[1024];
        long total = 0;
        int count;
        int percentDone = 0;
        while ((count = input.read(data)) != -1) {
            total += count;
            int latestPercentDone = (int) (total * MAX_PERCENT / fileLength);
            if (percentDone + NOTIFY_UPDATE == latestPercentDone) {
                percentDone = latestPercentDone;
                resultData.putInt(BUNDLE_PROGRESS, percentDone);
                resultData.putString(BUNDLE_SUCCESS, mPathFile);
                receiver.send(UPDATE_PROGRESS, resultData);
            }
            output.write(data, 0, count);
        }
    }

    private void finishDownload(Bundle resultData, ResultReceiver receiver, InputStream input,
            OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        } catch (IOException ignored) {
            sendErrorString(resultData, receiver, ignored.toString());
        }
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    private void sendErrorString(Bundle resultData, ResultReceiver receiver, String error) {
        resultData.putString(BUNDLE_ERROR, error);
        receiver.send(UPDATE_PROGRESS, resultData);
    }
}
