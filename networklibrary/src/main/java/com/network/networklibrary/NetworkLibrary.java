package com.network.networklibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.network.networklibrary.parsing.PreferenceKeys;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Hemant on 5/26/2017.
 */

public class NetworkLibrary {

    private static final String TAG = NetworkLibrary.class.getSimpleName();

    public enum Property {
        URL_BASE,
        COMPANY_ID
    }

    private static final String FILENAME_LOCAL_SETTINGS = "local_settings.properties";

    private static NetworkLibrary instance;

    private Context context;
    private Properties properties;
    private PreferenceManager privateManager;

    public static void init(Context context) {
        if (instance == null)
            instance = new NetworkLibrary(context);
    }

    public static boolean isDevelopment() {
//        boolean isDevelopment = true;
//
//        try {
//            PackageInfo packageInfo = instance.context.getPackageManager().getPackageInfo(instance.context.getPackageName(), 0);
//            isDevelopment = ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) && BuildConfig.DEBUG;
//        } catch (Throwable e) {
//            Q2TApp.logError(instance.context, TAG, "Unable to check environment");
//        }

        return BuildConfig.DEBUG;
    }

    public static boolean containsCompanyId() {
        return instance.privateManager.getSharedPreferences().contains(PreferenceKeys.COMPANY_ID);
    }

    public static boolean containsUserCache() {
        boolean contained = false;

        if (instance != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(instance.context);
            contained = pref.contains(PreferenceKeys.AUTH_KEY);
        }

        return contained;
    }

    public static void clearData() {
        if (instance != null) {
            //UserData.updateUserData(null);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(instance.context).edit();
            editor.clear();
            editor.apply();
        }
    }

    public static void restartApp() {
        if (instance != null) {
            Intent intent = instance.context.getPackageManager().getLaunchIntentForPackage(instance.context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            instance.context.startActivity(intent);
        }
    }

    public static String getProperty(Property property) {
        if (instance == null)
            return null;

        String propertyStr;

        switch (property) {
            case URL_BASE:
                propertyStr = instance.privateManager.getSharedPreferences().getString(PreferenceKeys.BASE_URL, instance.getPropertyString(property));
                break;

            case COMPANY_ID:
                propertyStr = instance.privateManager.getSharedPreferences().getString(PreferenceKeys.COMPANY_ID, null);
                break;

            default:
                propertyStr = instance.getPropertyString(property);
                break;
        }

        return propertyStr;
    }

    public static void setProperty(Property property, String propertyStr) {
        switch (property) {
            case COMPANY_ID:
                instance.privateManager.getSharedPreferences().edit().putString(PreferenceKeys.COMPANY_ID, propertyStr).apply();
                break;

            case URL_BASE:
                instance.privateManager.getSharedPreferences().edit().putString(PreferenceKeys.BASE_URL, propertyStr).apply();
                break;

            default:
        }
    }

    private NetworkLibrary(Context context) {
        this.context = context.getApplicationContext();

        properties = new Properties();
        try {
            properties.load(context.getAssets().open(FILENAME_LOCAL_SETTINGS));
        }
        catch (IOException e) {
            //Q2TApp.logError(context, TAG, "Unable to get local settings");
        }

        privateManager = new PreferenceManager(context);
        privateManager.setSharedPreferencesName(PreferenceKeys.NAME);
        privateManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        privateManager.setStorageDeviceProtected();
    }

    private String getPropertyString(Property property) {
        String propertyStr = isDevelopment() ? "testing_" : "production_";

        switch (property) {
            case URL_BASE:
                propertyStr += "url_base";
                break;
        }

        return properties.getProperty(propertyStr);
    }
}
