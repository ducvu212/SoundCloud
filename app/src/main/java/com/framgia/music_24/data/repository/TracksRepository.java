package com.framgia.music_24.data.repository;

import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.source.CallBack;
import com.framgia.music_24.data.source.TracksDataSource;
import com.framgia.music_24.data.source.local.TrackLocalDataSource;
import com.framgia.music_24.data.source.remote.TracksRemoteDataSource;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Created by CuD HniM on 18/08/25.
 */
public class TracksRepository {

    private static TracksRepository sInstance;
    private TracksRemoteDataSource mRemoteDataSource;
    private TrackLocalDataSource mLocalDataSource;

    private TracksRepository(TracksRemoteDataSource tracksRemoteDataSource,
            TrackLocalDataSource trackLocalDataSource) {
        mRemoteDataSource = checkNotNull(tracksRemoteDataSource);
        mLocalDataSource = checkNotNull(trackLocalDataSource);
    }

    public static synchronized TracksRepository getInstance(
            TracksRemoteDataSource tracksRemoteDataSource,
            TrackLocalDataSource trackLocalDataSource) {
        if (sInstance == null) {
            synchronized (TracksRepository.class) {
                if (sInstance == null) {
                    sInstance = new TracksRepository(checkNotNull(tracksRemoteDataSource),
                            checkNotNull(trackLocalDataSource));
                }
            }
        }
        return sInstance;
    }

    public void getTrack(String genre, String genreTitle, String type, CallBack callBack) {
        mRemoteDataSource.getTrack(genre, genreTitle, type, callBack);
    }

    public void getTrack(String genre, int limit, CallBack callBack) {
        mRemoteDataSource.getTrack(genre, limit, callBack);
    }

    public void addTrack(Track track) {
        mLocalDataSource.addTrack(track);
    }

    public List<Track> getAllTracks() {
        return mLocalDataSource.getAllTracks();
    }

    public void updateFavorite(Track track, int fav) {
        mLocalDataSource.updateFavorite(track, fav);
    }

    public boolean isExistRow(Track track) {
        return mLocalDataSource.isExistRow(track);
    }

    public Track findTrackById(String id) {
        return mLocalDataSource.findTrackById(id);
    }

    public void updateDownload(Track track, int download, String uri) {
        mLocalDataSource.updateDownload(track, download, uri);
    }

    public void downloadTrack(String title,
            TracksDataSource.TrackRemoteDataSource.OnDownloadListener onDownloadListener) {
        mRemoteDataSource.downloadTrack(title, onDownloadListener);
    }

    public void convertBitmap(String url,
            TracksDataSource.TrackRemoteDataSource.OnConvertBitmapListener
                    onConvertBitmapListener) {
        mRemoteDataSource.convertBitmap(url, onConvertBitmapListener);
    }

    public void saveTrackPlayingData(Track track, String url, String type) {
        mLocalDataSource.saveTrackPlayingData(track, url, type);
    }

    public String getTrackUrl() {
        return mLocalDataSource.getTrackUrl();
    }

    public String getTrackType() {
        return mLocalDataSource.getTrackType();
    }

    public int getTrackID() {
        return mLocalDataSource.getTrackId();
    }

    public List<Track> getAllFavorite() {
        return mLocalDataSource.getAllFavorite();
    }

    public List<Track> getAllDownload() {
        return mLocalDataSource.getAllDownload();
    }

    public void search(String query, int limit, CallBack<List<Track>> callBack) {
        mRemoteDataSource.search(query, limit, callBack);
    }

    public void getAllOfflineTrack(
            TracksDataSource.TrackLocalDataSource.OnGetOfflineTrackListener
                    onGetOfflineTrackListener) {
        mLocalDataSource.getOffLineMusic(onGetOfflineTrackListener);
    }
}
