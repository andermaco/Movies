package com.example.movies.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.movies.R;
import com.example.movies.dataParcelable.MovieDataParcelable;
import com.example.movies.util.Util;
import com.squareup.picasso.Picasso;

import java.util.AbstractList;
import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private AbstractList<MovieDataParcelable> mMovieDataParcel;

    public ImageAdapter(Context context, ArrayList<MovieDataParcelable> imageUrls) {
        this.mContext = context;
        this.mMovieDataParcel = imageUrls;
    }

    @Override
    public final int getCount() {
        return mMovieDataParcel==null?0:mMovieDataParcel.size();
    }

    @Override
    public final Object getItem(int i) {
        return mMovieDataParcel.get(i);
    }

    @Override
    public final long getItemId(int i) {
        return 0;
    }

    @Override
    public final View getView(final int position, View view, ViewGroup viewGroup) {
        final ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            if (mContext.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT) {
                imageView.setScaleType(ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(mMovieDataParcel.get(position).getFullPosterPath()).error(R.drawable.image_placeholder).into(imageView);

            } else {
                if (Util.is_phone(mContext.getResources())) {
                    imageView.setScaleType(ScaleType.FIT_XY);
                    Picasso.with(mContext)
                            .load(mMovieDataParcel.get(position).getFullPosterPath())
                            .into(imageView);
                } else {
                    imageView.setScaleType(ScaleType.FIT_XY);
                    Picasso.with(mContext)
                            .load(mMovieDataParcel.get(position).getFullPosterPath())
                            .resize(300, imageView.getHeight())
                            .noFade()
                            .into(imageView);
                }
            }
        } else {
            imageView = (ImageView) view;
        }
        return imageView;
    }
}
