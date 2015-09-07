package com.example.movies;

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
    private String mTitle;
    private String mPoster;
    private String mOverview;
    private String mRelease;
    private String mRating;

    protected MovieDataParcelable(Parcel in) {
        mTitle = in.readString();
        mPoster = in.readString();
        mOverview = in.readString();
        mRelease = in.readString();
        mRating = in.readString();
    }

    public MovieDataParcelable(JSONObject jsonObject) throws JSONException {
        mTitle = jsonObject.getString("original_title");
        mPoster = jsonObject.getString("poster_path");
        mOverview = jsonObject.getString("overview");
        mRelease = jsonObject.getString("release_date");
        mRating = jsonObject.getString("vote_average");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mRelease);
        parcel.writeString(mRating);
    }

    public String getFullPosterPath() {
        return IMAGE_SRC.concat(getPoster());
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getRelease() {
        return mRelease;
    }

    public void setRelease(String mRelease) {
        this.mRelease = mRelease;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String mRating) {
        this.mRating = mRating;
    }
}
