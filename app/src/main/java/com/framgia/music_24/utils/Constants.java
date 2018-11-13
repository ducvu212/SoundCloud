package com.framgia.music_24.utils;

/**
 * Created by CuD HniM on 18/08/24.
 */
public final class Constants {

    //API
    public static final String BASE_URL = "https://api-v2.soundcloud.com/";
    public static final String QUERY_TYPE_KEY = "type";
    public static final String QUERY_KIND = "charts?kind=top";
    public static final String QUERY_GENRE = "&genre=soundcloud";
    public static final String QUERY_TYPE = "%3Agenres%3Atype";
    public static final String CLIENT_ID = "&client_id=";
    public static final String QUERY_LIMIT = "&limit=";
    public static final int LIMIT = 20;

    //Search
    public static final String SEARCH= "search/tracks?q=";
    public static final String QUERY = "query";

    //Genre
    public static final String QUERY_GENRE_ALL_MUSIC = "all-music";
    public static final String QUERY_GENRE_ALL_AUDIO = "all-audio";
    public static final String QUERY_GENRE_ALTERNATIVE_ROCK = "alternativerock";
    public static final String QUERY_GENRE_AMBIENT = "ambient";
    public static final String QUERY_GENRE_CLASSICAL = "classical";
    public static final String QUERY_GENRE_COUNTRY = "country";

    //Display Utils
    public static final String PARSE_JSON_COLLECTION = "collection";
    public static final String PARSE_JSON_TRACK = "track";

    //Stream Music
    public static final String STREAM_URL = "http://api.soundcloud.com/tracks/";
    public static final String STREAM_TRACK_ID = "track_id";
    public static final String STREAM = "/stream";
    public static final String STREAM_CLIENT_ID = "?client_id=";

    public static final String ARROW = " >>";
    public static final String ARGUMENT_GENRE = "GenreData";
}
