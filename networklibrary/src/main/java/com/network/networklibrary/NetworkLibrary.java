package com.network.networklibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.network.networklibrary.shared_preferences.PermanentPreferences;

import java.util.ArrayList;


/**
 * Created by Hemant on 5/26/2017.
 */

public class NetworkLibrary {

    private static NetworkLibrary instance;

    private Context context;
    private PreferenceManager privateManager;
    public static ArrayList<ErrorType> typeList;

    public static void init(Context context, String baseurl, ErrorType... type) {
        if (instance == null) {
            instance = new NetworkLibrary(context, baseurl);
            typeList = new ArrayList<>();

            for (ErrorType errorType : type) {
                addNewErrorTypes(errorType);
            }
        }
    }

    public static void addNewErrorTypes(ErrorType... type) {
        for (int i = 0; i < type.length; i++) {
            if (!typeList.get(i).getTypeValue().equals(type[i])) {
                typeList.add(type[i]);
            }
        }
    }

    public static void clearData() {
        if (instance != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(instance.context).edit();
            editor.clear();
            editor.apply();
        }
    }

    private NetworkLibrary(Context context, String baseurl) {
        this.context = context.getApplicationContext();

        privateManager = new PreferenceManager(context);
        privateManager.setSharedPreferencesName(PermanentPreferences.NAME);
        privateManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        privateManager.setStorageDeviceProtected();
        privateManager.getSharedPreferences().edit().putString(PermanentPreferences.BASE_URL, baseurl).apply();
    }

    public static void setBaseUrl(String baseUrl) {
        instance.privateManager.getSharedPreferences().edit().putString(PermanentPreferences.BASE_URL, baseUrl).apply();
    }

    public static String getBaseUrl() {
        return instance.privateManager.getSharedPreferences().getString(PermanentPreferences.BASE_URL, "");
    }
}
