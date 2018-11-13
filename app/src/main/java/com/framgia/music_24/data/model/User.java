package com.framgia.music_24.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import static com.framgia.music_24.data.model.User.JsonParamKey.USER_AVATAR_URL;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_CITY;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_FIRST_NAME;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_FULL_NAME;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_ID;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_KIND;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_LAST_MODIFIED;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_LAST_NAME;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_NAME;
import static com.framgia.music_24.data.model.User.JsonParamKey.USER_PERMALINK_URL;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String mAvatarUrl;
    private String mFirstName;
    private String mFullName;
    private String mKind;
    private String mLastModified;
    private String mLastName;
    private String mPermalinkUrl;
    private String mUsername;
    private String mCity;
    private int mId;

    private User(UserBuilder userBuilder) {
        mAvatarUrl = userBuilder.mAvatarUrl;
        mFirstName = userBuilder.mFirstName;
        mFullName = userBuilder.mFullName;
        mId = userBuilder.mId;
        mKind = userBuilder.mKind;
        mLastModified = userBuilder.mLastModified;
        mLastName = userBuilder.mLastName;
        mPermalinkUrl = userBuilder.mPermalinkUrl;
        mUsername = userBuilder.mUsername;
        mCity = userBuilder.mCity;
    }

    User(JSONObject jsonObject) throws JSONException {
        mAvatarUrl = jsonObject.getString(USER_AVATAR_URL);
        mFirstName = jsonObject.getString(USER_FIRST_NAME);
        mFullName = jsonObject.getString(USER_FULL_NAME);
        mKind = jsonObject.getString(USER_KIND);
        mLastModified = jsonObject.getString(USER_LAST_MODIFIED);
        mLastName = jsonObject.getString(USER_LAST_NAME);
        mPermalinkUrl = jsonObject.getString(USER_PERMALINK_URL);
        mUsername = jsonObject.getString(USER_NAME);
        mCity = jsonObject.getString(USER_CITY);
        mId = jsonObject.getInt(USER_ID);
    }

    private User(Parcel in) {
        mAvatarUrl = in.readString();
        mFirstName = in.readString();
        mFullName = in.readString();
        mId = in.readInt();
        mKind = in.readString();
        mLastModified = in.readString();
        mLastName = in.readString();
        mPermalinkUrl = in.readString();
        mUsername = in.readString();
        mCity = in.readString();
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getFullName() {
        return mFullName;
    }

    public int getId() {
        return mId;
    }

    public String getKind() {
        return mKind;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPermalinkUrl() {
        return mPermalinkUrl;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getCity() {
        return mCity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAvatarUrl);
        dest.writeString(mFirstName);
        dest.writeString(mFullName);
        dest.writeInt(mId);
        dest.writeString(mKind);
        dest.writeString(mLastModified);
        dest.writeString(mLastName);
        dest.writeString(mPermalinkUrl);
        dest.writeString(mUsername);
        dest.writeString(mCity);
    }

    public static final class UserBuilder {
        private String mAvatarUrl;
        private String mFirstName;
        private String mFullName;
        private String mKind;
        private String mLastModified;
        private String mLastName;
        private String mPermalinkUrl;
        private String mUsername;
        private String mCity;
        private int mId;

        public UserBuilder() {
        }

        public UserBuilder AvatarUrl(String avatarUrl) {
            mAvatarUrl = avatarUrl;
            return this;
        }

        public UserBuilder FirstName(String firstName) {
            mFirstName = firstName;
            return this;
        }

        public UserBuilder FullName(String fullName) {
            mFullName = fullName;
            return this;
        }

        public UserBuilder Id(int id) {
            mId = id;
            return this;
        }

        public UserBuilder Kind(String kind) {
            mKind = kind;
            return this;
        }

        public UserBuilder LastModified(String lastModified) {
            mLastModified = lastModified;
            return this;
        }

        public UserBuilder LastName(String lastName) {
            mLastName = lastName;
            return this;
        }

        public UserBuilder PermalinkUrl(String permalinkUrl) {
            mPermalinkUrl = permalinkUrl;
            return this;
        }

        public UserBuilder Username(String username) {
            mUsername = username;
            return this;
        }

        public UserBuilder City(String city) {
            mCity = city;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    static class JsonParamKey {
        //User
        static final String USER_AVATAR_URL = "avatar_url";
        static final String USER_FIRST_NAME = "first_name";
        static final String USER_FULL_NAME = "full_name";
        static final String USER_ID = "id";
        static final String USER_KIND = "kind";
        static final String USER_LAST_MODIFIED = "last_modified";
        static final String USER_LAST_NAME = "last_name";
        static final String USER_PERMALINK_URL = "permalink_url";
        static final String USER_NAME = "username";
        static final String USER_CITY = "city";
    }
}
