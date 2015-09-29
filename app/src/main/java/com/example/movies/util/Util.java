package com.example.movies.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.example.movies.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
    public static String getApiKeyProperty(Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("app.properties");
        properties.load(inputStream);
        return properties.getProperty("dbapi_key");
    }



    private static final String TABLET = "tablet";
    private static final String PHONE = "phone";
    public static boolean is_phone (Resources resources) {
        return resources.getString(R.string.screen_type).equals(PHONE);
    }
    public static boolean is_tablet (Resources resources) {
        return resources.getString(R.string.screen_type).equals(TABLET);
    }
}