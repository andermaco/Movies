package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {

    private static final String TAG = ImageAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<MovieDataParcelable> mMovieDataParcel;
    private LayoutInflater mLayoutInflater;

    public ImageAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public ImageAdapter(Context context, ArrayList<MovieDataParcelable> imageUrls) {
        this.mContext = context;
        this.mMovieDataParcel = imageUrls;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mMovieDataParcel.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) view;
        }
        // Piccaso will load the image
        Picasso.with(mContext).load(mMovieDataParcel.get(position).getFullPosterPath()).error(R.drawable.image_placeholder).into(imageView);
        // Call details activity
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra(MovieDataParcelable.KEY, mMovieDataParcel.get(position));
                mContext.startActivity(intent);
            }
        });

        return imageView;
    }

    public void setImageUrls(ArrayList<MovieDataParcelable> imageUrls) {
        this.mMovieDataParcel = imageUrls;
    }
}
