package com.framgia.music_24.screens.play;

import android.graphics.Bitmap;
import com.framgia.music_24.data.model.Setting;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.repository.PlaySettingRepository;
import com.framgia.music_24.data.repository.TracksRepository;
import com.framgia.music_24.data.source.TracksDataSource;

/**
 * Created by CuD HniM on 18/08/29.
 */
public class PlayMusicPresenter implements PlayMusicContract.Presenter {

    private PlayMusicContract.View mView;
    private PlaySettingRepository mRepository;
    private TracksRepository mTracksRepository;

    public PlayMusicPresenter(PlaySettingRepository repository, TracksRepository tracksRepository) {
        mRepository = repository;
        mTracksRepository = tracksRepository;
    }

    @Override
    public void setView(PlayMusicContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void saveSetting(Setting setting) {
        mRepository.saveSetting(setting);
    }

    @Override
    public Setting getSetting() {
        return mRepository.getSetting();
    }

    @Override
    public void editFavorite(Track track, int fav) {
        mTracksRepository.updateFavorite(track, fav);
    }

    @Override
    public void addTracks(Track track) {
        mTracksRepository.addTrack(track);
    }

    @Override
    public boolean isExistRow(Track track) {
        return mTracksRepository.isExistRow(track);
    }

    @Override
    public void findTrackById(String id) {
        mView.initData(mTracksRepository.findTrackById(id));
    }

    @Override
    public void editDownload(Track track, int download, String uri) {
        mTracksRepository.updateDownload(track, download, uri);
    }

    @Override
    public void downloadTrack(String title) {
        mTracksRepository.downloadTrack(title,
                new TracksDataSource.TrackRemoteDataSource.OnDownloadListener() {
                    @Override
                    public void OnSuccess(String url) {
                        mView.downloadSuccess(url);
                    }

                    @Override
                    public void OnError(String e) {
                        mView.downloadError(e);
                    }
                });
    }
    
    @Override
    public void convertBitmap(String url) {
        mTracksRepository.convertBitmap(url,
                new TracksDataSource.TrackRemoteDataSource.OnConvertBitmapListener() {
                    @Override
                    public void OnSuccess(Bitmap bitmap) {
                        mView.convertSuccess(bitmap);
                    }

                    @Override
                    public void OnError(Exception e) {
                        mView.downloadError(e.toString());
                        mView.convertSuccess(null);
                    }
                });
    }
}
