package com.example.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<MovieDataParcelable> mArrayList;
    private static final String MOVIE_KEY = "MOVIES";
    protected ImageAdapter mImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArrayList = new ArrayList<MovieDataParcelable>();
        if (savedInstanceState == null) {
            loadMovies();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        if (savedInstanceState != null)  {
            mArrayList = (ArrayList<MovieDataParcelable>) savedInstanceState.get(MOVIE_KEY);
            GridView gridview = (GridView) root.findViewById(R.id.gridView);
            mImageAdapter = new ImageAdapter(getActivity(), mArrayList);
            gridview.setAdapter(mImageAdapter);
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, mArrayList);
    }

    public void loadMovies () {
        FetchDBMovieTask fetchDBMovieTask = new FetchDBMovieTask();
        fetchDBMovieTask.execute();
    }

    private class FetchDBMovieTask extends AsyncTask<Void, Void, ArrayList<MovieDataParcelable>> {
        private final String TAG = FetchDBMovieTask.class.getSimpleName();

        @Override
        protected ArrayList<MovieDataParcelable> doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground");
            mArrayList = new ArrayList<MovieDataParcelable>();
            JSONArray json_array = getMovieData().optJSONArray("results");
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

        protected JSONObject getMovieData () {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try {
                URL url = new URL(getResources().getString(R.string.dbapiurl)
                        .concat(getResources().getString(R.string.api_key)));

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
                 e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            String movieData = buffer.toString();
            if (movieData == "") {
                return null;
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDataParcelable> movieDataParcelables) {
            super.onPostExecute(movieDataParcelables);
            GridView gridview = (GridView) getActivity().findViewById(R.id.gridView);
            mImageAdapter = new ImageAdapter(getActivity(), movieDataParcelables);
            gridview.setAdapter(mImageAdapter);
        }
    }
}
