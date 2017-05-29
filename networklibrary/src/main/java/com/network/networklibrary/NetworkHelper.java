package com.network.networklibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.network.networklibrary.inherit.Response;
import com.network.networklibrary.parsing.GsonRequest;
import com.network.networklibrary.parsing.PreferenceKeys;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aksha_000 on 12/24/2015.
 */
public class NetworkHelper<T> {
    private static final String TAG = NetworkHelper.class.getSimpleName();

    protected enum MethodType {
        GET, POST, PUT
    }

    /*public enum Status {
        SUCCESS,
        ERROR_INVALID_DATA,
        ERROR_SERVER,
        ERROR_NETWORK,
        UNKNOWN
    }*/

    private Context context;
    private RequestQueue requestQueue;

    public NetworkHelper(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
    }

    public Context getContext() {
        return context;
    }

    protected void addToQueue(Request request) {
        Log.d(TAG, "Sending request: " + request.getUrl());
        requestQueue.add(request);
    }

    protected void cancelRequests(Object tag) {
        requestQueue.cancelAll(tag);
    }

    protected void createAndMakeGsonRequest(@NonNull Object requestTag, @NonNull MethodType type, @NonNull String endpoint, @Nullable T bodyParams, final Class<T> responseClass) {
        Request request = createGsonRequest(requestTag, type, endpoint, bodyParams, responseClass);
        addToQueue(request);
    }

    protected GsonRequest createGsonRequest(@NonNull final Object requestTag, @NonNull MethodType type, @NonNull String endpoint, @Nullable T bodyParams, final Class<T> responseClass) {
        int methodType;
        String url = NetworkLibrary.getProperty(NetworkLibrary.Property.URL_BASE) + endpoint;

        switch (type) {
            case GET:
                methodType = Request.Method.GET;
                break;
            case POST:
                methodType = Request.Method.POST;
                break;
            case PUT:
                methodType = Request.Method.PUT;
                break;
            default:
                methodType = Request.Method.GET;
        }

        GsonRequest request = new GsonRequest(methodType, url, bodyParams, responseClass, getHeaders(), new com.android.volley.Response.Listener() {
            @Override
            public void onResponse(Object response) {
                handleResponse(requestTag, response, false, responseClass);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Object response;
                boolean invalidData = false;
                try {
                    if (error.getCause() instanceof JsonParseException) {
                        Log.e(TAG, "parse error", error.getCause());
                        //Q2TApp.logError(getContext(), TAG, error.getMessage());
                        invalidData = true;
                        response = null;
                    }
                    else {
                        Log.e(TAG, "error", error);
                        //Q2TApp.logError(getContext(), TAG, error.getMessage());
                        response = new Gson().fromJson(new String(error.networkResponse.data), responseClass);
                    }
                }
                catch (Throwable e) {
                    Log.e(TAG, "error", e);
                    //Q2TApp.logError(getContext(), TAG, error.getMessage());
                    response = null;
                }

                handleResponse(requestTag, response, invalidData, responseClass);
            }
        });

        request.setTag(requestTag);

        return request;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        headers.put("Accept", "application/json, application/json.v2");

        if (prefs.contains(PreferenceKeys.AUTH_KEY))
            headers.put("X-API-KEY", prefs.getString(PreferenceKeys.AUTH_KEY, ""));

        headers.put("X-DEV-UUID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        return headers;
    }

    private void handleResponse(@NonNull Object requestTag, @Nullable Object responseObject, @NonNull boolean invalidData, Class<T> responseClass) {
        Response response = (Response) responseObject;

        if (invalidData) {
            try {
                response = (Response) responseClass.newInstance();
                response.setErrors(context.getString(R.string.invalid), context.getString(R.string.invalid_data));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (response == null) {
            try {
                response = (Response) responseClass.newInstance();
                response.setErrors(context.getString(R.string.network), context.getString(R.string.nework_error));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (response != null) {
            EventBus.getDefault().post(response);
        }
    }
}
