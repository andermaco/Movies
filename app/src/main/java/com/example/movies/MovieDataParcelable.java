package com.example.movies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDataParcelable implements Parcelable {

    private String mTitle;
    private String mPoster;
    private String mOverview;
    private String mRelease;
    private String mRating;
    private static final String IMAGE_SRC = "http://image.tmdb.org/t/p/w500/";

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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mRelease);
        parcel.writeString(mRating);
    }

    public String getFullPosterPath () {
        return IMAGE_SRC.concat(getmPoster());
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPoster() {
        return mPoster;
    }

    public String getmOverview() {
        return mOverview;
    }

    public String getmRelease() {
        return mRelease;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setmRelease(String mRelease) {
        this.mRelease = mRelease;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }
}
