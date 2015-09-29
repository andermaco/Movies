package com.example.movies;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.movies.adapter.ImageAdapter;
import com.example.movies.dataParcelable.MovieDataParcelable;
import com.example.movies.exception.MovieException;
import com.example.movies.util.ConnectProvider;
import com.example.movies.util.MyAlertDialogFragment;
import com.example.movies.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String MOVIE_KEY = "MOVIES";
    private static final String SORT_KEY = "SORT";
    private ArrayList<MovieDataParcelable> mArrayList;
    private Menu sMenu;
    private SharedPreferences mPreferences;
    private GridView gridview;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            // Load default movie selection
            loadMovies(getResources().getString(R.string.menu_most_popular));
            mPreferences.edit().putString(SORT_KEY, getResources().getString(R.string.menu_most_popular)).apply();
        }
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_grid_layout, container, false);
        gridview = (GridView) root.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onMoviePosterSelected((MovieDataParcelable) parent.getAdapter().getItem(position));
            }
        });
        // Defining handler and filter for broadcasting messages
        IntentFilter mReceiveFilter = new IntentFilter(TAG);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(handler, mReceiveFilter);

        return root;
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load poster from state saved
        if (savedInstanceState != null) {
            mArrayList = (ArrayList<MovieDataParcelable>) savedInstanceState.get(MOVIE_KEY);
        }
        ImageAdapter mImageAdapter = new ImageAdapter(getActivity(), mArrayList);
        gridview.setAdapter(mImageAdapter);
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(handler);
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, mArrayList);
    }

    @Override
    public final void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        Set<String> stringSet = getFavorites(mPreferences);
        if (mPreferences.getString(SORT_KEY, "")
                .equals(getResources().getString(R.string.menu_most_popular))) {
            menu.findItem(R.id.menu_highest_rated).setVisible(true);
            menu.findItem(R.id.menu_most_popular).setVisible(false);
            // Enabling favorites menu if favorites found.
            if (stringSet.size() > 0) {
                menu.findItem(R.id.menu_favorites).setVisible(true);
            }
        } else if (mPreferences.getString(SORT_KEY, "")
                .equals(getResources().getString(R.string.menu_highest_rated))){
            menu.findItem(R.id.menu_most_popular).setVisible(true);
            menu.findItem(R.id.menu_highest_rated).setVisible(false);
            // Enabling favorites menu if favorites found.
            if (stringSet.size() > 0) {
                menu.findItem(R.id.menu_favorites).setVisible(true);
            }
        } else if (mPreferences.getString(SORT_KEY, "")
                .equals(getResources().getString(R.string.menu_favorites))){
            menu.findItem(R.id.menu_most_popular).setVisible(true);
            menu.findItem(R.id.menu_highest_rated).setVisible(true);
            menu.findItem(R.id.menu_favorites).setVisible(false);
        }
        this.sMenu = menu;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_most_popular:
                item.setVisible(false);
                sMenu.findItem(R.id.menu_highest_rated).setVisible(true);
                if (getFavorites(mPreferences).size() > 0) {
                    sMenu.findItem(R.id.menu_favorites).setVisible(true);
                }
                mPreferences.edit().putString(SORT_KEY,
                        getResources().getString(R.string.menu_most_popular)).apply();
                loadMovies(getResources().getString(R.string.menu_most_popular));
                return true;
            case R.id.menu_highest_rated:
                item.setVisible(false);
                sMenu.findItem(R.id.menu_most_popular).setVisible(true);
                if (getFavorites(mPreferences).size() > 0) {
                    sMenu.findItem(R.id.menu_favorites).setVisible(true);
                }
                mPreferences.edit().putString(SORT_KEY,
                        getResources().getString(R.string.menu_highest_rated)).apply();
                loadMovies(getResources().getString(R.string.menu_highest_rated));
                return true;
            case R.id.menu_favorites:
                item.setVisible(false);
                sMenu.findItem(R.id.menu_highest_rated).setVisible(true);
                sMenu.findItem(R.id.menu_most_popular).setVisible(true);
                mPreferences.edit().putString(SORT_KEY,
                        getResources().getString(R.string.menu_favorites)).apply();
                loadMovies(getResources().getString(R.string.menu_favorites));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Implementing the way of getting communicated with its activity
    private OnGridSelectedListener mCallback;
    // Container Activity must implement this interface
    public interface OnGridSelectedListener {
        void onMoviePosterSelected(MovieDataParcelable movieDataParcelable);
    }
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGridSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGridSelectedListener");
        }
    }


    private static Set<String> getFavorites(SharedPreferences preferences) {
        return preferences.getStringSet(MovieDetailActivityFragment.FAVORITES_KEY,
                new HashSet<String>());
    }

    public static String getApiKey (Context context) throws MovieException {
        String key;
        // Read tmdb.org key
        try {
            key = Util.getApiKeyProperty(context);
            if (key == null || key.equals("")) {
                throw new MovieException(context.getString(R.string.dbapi_wrong_params));
            }
        } catch (IOException e) {
            throw new MovieException(context.getString(R.string.dbapi_wrong_params));
        }
        return key;
    }

    private void loadMovies(String target) {
        FetchDBMovieTask fetchDBMovieTask = new FetchDBMovieTask();
        fetchDBMovieTask.execute(target);
    }

    private void showDialog(String message) {
        FragmentManager fm = getFragmentManager();
        MyAlertDialogFragment dialogFragment = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MyAlertDialogFragment.TAG, message);
        dialogFragment.setArguments(args);
        dialogFragment.show(fm, MyAlertDialogFragment.TAG);
    }

    // Receiver for invalidate menu requests.
    private final BroadcastReceiver handler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().invalidateOptionsMenu();
        }
    };

    private class FetchDBMovieTask extends AsyncTask<String, Void, ArrayList<MovieDataParcelable>> {
        private final String TAG = FetchDBMovieTask.class.getSimpleName();
        private MovieException movieException;

        @Override
        protected final ArrayList<MovieDataParcelable> doInBackground(String... strings) {
            mArrayList = new ArrayList<>();
            JSONArray json_array = null;


            if (strings[0].equals(getResources().getString(R.string.menu_most_popular))) {
                try {
                    // Getting MovieData
                    json_array = getMoviesData(getResources().getString(R.string.dbapiurl_discover,
                            getResources().getString(R.string.dbapiurl_discover_popularity),
                            getApiKey(getContext()))).optJSONArray("results");
                } catch (MovieException e) {
                    movieException = e;
                    return null;
                }
            } else if (strings[0].equals(getResources().getString(R.string.menu_highest_rated))) {
                try {
                    // Getting MovieData
                    json_array = getMoviesData(getResources().getString(R.string.dbapiurl_discover,
                            getResources().getString(R.string.dbapiurl_discover_rate),
                            getApiKey(getContext()))).optJSONArray("results");
                } catch (MovieException e) {
                    movieException = e;
                    return null;
                }
            } else if (strings[0].equals(getResources().getString(R.string.menu_favorites))) {
                try {
                    // Getting MovieData
                    json_array = new JSONArray();
                    for (String movieId : getFavorites(mPreferences)) {
                        JSONObject jsonObject = getMoviesData(getResources()
                                .getString(R.string.dbapiurl_movie, movieId
                                        , getApiKey(getContext())));

                        json_array.put(jsonObject);
                    }
                } catch (MovieException e) {
                    movieException = e;
                    return null;
                }
            }

            for (int i = 0; i < (json_array != null ? json_array.length() : 0); i++) {
                try {
                    JSONObject jsonObject = json_array.getJSONObject(i);
                    MovieDataParcelable movieDataParcelable = new MovieDataParcelable(jsonObject);
                    mArrayList.add(movieDataParcelable);
                } catch (JSONException e) {
                    Log.e(TAG, "Error building JSONObject from remote file.", e);
                    return null;
                }
            }
            return mArrayList;
        }

        private JSONObject getMoviesData(String uri) throws MovieException {
            ConnectProvider connectProvider = new ConnectProvider(uri);
            return connectProvider.getData();
        }

        @Override
        protected final void onPostExecute(ArrayList<MovieDataParcelable> movieDataParcelables) {
            super.onPostExecute(movieDataParcelables);
            if (movieException != null) {
                MainActivityFragment.this.showDialog(movieException.getMessage());
            } else {
                ImageAdapter mImageAdapter = new ImageAdapter(getActivity(), movieDataParcelables);
                gridview.setAdapter(mImageAdapter);
            }
        }
    }
}
