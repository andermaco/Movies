package com.example.movies.dataParcelable;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieTrailerParcelable extends MovieParcelable {

    private String mName;
    private String mKey;
    private String mSite;
    private String mSize;
    private String mType;


    protected MovieTrailerParcelable(Parcel in) {
        super(in);
    }

    public MovieTrailerParcelable(JSONObject jsonObject) throws JSONException {
        super(Type.TRAILER);
        mName = jsonObject.getString("name");
        mKey = jsonObject.getString("key");
        mSite = jsonObject.getString("site");
        mSize = jsonObject.getString("size");
        mType = jsonObject.getString("type");
    }


    public final String getName() {
        return mName;
    }

    public final void setName(String mName) {
        this.mName = mName;
    }

    public final String getKey() {
        return mKey;
    }

    public final void setKey(String mKey) {
        this.mKey = mKey;
    }

    public final String getSite() {
        return mSite;
    }

    public final void setSite(String mSite) {
        this.mSite = mSite;
    }

    public final String getSize() {
        return mSize;
    }

    public final void setSize(String mSize) {
        this.mSize = mSize;
    }

    public final String getmType() {
        return mType;
    }

    public final void setType(String mType) {
        this.mType = mType;
    }
}
