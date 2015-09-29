package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity
        implements MovieDetailActivityFragment.onFavoritesUpdateListener {

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public final void onFavoritesUpdate() {
        Intent intent = new Intent(MainActivityFragment.class.getSimpleName());
        // Sending message to invalidate menu.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
