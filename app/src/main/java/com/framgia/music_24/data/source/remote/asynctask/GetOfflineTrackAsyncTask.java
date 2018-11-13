package com.framgia.music_24.data.source.remote.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.model.User;
import com.framgia.music_24.data.source.TracksDataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class GetOfflineTrackAsyncTask extends AsyncTask<Void, Void, List<Track>> {

    private Context mContext;
    private List<Track> mTracks;
    private TracksDataSource.TrackLocalDataSource.OnGetOfflineTrackListener mListener;
    private Exception mException;

    public GetOfflineTrackAsyncTask(Context context, TracksDataSource.TrackLocalDataSource.OnGetOfflineTrackListener listener) {
        mContext = context;
        mTracks = new ArrayList<>();
        mListener = listener;
    }

    @Override
    protected List<Track> doInBackground(Void... voids) {
        String list[] = new String[] {
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, list, null, null, null);
        if (cursor == null) {
            return null;
        }
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexAlbumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String path = cursor.getString(indexData);
            String title = cursor.getString(indexTitle);
            String artist = cursor.getString(indexArtist);
            long id = cursor.getLong(indexAlbumId);
            Bitmap bitmap = getAlbumart(Uri.parse(path));
            mTracks.add(new Track.TrackBuilder()
                    .DownloadUri(path)
                    .Title(title)
                    .User(new User.UserBuilder().Username(artist).build())
                    .Id((int) id)
                    .Bitmap(bitmap)
                    .Offline(true)
                    .build());
            cursor.moveToNext();
        }
        cursor.close();
        return mTracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        mContext = null;
        if(tracks.size() > 0) {
            mListener.OnSuccess(tracks);
        } else {
            mListener.OnError();
        }
    }

    private Bitmap getAlbumart(Uri uri) {
        Bitmap art = null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            mmr.setDataSource(mContext, uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt) {
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            }
        }catch (Exception e) {
            mException = e;
        }
        return art;
    }
}
