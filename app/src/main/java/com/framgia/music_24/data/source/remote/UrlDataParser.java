package com.framgia.music_24.data.source.remote;

import com.framgia.music_24.data.model.Track;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.framgia.music_24.utils.Constants.PARSE_JSON_COLLECTION;
import static com.framgia.music_24.utils.Constants.PARSE_JSON_TRACK;

/**
 * Created by CuD HniM on 18/08/27.
 */
public class UrlDataParser {

    private static final String REQUEST_TYPE = "GET";
    private static final int READ_TIMEOUT = 20000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String LAST_CHARACTER = "\n";

    public static String getJsonFromUrl(String json) throws IOException {
        HttpURLConnection urlConnection;
        URL url;
        String result;
        url = new URL(json);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(REQUEST_TYPE);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        Reader reader = new InputStreamReader(url.openStream());
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line).append(LAST_CHARACTER);
        }
        bufferedReader.close();
        result = builder.toString();
        reader.close();
        urlConnection.disconnect();
        return result;
    }

    public static List<Track> parseJsonTrack(String json) throws JSONException {
        List<Track> tracks = new ArrayList<>();
        JSONObject jsonRoot = new JSONObject(json);
        JSONArray jsonCollection = jsonRoot.getJSONArray(PARSE_JSON_COLLECTION);
        for (int i = 0; i < jsonCollection.length(); i++) {
            Track track =
                    new Track(jsonCollection.getJSONObject(i).getJSONObject(PARSE_JSON_TRACK));
            tracks.add(track);
        }
        return tracks;
    }

    public static List<Track> parseJsonTrackSearch(String json) throws JSONException {
        ArrayList<Track> tracks = new ArrayList<>();
        JSONObject jsonRoot = new JSONObject(json);
        JSONArray jsonCollection = jsonRoot.getJSONArray(PARSE_JSON_COLLECTION);
        for (int i = 0; i < jsonCollection.length(); i++) {
            Track track =
                    new Track(jsonCollection.getJSONObject(i));
            tracks.add(track);
        }
        return tracks;
    }
}
