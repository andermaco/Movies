package com.example.movies.dataParcelable;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieParcelable implements Parcelable {

    public enum Type {TRAILER, REVIEW}
    private Type type;

    protected MovieParcelable(Parcel in) {
    }

    public MovieParcelable(Type type) {
        this.type = type;
    }

    public static final Creator<MovieParcelable> CREATOR = new Creator<MovieParcelable>() {
        @Override
        public MovieParcelable createFromParcel(Parcel in) {
            return new MovieParcelable(in);
        }

        @Override
        public MovieParcelable[] newArray(int size) {
            return new MovieParcelable[size];
        }
    };

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public final Type getType() {
        return type;
    }

    public final void setType(Type type) {
        this.type = type;
    }
}
