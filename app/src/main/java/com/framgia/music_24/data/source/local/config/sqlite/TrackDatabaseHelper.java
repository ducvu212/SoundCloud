package com.framgia.music_24.data.source.local.config.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .ART_URL;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .DATABASE_TABLE_NAME;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .DOWNLOADED;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .FAVORITE;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .ID;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .NAME;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .TRACK_ID;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .TRACK_SINGER;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .TRACK_URI;
import static com.framgia.music_24.data.source.local.config.sqlite.TrackDatabaseHelper.TrackEntry
        .TRACK_URL;

/**
 * Created by CuD HniM on 18/09/04.
 */
public class TrackDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "track_database.sqlite";
    private static int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DATABASE_TABLE_NAME
            + "( "
            + ID
            + " integer primary key, "
            + NAME
            + " TEXT, "
            + TRACK_URL
            + " TEXT, "
            + TRACK_ID
            + " integer, "
            + TRACK_SINGER
            + " TEXT, "
            + ART_URL
            + " TEXT, "
            + DOWNLOADED
            + " integer, "
            + FAVORITE
            + " integer, "
            + TRACK_URI
            + " TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXITS " + DATABASE_TABLE_NAME;
    public static final String QUERY_ALL_RECODRD = "SELECT * FROM " + DATABASE_TABLE_NAME;
    private static TrackDatabaseHelper sInstance;

    TrackDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TrackDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TrackDatabaseHelper(checkNotNull(context));
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public static final class TrackEntry implements BaseColumns {
        public static final String DATABASE_TABLE_NAME = "track";
        static final String ID = "id";
        static final String NAME = "name";
        public static final String TRACK_URL = "track_url";
        public static final String ART_URL = "art_url";
        public static final String TRACK_ID = "track_id";
        public static final String DOWNLOADED = "download";
        public static final String FAVORITE = "favorite";
        public static final String TRACK_URI = "track_uri";
        public static final String TRACK_SINGER = "track_singer";
    }
}
