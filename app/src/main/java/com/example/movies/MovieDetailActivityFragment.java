package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Get movie data
        Intent detailIntent = getActivity().getIntent();
        MovieDataParcelable movieDataParcelable = detailIntent
                .getParcelableExtra(MovieDataParcelable.KEY);

        // Set movie data
        TextView titleView = (TextView) detailView.findViewById(R.id.detail_movie_title);
        titleView.setText(movieDataParcelable.getTitle());

        TextView releaseDateView = (TextView) detailView.findViewById(R.id.detail_movie_release_date);
        releaseDateView.setText(movieDataParcelable.getRelease());

        TextView ratingView = (TextView) detailView.findViewById(R.id.detail_movie_rating);
        ratingView.setText(movieDataParcelable.getRating());

        ImageView detail_imageView = (ImageView) detailView.findViewById(R.id.detail_imageView);
        // Piccaso will load the image
        Picasso.with(getActivity()).load(movieDataParcelable.getFullPosterPath())
                .error(R.drawable.image_placeholder).into(detail_imageView);

        TextView detail_overview = (TextView) detailView.findViewById(R.id.detail_overview);
        detail_overview.setText(movieDataParcelable.getOverview());

        return detailView;
    }
}
