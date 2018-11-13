package com.framgia.music_24.data.source.remote.asynctask;

import android.os.AsyncTask;
import com.framgia.music_24.data.model.Track;
import com.framgia.music_24.data.source.CallBack;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

import static com.framgia.music_24.data.source.remote.UrlDataParser.getJsonFromUrl;
import static com.framgia.music_24.data.source.remote.UrlDataParser.parseJsonTrackSearch;

/**
 * Created by CuD HniM on 18/09/09.
 */
public class SearchAsyncTask extends AsyncTask<String, Void, List<Track>> {

    private CallBack mCallBack;
    private Exception mException;

    public SearchAsyncTask(CallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    protected List<Track> doInBackground(String... strings) {
        List<Track> tracks = new ArrayList<>();
        try {
            String json = getJsonFromUrl(strings[0]);
            tracks = parseJsonTrackSearch(json);
        } catch (IOException e) {
            e.printStackTrace();
            mException = e;
        } catch (JSONException e) {
            e.printStackTrace();
            mException = e;
        }
        return tracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        if (mException == null) {
            mCallBack.onSuccess(tracks);
        } else {
            mCallBack.onError(mException);
        }
    }
}
