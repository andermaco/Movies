package com.example.movies.dataParcelable;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDataParcelable implements Parcelable {

    public static final String KEY = "MOVIEDATA";
    public static final Parcelable.Creator<MovieDataParcelable> CREATOR = new ClassLoaderCreator<MovieDataParcelable>() {
        @Override
        public MovieDataParcelable createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new MovieDataParcelable(parcel);
        }

        @Override
        public MovieDataParcelable createFromParcel(Parcel parcel) {
            return new MovieDataParcelable(parcel);
        }

        @Override
        public MovieDataParcelable[] newArray(int i) {
            return new MovieDataParcelable[i];
        }
    };
    private static final String IMAGE_SRC = "http://image.tmdb.org/t/p/w500/";

    private Integer mId;
    private String mTitle;
    private String mPoster;
    private String mOverview;
    private String mRelease;
    private String mRating;

    protected MovieDataParcelable(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mOverview = in.readString();
        mRelease = in.readString();
        mRating = in.readString();
    }

    public MovieDataParcelable(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.getInt("id");
        mTitle = jsonObject.getString("original_title");
        mPoster = jsonObject.getString("poster_path");
        mOverview = jsonObject.getString("overview");
        mRelease = jsonObject.getString("release_date");
        mRating = jsonObject.getString("vote_average");
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mRelease);
        parcel.writeString(mRating);
    }

    public final String getFullPosterPath() {
        return IMAGE_SRC.concat(getPoster());
    }

    public final String getTitle() {
        return mTitle;
    }

    public final void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    private String getPoster() {
        return mPoster;
    }

    public final void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public final String getOverview() {
        return mOverview;
    }

    public final void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public final String getRelease() {
        return mRelease;
    }

    public final void setRelease(String mRelease) {
        this.mRelease = mRelease;
    }

    public final String getRating() {
        return mRating;
    }

    public final void setRating(String mRating) {
        this.mRating = mRating;
    }

    public final Integer getmId() {
        return mId;
    }

    public final void setmId(Integer mId) {
        this.mId = mId;
    }
}
