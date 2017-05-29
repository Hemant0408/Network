package com.network.networklibrary;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

import com.network.networklibrary.parsing.PreferenceKeys;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Hemant on 5/26/2017.
 */

public class NetworkLibrary {

    private static String base_url;

    private PreferenceManager privateManager;
    private static NetworkLibrary instance;
    private Context context;

    public static void init(Context context) {
        if (instance == null)
            instance = new NetworkLibrary(context);
    }

    private NetworkLibrary(Context context) {
        this.context = context.getApplicationContext();

        privateManager = new PreferenceManager(context);
        privateManager.setSharedPreferencesName(PreferenceKeys.NAME);
        privateManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        privateManager.setStorageDeviceProtected();
    }

    public static String getBase_url() {
        return instance.privateManager.getSharedPreferences().getString(PreferenceKeys.BASE_URL, "");
    }

    public static void setBase_url(String base_url) {
        instance.privateManager.getSharedPreferences().edit().putString(PreferenceKeys.BASE_URL, base_url).apply();
    }
}
