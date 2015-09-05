package com.example.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {

    private static final String TAG = ImageAdapter.class.getSimpleName();

    public void setContext(Context context) {
        this.mContext = context;
    }

    private Context mContext;
    private ArrayList<MovieDataParcelable> mImageUrls;
    private LayoutInflater mLayoutInflater;

    public ImageAdapter (Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public ImageAdapter (Context context, ArrayList<MovieDataParcelable> imageUrls) {
        this.mContext = context;
        this.mImageUrls = imageUrls;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public Object getItem(int i) {
//        return mImageUrls.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) view;
        }

        Picasso.with(mContext).load(mImageUrls.get(position).getFullPosterPath()).error(R.drawable.image_placeholder).into(imageView);;
        return imageView;
    }

    public void setImageUrls(ArrayList<MovieDataParcelable> imageUrls) {
        this.mImageUrls = imageUrls;
    }
}
