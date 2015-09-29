package com.example.movies.util;

import android.util.Log;

import com.example.movies.exception.MovieException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectProvider {
    private final static String TAG = ConnectProvider.class.getSimpleName();
    private final String uri;

    public ConnectProvider(String url) {
        this.uri = url;
    }

    public final JSONObject getData() throws MovieException{
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            URL url = new URL(uri);

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
                buffer.append(line).append("\n");
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
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error building JSONObject. ", e);
            throw new MovieException("Error building JSON data from remote source: tmdb.org");
        }
        return jsonObject;
    }
}
