package com.example.movies;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private static final String MOVIE_KEY = "MOVIES";
    private static final String SORT_KEY = "SORT";
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
        if (savedInstanceState != null)  {
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

    public void loadMovies (String sort) {
        FetchDBMovieTask fetchDBMovieTask = new FetchDBMovieTask();
        fetchDBMovieTask.execute(sort);
    }

    private class FetchDBMovieTask extends AsyncTask<String, Void, ArrayList<MovieDataParcelable>> {
        private final String TAG = FetchDBMovieTask.class.getSimpleName();

        @Override
        protected ArrayList<MovieDataParcelable> doInBackground(String... strings) {
            Log.i(TAG, "doInBackground");
            mArrayList = new ArrayList<MovieDataParcelable>();
            JSONArray json_array = getMovieData(strings[0]).optJSONArray("results");
            for (int i=0; i<json_array.length(); i++) {
                try {
                    JSONObject jsonObject = json_array.getJSONObject(i);
                    MovieDataParcelable movieDataParcelable = new MovieDataParcelable(jsonObject);
                    mArrayList.add(movieDataParcelable);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return mArrayList;
        }

        private JSONObject getMovieData (String sort) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try {
                Resources resources = getActivity().getResources();
                URL url = new URL(resources.getString(R.string.dbapiurl)
                        .concat(sort)
                        .concat(resources.getString(R.string.dbapikey_param))
                        .concat(resources.getString(R.string.dbapikey)));

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
                if (buffer.length() == 0) {
                    return null;
                }
            } catch (IOException e) {
                 Log.e(TAG, "Error managing connection. " , e);
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
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDataParcelable> movieDataParcelables) {
            super.onPostExecute(movieDataParcelables);
            GridView gridview = (GridView) getActivity().findViewById(R.id.gridView);
            ImageAdapter mImageAdapter = new ImageAdapter(getActivity(), movieDataParcelables);
            gridview.setAdapter(mImageAdapter);
        }
    }
}
