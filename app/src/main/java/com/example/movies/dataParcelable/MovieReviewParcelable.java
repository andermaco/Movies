package com.example.movies.dataParcelable;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReviewParcelable extends MovieParcelable {

    private String mAuthor;
    private String mContent;

    protected MovieReviewParcelable(Parcel in) {
        super(in);
    }

    public MovieReviewParcelable(JSONObject jsonObject) throws JSONException {
        super(Type.REVIEW);
        mAuthor = jsonObject.getString("author");
        mContent = jsonObject.getString("content");
    }

    public final String getmAuthor() {
        return mAuthor;
    }

    public final void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public final String getmContent() {
        return mContent;
    }

    public final void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
