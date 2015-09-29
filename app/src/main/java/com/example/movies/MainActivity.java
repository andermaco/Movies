package com.example.movies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.example.movies.dataParcelable.MovieDataParcelable;
import com.example.movies.util.Util;

public class MainActivity extends AppCompatActivity
        implements  MainActivityFragment.OnGridSelectedListener, MovieDetailActivityFragment.onFavoritesUpdateListener {

    private static final String TAG = MainActivity.class.getName();
    private ViewGroup mImageSelectorLayout;
    private ViewGroup mImageRotatorLayout;
    private MovieDataParcelable movieDataParcelable;
    private MainActivityFragment mainActivityFragment;
    private MovieDetailActivityFragment movieDetailActivityFragment;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_main);
        if (savedInstanceState == null) {
            mainActivityFragment = new MainActivityFragment();
            movieDetailActivityFragment = new MovieDetailActivityFragment();

            if (Util.is_phone(getResources())) {
                // Checks if our layout has a container for MainActivityFragment
                mImageSelectorLayout = (ViewGroup) findViewById(R.id.activity_main_image_selector_container);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.add(mImageSelectorLayout.getId()
                        , mainActivityFragment, MainActivityFragment.class.getName());
                fragmentTransaction.commit();
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
                    // Checks if our layout has a container for MainActivityFragment
                    mImageSelectorLayout = (ViewGroup) findViewById(R.id.activity_main_image_selector_container);
                    if (mImageSelectorLayout != null) {

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                .beginTransaction();
                        fragmentTransaction.add(mImageSelectorLayout.getId()
                                , mainActivityFragment, MainActivityFragment.class.getName());
                        fragmentTransaction.commit();
                    }
                    mImageRotatorLayout = (ViewGroup) findViewById(R.id.activity_main_image_rotate_container);
                    if (mImageSelectorLayout != null) {

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                .beginTransaction();
                        fragmentTransaction.add(mImageRotatorLayout.getId()
                                , movieDetailActivityFragment, MovieDetailActivityFragment.class.getName());
                        fragmentTransaction.commit();
                    }

                } else { // Portratit mode
                    // Checks if our layout has a container for MainActivityFragment
                    mImageSelectorLayout = (ViewGroup) findViewById(R.id.activity_main_image_selector_container);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(mImageSelectorLayout.getId()
                            , mainActivityFragment, MainActivityFragment.class.getName());
                    fragmentTransaction.commit();
                }
            }
        }
    }

    @Override
    public final void onMoviePosterSelected(MovieDataParcelable movieDataParcelable) {

        this.movieDataParcelable = movieDataParcelable;

        mImageSelectorLayout = (ViewGroup) findViewById(R.id.activity_main_image_selector_container);
        mImageRotatorLayout = (ViewGroup) findViewById(R.id.activity_main_image_rotate_container);

        Log.i(TAG, getResources().getString(R.string.screen_type));
        if (Util.is_phone(getResources())) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDataParcelable.KEY, movieDataParcelable);

            movieDetailActivityFragment = new MovieDetailActivityFragment();
            movieDetailActivityFragment.setArguments(bundle);
            fragmentTransaction.replace(mImageSelectorLayout.getId(), movieDetailActivityFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (!Util.is_phone(getResources())) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDataParcelable.KEY, movieDataParcelable);
                movieDetailActivityFragment = new MovieDetailActivityFragment();
                movieDetailActivityFragment.setArguments(bundle);
                fragmentTransaction.add(mImageRotatorLayout.getId(), movieDetailActivityFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDataParcelable.KEY, movieDataParcelable);

                movieDetailActivityFragment = new MovieDetailActivityFragment();
                movieDetailActivityFragment.setArguments(bundle);
                fragmentTransaction.replace(mImageRotatorLayout.getId(), movieDetailActivityFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onFavoritesUpdate() {
        invalidateOptionsMenu();
    }

}
