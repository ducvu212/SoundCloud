package com.framgia.music_24.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.model.User;
import com.framgia.music_24.data.source.TracksDataSource;
import com.framgia.music_24.data.source.local.config.shareprefs.SharedPrefsImpl;
import com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper;
import com.framgia.music_24.data.source.remote.asynctask.GetOfflineTrackAsyncTask;
import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.Intents.Insert.NAME;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.QUERY_ALL_RECODRD;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.ART_URL;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.DATABASE_TABLE_NAME;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.DOWNLOADED;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.FAVORITE;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.TRACK_ID;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.TRACK_SINGER;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.TRACK_URI;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry.TRACK_URL;
import static com.framgia.music_24.data.source.remote.TracksRemoteDataSource.buildStreamUrl;
import static com.framgia.music_24.screens.main.MainActivity.DOWNLOAD_TYPE;
import static com.framgia.music_24.screens.navfragment.NavigationFragment.FAVORITE_TYPE;
import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_DOWNLOADED;
import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_FAVORITE;

/**
 * Created by CuD HniM on 18/09/04.
 */
public class TrackLocalDataSource implements TracksDataSource.TrackLocalDataSource {

    private static final String TRACK_ART_URL = "TRACK_ART_URL";
    private static final String TRACK_STREAM_URL = "TRACK_STREAM_URL";
    private static final String TRACK_GENRE_TYPE = "TRACK_GENRE_TYPE";
    private static final String TAG = TrackLocalDataSource.class.getSimpleName();
    private static TrackLocalDataSource sInstance;
    private TrackDatabaseHelper mHelper;
    private SharedPrefsImpl mSharedPrefs;
    private Context mContext;

    private TrackLocalDataSource(Context context, TrackDatabaseHelper trackDatabaseHelper) {
        mHelper = trackDatabaseHelper;
        mSharedPrefs = SharedPrefsImpl.getInstance(context);
        mContext = context;
    }

    public static synchronized TrackLocalDataSource getInstance(Context context,
            TrackDatabaseHelper trackDatabaseHelper) {
        if (sInstance == null) {
            synchronized (PlaySettingLocalDataSource.class) {
                if (sInstance == null) {
                    sInstance = new TrackLocalDataSource(context, trackDatabaseHelper);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void addTrack(Track track) {
        if (track == null) {
            return;
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, track.getTitle());
        values.put(TRACK_URL, buildStreamUrl(track.getId()));
        values.put(TRACK_ID, track.getId());
        values.put(TRACK_SINGER, track.getUser().getUsername());
        values.put(ART_URL, track.getArtworkUrl());
        values.put(DOWNLOADED, 0);
        values.put(FAVORITE, 0);
        db.insert(DATABASE_TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_ALL_RECODRD, null);
        tracks = createTrack(tracks, cursor);
        cursor.close();
        db.close();
        return tracks;
    }

    private List<Track> createTrack(List<Track> tracks, Cursor cursor) {
        Track track = null;
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                String title = cursor.getString(1);
                String url = cursor.getString(2);
                int id = cursor.getInt(3);
                String singer = cursor.getString(4);
                String art = cursor.getString(5);
                int download = cursor.getInt(6);
                int favorite = cursor.getInt(7);
                String uri = cursor.getString(8);
                track = new Track.TrackBuilder().Title(title)
                        .Downloaded(download)
                        .Url(url)
                        .Id(id)
                        .ArtworkUrl(art)
                        .Favorite(favorite)
                        .User(new User.UserBuilder().Username(singer).build())
                        .DownloadUri(uri)
                        .build();
                if (tracks != null) {
                    tracks.add(track);
                }
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    @Override
    public void updateFavorite(Track track, int fav) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FAVORITE, fav);
        db.update(DATABASE_TABLE_NAME, values, TRACK_ID + "=?",
                new String[] { String.valueOf(track.getId()) });
    }

    @Override
    public boolean isExistRow(Track track) {
        Cursor cursor;
        boolean check;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE TRACK_ID=" + track.getId();
        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            check = true;
        } else {
            check = false;
        }
        cursor.close();
        return check;
    }

    @Override
    public Track findTrackById(String trackId) {
        Cursor cursor;
        Track track = null;
        List<Track> tracks = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE TRACK_ID=" + trackId;
        cursor = db.rawQuery(sql, null);
        if (createTrack(tracks, cursor).size() > 0) {
            track = createTrack(tracks, cursor).get(0);
        }
        cursor.close();
        db.close();
        return track;
    }

    @Override
    public void updateDownload(Track track, int download, String uri) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DOWNLOADED, download);
        values.put(TRACK_URI, uri);
        db.update(DATABASE_TABLE_NAME, values, TRACK_ID + "=?",
                new String[] { String.valueOf(track.getId()) });
    }

    @Override
    public void saveTrackPlayingData(Track track, String url, String type) {
        mSharedPrefs.put(TRACK_ART_URL, track.getArtworkUrl());
        mSharedPrefs.put(TRACK_STREAM_URL, url);
        mSharedPrefs.put(TRACK_GENRE_TYPE, type);
        mSharedPrefs.put(TRACK_ID, track.getId());
    }

    @Override
    public String getTrackUrl() {
        return mSharedPrefs.get(TRACK_STREAM_URL, String.class);
    }

    @Override
    public String getTrackType() {
        return mSharedPrefs.get(TRACK_GENRE_TYPE, String.class);
    }

    @Override
    public int getTrackId() {
        return mSharedPrefs.get(TRACK_ID, Integer.class);
    }

    @Override
    public List<Track> getAllFavorite() {
        return getDataType(FAVORITE_TYPE);
    }

    private List<Track> getDataType(int type) {
        Cursor cursor;
        List<Track> tracks = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql;
        if (type == DOWNLOAD_TYPE) {
            sql = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE DOWNLOAD=" + PLAY_DOWNLOADED;
        } else {
            sql = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE FAVORITE=" + PLAY_FAVORITE;
        }
        cursor = db.rawQuery(sql, null);
        tracks = createTrack(tracks, cursor);
        cursor.close();
        db.close();
        return tracks;
    }

    @Override
    public List<Track> getAllDownload() {
        return getDataType(DOWNLOAD_TYPE);
    }

    @Override
    public void getOffLineMusic(OnGetOfflineTrackListener onGetOfflineTrackListener) {
        new GetOfflineTrackAsyncTask(mContext, onGetOfflineTrackListener).execute();
    }
}
