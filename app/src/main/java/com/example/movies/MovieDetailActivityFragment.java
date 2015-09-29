package com.example.movies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.movies.dataParcelable.MovieDataParcelable;
import com.example.movies.dataParcelable.MovieParcelable;
import com.example.movies.dataParcelable.MovieParcelable.Type;
import com.example.movies.dataParcelable.MovieReviewParcelable;
import com.example.movies.dataParcelable.MovieTrailerParcelable;
import com.example.movies.exception.MovieException;
import com.example.movies.util.ConnectProvider;
import com.example.movies.util.MyAlertDialogFragment;
import com.example.movies.util.TaskParams;
import com.example.movies.util.TaskParams.Function;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MovieDetailActivityFragment extends Fragment {

    private static final String TAG = MovieDetailActivityFragment.class.getSimpleName();
    public static final String FAVORITES_KEY = "FAVORITES";
    public static final String PARCELABLE_KEY = "PARCELABLE";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private MovieDataParcelable mMovieDataParcelable;
    private Set<String> mStringSet;
    private View mDetailView;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSharedPreferences.edit();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDetailView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        return mDetailView;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get movie data
        Bundle bundle = this.getArguments();
        if (bundle == null && mMovieDataParcelable == null) {
            return;
        } else if (bundle != null && bundle.get(MovieDataParcelable.KEY) != null) {
            mMovieDataParcelable = (MovieDataParcelable) bundle.get(MovieDataParcelable.KEY);
        } else {
            return;
        }

        // Set movie data
        TextView movieNameView = (TextView) mDetailView.findViewById(R.id.movie_name);
        movieNameView.setText(mMovieDataParcelable.getTitle());


        final TextView releaseDateView = (TextView) mDetailView.findViewById(R.id.detail_movie_release_date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(mMovieDataParcelable.getRelease());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            releaseDateView.setText(Integer.toString(calendar.get(Calendar.YEAR)));
        } catch (ParseException e) {
            releaseDateView.setText("----");
            Log.e(TAG, "Error parsing release date. ", e);
        }

        TextView ratingView = (TextView) mDetailView.findViewById(R.id.detail_movie_rating);
        ratingView.setText(mMovieDataParcelable.getRating()
                .concat(getResources().getString(R.string.detail_rating_over)));

        final Button favoriteButton = (Button) mDetailView.findViewById(R.id.favorite_button);
        // Getting favorites
        mStringSet =  mSharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<String>());
        if (mStringSet.contains(mMovieDataParcelable.getmId().toString())) {
            favoriteButton.setEnabled(false);
        } else {
            favoriteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringSet.add(mMovieDataParcelable.getmId().toString());
                    mEditor.remove(FAVORITES_KEY).commit();
                    mEditor.putStringSet(FAVORITES_KEY, mStringSet).commit();
                    favoriteButton.setEnabled(false);
                    mCallback.onFavoritesUpdate();

                }
            });
        }

        ImageView detail_imageView = (ImageView) mDetailView.findViewById(R.id.detail_imageView);
        // Piccaso will load the image
        Picasso.with(getActivity()).load(mMovieDataParcelable.getFullPosterPath())
                .error(R.drawable.image_placeholder).into(detail_imageView);

        TextView detail_overview = (TextView) mDetailView.findViewById(R.id.detail_overview);
        detail_overview.setText(mMovieDataParcelable.getOverview());

        // Get Movies
        FetchDBMovieTask fetchDBMovieTask = new FetchDBMovieTask();
        fetchDBMovieTask
                .execute(new TaskParams(mMovieDataParcelable.getmId(), Function.TRAILERS));
        FetchDBMovieTask fetchDBMovieReviewTask = new FetchDBMovieTask();
        fetchDBMovieReviewTask.execute(new TaskParams(mMovieDataParcelable.getmId(), Function.REVIEWS));
    }


    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARCELABLE_KEY, mMovieDataParcelable);
    }

    private void showDialog(String message) {
        FragmentManager fm = getFragmentManager();
        MyAlertDialogFragment dialogFragment = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MyAlertDialogFragment.TAG, message);
        dialogFragment.setArguments(args);
        dialogFragment.show(fm, MyAlertDialogFragment.TAG);
    }

    public final void setParcelableData(MovieDataParcelable movieDataParcelable) {
        // Set movie data
        this.mMovieDataParcelable = movieDataParcelable;
    }


    private onFavoritesUpdateListener mCallback;
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onFavoritesUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onFavoritesUpdateListener");
        }
    }

    public interface onFavoritesUpdateListener {
        void onFavoritesUpdate();
    }

    private class FetchDBMovieTask extends AsyncTask<TaskParams, Void, ArrayList<MovieParcelable>> {
        JSONArray jsonArray = null;
        private MovieException movieException;

        @Override
        protected final ArrayList<MovieParcelable> doInBackground(TaskParams... params) {
            ArrayList<MovieParcelable> movieParcelableArrayList = new ArrayList<>();
            Function type = params[0].getFunction();
            try {
                ConnectProvider connectProvider;

                if (type.equals(Function.TRAILERS)) {
                    connectProvider = new ConnectProvider(getResources()
                            .getString(R.string.dbapiurl_trailer, params[0]
                                    .getmId(), MainActivityFragment.getApiKey(getContext())));
                } else if (type.equals(Function.REVIEWS)) {
                    connectProvider = new ConnectProvider(getResources()
                            .getString(R.string.dbapiurl_review, params[0]
                                    .getmId(), MainActivityFragment.getApiKey(getContext())));
                } else {
                    Log.e(TAG, "Error providing functionality");
                    movieException = new MovieException("Error providing functionality");
                    return null;
                }
                jsonArray = connectProvider.getData().optJSONArray("results");
            } catch (MovieException e) {
                Log.e(TAG, "Error getting movie trailers. ", e);
                movieException = e;
                return null;
            }

            if (type.equals(Function.TRAILERS)) { // TRAILERS
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MovieTrailerParcelable movieTrailerParcelable = new MovieTrailerParcelable(jsonObject);
                        movieParcelableArrayList.add(movieTrailerParcelable);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error building JSONObject from remote file.", e);
                        return null;
                    }
                }
            } else { // REVIEWS
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MovieReviewParcelable movieReviewParcelable = new MovieReviewParcelable(jsonObject);
                        movieParcelableArrayList.add(movieReviewParcelable);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error building JSONObject from remote file.", e);
                        return null;
                    }
                }
            }
            return movieParcelableArrayList;
        }

        @Override
        protected final void onPostExecute(ArrayList<MovieParcelable> movieTrailerParcelables) {
            super.onPostExecute(movieTrailerParcelables);
            if (movieException != null) {
                MovieDetailActivityFragment.this.showDialog(movieException.getMessage());
                return;
            }
            if (movieTrailerParcelables.size() > 0 && movieTrailerParcelables.get(0).getType() == Type.TRAILER) {

                LinearLayout linearLayoutTrailerData = (LinearLayout) mDetailView.findViewById(R.id.movie_trailers);
                linearLayoutTrailerData.setVisibility(View.VISIBLE);

                // Building player views
                LinearLayout linearLayout = (LinearLayout) mDetailView.findViewById(R.id.detail_movie_trailers);


                for (MovieParcelable movieTrailerParcelable1 : movieTrailerParcelables) {
                    final MovieTrailerParcelable movieTrailerParcelable = (MovieTrailerParcelable) movieTrailerParcelable1;
                    LinearLayout linearLayoutHorizontal = new LinearLayout(getActivity());
                    linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 20, 10, 20);
                    linearLayoutHorizontal.setLayoutParams(layoutParams);
                    linearLayoutHorizontal.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"
                                    + movieTrailerParcelable.getKey()));
                            startActivity(intent);
                        }
                    });

                    ImageView imageView = new ImageView(getActivity());
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageResource(R.drawable.ic_play_video);

                    TextView textView = new TextView(getActivity(), null, R.style.detail_small_text);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    textView.setLayoutParams(layoutParams);
                    textView.setText(getResources().getString(R.string.detail_trailer_desc,
                            movieTrailerParcelable.getSize()));
                    textView.setTextAppearance(getActivity(), R.style.detail_half_medium_text);

                    linearLayoutHorizontal.addView(imageView);
                    linearLayoutHorizontal.addView(textView);
                    linearLayout.addView(linearLayoutHorizontal);
                }
            } else if (movieTrailerParcelables.size() > 0 && movieTrailerParcelables.get(0).getType() == Type.REVIEW) {
                LinearLayout linearLayoutReviewData = (LinearLayout) mDetailView.findViewById(R.id.movie_reviews);
                linearLayoutReviewData.setVisibility(View.VISIBLE);

                // Building reviews views
                LinearLayout linearLayout = (LinearLayout) mDetailView.findViewById(R.id.detail_movie_reviews);
                for (MovieParcelable movieTrailerParcelable : movieTrailerParcelables) {
                    final MovieReviewParcelable movieReviewParcelable = (MovieReviewParcelable) movieTrailerParcelable;

                    LinearLayout linearLayoutHorizontal = new LinearLayout(getActivity());
                    linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 20, 10, 20);
                    linearLayoutHorizontal.setLayoutParams(layoutParams);

                    ImageView imageView = new ImageView(getActivity());
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageResource(R.drawable.ic_message_rewiew);

                    TextView textView = new TextView(getActivity(), null, R.style.detail_small_text);
                    layoutParams.gravity = Gravity.TOP;
                    textView.setLayoutParams(layoutParams);
                    textView.setText(movieReviewParcelable.getmContent());
                    textView.setTextAppearance(getActivity(), R.style.detail_half_medium_text);

                    linearLayoutHorizontal.addView(imageView);
                    linearLayoutHorizontal.addView(textView);
                    linearLayout.addView(linearLayoutHorizontal);
                }
            }
        }
    }
}
