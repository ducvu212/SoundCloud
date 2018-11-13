package com.framgia.music_24.data.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_ARTWORK_URL;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_CREATED_AT;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_DESCRIPTION;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_DISPLAY_DATE;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_DURATION;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_FULL_DURATION;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_ID;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_LAST_MODIFIED;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_TITLE;
import static com.framgia.music_24.data.model.Track.JsonParamKey.TRACK_USER;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class Track implements Parcelable {

    private String mArtworkUrl;
    private String mCreatedAt;
    private String mDescription;
    private String mLastModified;
    private String mTitle;
    private String mDisplayDate;
    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
    private int mDuration;
    private int mFullDuration;
    private int mId;
    private String mUrl;
    private int mDownloaded;
    private User mUser;
    private int mFavorite;
    private String mDownloadUri;
    private Bitmap mBitmap;
    private boolean mIsOffline;

    public Track(JSONObject jsonObject) throws JSONException {
        mArtworkUrl = jsonObject.getString(TRACK_ARTWORK_URL);
        mCreatedAt = jsonObject.getString(TRACK_CREATED_AT);
        mDescription = jsonObject.getString(TRACK_DESCRIPTION);
        mLastModified = jsonObject.getString(TRACK_LAST_MODIFIED);
        mDisplayDate = jsonObject.getString(TRACK_DISPLAY_DATE);
        mTitle = jsonObject.getString(TRACK_TITLE);
        mDuration = jsonObject.getInt(TRACK_DURATION);
        mFullDuration = jsonObject.getInt(TRACK_FULL_DURATION);
        mId = jsonObject.getInt(TRACK_ID);
        mUser = new User(jsonObject.getJSONObject(TRACK_USER));
    }

    private Track(TrackBuilder trackBuilder) {
        mArtworkUrl = trackBuilder.mArtworkUrl;
        mCreatedAt = trackBuilder.mCreatedAt;
        mDescription = trackBuilder.mDescription;
        mLastModified = trackBuilder.mLastModified;
        mTitle = trackBuilder.mTitle;
        mDisplayDate = trackBuilder.mDisplayDate;
        mDuration = trackBuilder.mDuration;
        mFullDuration = trackBuilder.mFullDuration;
        mId = trackBuilder.mId;
        mDownloaded = trackBuilder.mDownloaded;
        mFavorite = trackBuilder.mFavorite;
        mUser = trackBuilder.mUser;
        mUrl = trackBuilder.mUrl;
        mDownloadUri = trackBuilder.mDownloadUri;
        mBitmap = trackBuilder.mBitmap;
        mIsOffline = trackBuilder.mIsOffline;
    }

    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setId(int id) {
        mId = id;
    }

    protected Track(Parcel in) {
        mArtworkUrl = in.readString();
        mCreatedAt = in.readString();
        mDescription = in.readString();
        mLastModified = in.readString();
        mTitle = in.readString();
        mDisplayDate = in.readString();
        mDuration = in.readInt();
        mFullDuration = in.readInt();
        mId = in.readInt();
        mDownloaded = in.readInt();
        mFavorite = in.readInt();
        mUser = in.readParcelable(User.class.getClassLoader());
    }

    public static Creator<Track> getCREATOR() {
        return CREATOR;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public String getDownloadUri() {
        return mDownloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        mDownloadUri = downloadUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getFullDuration() {
        return mFullDuration;
    }

    public int getId() {
        return mId;
    }

    public String getDisplayDate() {
        return mDisplayDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public User getUser() {
        return mUser;
    }

    public int getDownloaded() {
        return mDownloaded;
    }

    public void setDownloaded(int downloaded) {
        mDownloaded = downloaded;
    }

    public int getFavorite() {
        return mFavorite;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public boolean isOffline() {
        return mIsOffline;
    }

    public void setOffline(boolean offline) {
        mIsOffline = offline;
    }

    static class JsonParamKey {
        //Track
        static final String TRACK_ARTWORK_URL = "artwork_url";
        static final String TRACK_CREATED_AT = "created_at";
        static final String TRACK_DESCRIPTION = "description";
        static final String TRACK_LAST_MODIFIED = "last_modified";
        static final String TRACK_DISPLAY_DATE = "display_date";
        static final String TRACK_TITLE = "title";
        static final String TRACK_DURATION = "duration";
        static final String TRACK_FULL_DURATION = "full_duration";
        static final String TRACK_ID = "id";
        static final String TRACK_USER = "user";
    }

    public void setFavorite(int favorite) {
        mFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtworkUrl);
        dest.writeString(mCreatedAt);
        dest.writeString(mDescription);
        dest.writeString(mLastModified);
        dest.writeString(mTitle);
        dest.writeString(mDisplayDate);
        dest.writeInt(mDuration);
        dest.writeInt(mFullDuration);
        dest.writeInt(mId);
        dest.writeInt(mDownloaded);
        dest.writeInt(mFavorite);
        dest.writeParcelable(mUser, flags);
    }

    public static final class TrackBuilder {
        private String mArtworkUrl;
        private String mCreatedAt;
        private String mDescription;
        private String mLastModified;
        private String mTitle;
        private String mDisplayDate;
        private String mUrl;
        private String mDownloadUri;
        private int mDuration;
        private int mFullDuration;
        private int mId;
        private int mDownloaded;
        private int mFavorite;
        private User mUser;
        private Bitmap mBitmap;
        private boolean mIsOffline;

        public TrackBuilder() {
        }

        public TrackBuilder ArtworkUrl(String artworkUrl) {
            mArtworkUrl = artworkUrl;
            return this;
        }

        public TrackBuilder CreatedAt(String createdAt) {
            mCreatedAt = createdAt;
            return this;
        }

        public TrackBuilder Description(String description) {
            mDescription = description;
            return this;
        }

        public TrackBuilder LastModified(String lastModified) {
            mLastModified = lastModified;
            return this;
        }

        public TrackBuilder Title(String title) {
            mTitle = title;
            return this;
        }

        public TrackBuilder DisplayDate(String displayDate) {
            mDisplayDate = displayDate;
            return this;
        }

        public TrackBuilder Duration(int duration) {
            mDuration = duration;
            return this;
        }

        public TrackBuilder FullDuration(int fullDuration) {
            mFullDuration = fullDuration;
            return this;
        }

        public TrackBuilder Id(int id) {
            mId = id;
            return this;
        }

        public TrackBuilder Bitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            return this;
        }

        public TrackBuilder Offline(boolean isOffline) {
            mIsOffline = isOffline;
            return this;
        }

        public TrackBuilder Downloaded(int downloaded) {
            mDownloaded = downloaded;
            return this;
        }

        public TrackBuilder Favorite(int favorite) {
            mFavorite = favorite;
            return this;
        }

        public TrackBuilder User(User user) {
            mUser = user;
            return this;
        }

        public TrackBuilder Url(String url) {
            mUrl = url;
            return this;
        }

        public TrackBuilder DownloadUri(String uri) {
            mDownloadUri = uri;
            return this;
        }

        public Track build() {
            return new Track(this);
        }
    }
}
