package com.framgia.music_24.data.source.local.config.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.bumptech.glide.util.Preconditions.checkNotNull;
import static com.framgia.music_24.data.source.local.config.sqlite.PlaylistDatabaseHelper.PlaylistEntry.DATABASE_TABLE_NAME;
import static com.framgia.music_24.data.source.local.config.sqlite.PlaylistDatabaseHelper.PlaylistEntry.ID;
import static com.framgia.music_24.data.source.local.config.sqlite.PlaylistDatabaseHelper.PlaylistEntry.NAME;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class PlaylistDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "track_database.sqlite";
    private static int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + DATABASE_TABLE_NAME
            + "( "
            + ID
            + " integer primary key, "
            + NAME
            + " TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXITS " + DATABASE_TABLE_NAME;
    public static final String QUERY_ALL_RECODRD = "SELECT * FROM " + DATABASE_TABLE_NAME;
    private static TrackDatabaseHelper sInstance;

    private PlaylistDatabaseHelper(Context context) {
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

    static final class PlaylistEntry implements BaseColumns {
        static final String DATABASE_TABLE_NAME = "playlist";
        static final String ID = "id";
        static final String NAME = "name";
    }
}
