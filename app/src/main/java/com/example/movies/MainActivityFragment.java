package com.example.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.movies.adapter.ImageAdapter;
import com.example.movies.exception.MovieException;
import com.example.movies.util.MyAlertDialogFragment;
import com.example.movies.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String MOVIE_KEY = "MOVIES";
    private static final String SORT_KEY = "SORT";
    protected String mApiKey = null;
    private ArrayList<MovieDataParcelable> mArrayList;
    private Menu sMenu;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            String sort_type = getResources().getString(R.string.dbapiurl_popularity);
            loadMovies(sort_type);
            mPreferences.edit().putString(SORT_KEY,
                    getResources().getString(R.string.most_popular)).apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        if (savedInstanceState != null) {
            mArrayList = (ArrayList<MovieDataParcelable>) savedInstanceState.get(MOVIE_KEY);
            GridView gridview = (GridView) root.findViewById(R.id.gridView);
            ImageAdapter mImageAdapter = new ImageAdapter(getActivity(), mArrayList);
            gridview.setAdapter(mImageAdapter);
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, mArrayList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        if (mPreferences.getString(SORT_KEY, "").equals(getResources().getString(R.string.most_popular))) {
            menu.findItem(R.id.highest_rated).setVisible(true);
            menu.findItem(R.id.most_popular).setVisible(false);
        } else {
            menu.findItem(R.id.most_popular).setVisible(true);
            menu.findItem(R.id.highest_rated).setVisible(false);
        }
        this.sMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                item.setVisible(false);
                sMenu.findItem(R.id.highest_rated).setVisible(true);
                mPreferences.edit().putString(SORT_KEY,
                        getResources().getString(R.string.most_popular)).apply();
                loadMovies(getResources().getString(R.string.dbapiurl_popularity));
                return true;
            case R.id.highest_rated:
                item.setVisible(false);
                sMenu.findItem(R.id.most_popular).setVisible(true);
                mPreferences.edit().putString(SORT_KEY,
                        getResources().getString(R.string.highest_rated)).apply();
                loadMovies(getResources().getString(R.string.dbapiurl_rate));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadMovies(String sort) {
        // Read dbapi.org key
        try {
            mApiKey = Util.getProperty("dbapi_key", getActivity());
            if (mApiKey == null || mApiKey.equals("")) {
                throw new Exception();
            }
        } catch (Exception e) {
            showDialog(getResources().getString(R.string.dbapi_wrong_params));
            return;
        }
        FetchDBMovieTask fetchDBMovieTask = new FetchDBMovieTask();
        fetchDBMovieTask.execute(sort);
    }

    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showDialog(String message) {
        FragmentManager fm = getFragmentManager();
        MyAlertDialogFragment dialogFragment = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MyAlertDialogFragment.TAG, message);
        dialogFragment.setArguments(args);
        dialogFragment.show(fm, MyAlertDialogFragment.TAG);
    }

    private class FetchDBMovieTask extends AsyncTask<String, Void, ArrayList<MovieDataParcelable>> {
        private final String TAG = FetchDBMovieTask.class.getSimpleName();
        private MovieException movieException;

        @Override
        protected ArrayList<MovieDataParcelable> doInBackground(String... strings) {
            Log.i(TAG, "doInBackground");
            mArrayList = new ArrayList<MovieDataParcelable>();
            JSONArray json_array = null;
            try {
                json_array = getMovieData(strings[0]).optJSONArray("results");
            } catch (MovieException e) {
                movieException = e;
                return null;
            }
            for (int i = 0; i < json_array.length(); i++) {
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

        private JSONObject getMovieData(String sort) throws MovieException {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try {
                Resources resources = getActivity().getResources();
                URL url = new URL(resources.getString(R.string.dbapiurl)
                        .concat(sort)
                        .concat(resources.getString(R.string.dbapikey_param))
                        .concat(mApiKey));

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer == null || buffer.length() == 0) {
                    throw new MovieException("Trying to get data from remote tmdb.org. No data was found");
                }
            } catch (MalformedURLException e) {
                throw new MovieException(e.getClass().getSimpleName()
                        .concat("tmdb.org url was wrong, check your params."));
            } catch (java.io.FileNotFoundException e) {
                throw new MovieException(e.getClass().getSimpleName()
                        .concat(". ").concat(e.getMessage()));
            } catch (IOException e) {
                throw new MovieException("Check your network settings");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error trying to close the Stream", e);
                    }
                }
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(buffer.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error building JSONObject. ", e);
                throw new MovieException("Error building JSON data from remote source: tmdb.org");
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDataParcelable> movieDataParcelables) {
            super.onPostExecute(movieDataParcelables);
            if (movieException != null) {
                MainActivityFragment.this.showDialog(movieException.getMessage());
            } else {
                GridView gridview = (GridView) getActivity().findViewById(R.id.gridView);
                ImageAdapter mImageAdapter = new ImageAdapter(getActivity(), movieDataParcelables);
                gridview.setAdapter(mImageAdapter);
            }
        }
    }
}
