package com.framgia.music_24.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class Playlist implements Parcelable {
    private String mName;

    private Playlist(PlaylistBuilder playlistBuilder) {
        setName(playlistBuilder.mName);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
    }

    public Playlist() {
    }

    private Playlist(Parcel in) {
        this.mName = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public static final class PlaylistBuilder {
        private String mName;

        public PlaylistBuilder() {
        }

        public PlaylistBuilder mName(String val) {
            mName = val;
            return this;
        }

        public Playlist build() {
            return new Playlist(this);
        }
    }
}
