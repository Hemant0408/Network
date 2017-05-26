package com.network.networklibrary.parsing;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by aksha_000 on 12/24/2015.
 */
public class GsonRequest<T, S> extends Request<T> {
    private static final String TAG = GsonRequest.class.getSimpleName();

    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final S bodyParams;
    private final Response.Listener<T> listener;

    public GsonRequest(int methodType, String url, S bodyParams, Class<T> clazz, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(methodType, url, errorListener);
        this.clazz = clazz;
        this.bodyParams = bodyParams;
        this.headers = headers;
        this.listener = listener;
        setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        String params = null;

        if (bodyParams != null)
            params = new Gson().toJson(bodyParams);

        return params != null ? params.getBytes(Charset.forName("UTF-8")) : super.getBody();
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers)).trim();
            logResponse(response.statusCode, json);
            Gson gson = new GsonBuilder().registerTypeAdapter(clazz, new AnnotatedDeserializer<T>(){}).create();
            return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (Throwable e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        String response = null;
        int statusCode = 0;

        try {
            statusCode = volleyError.networkResponse.statusCode;
        }
        catch (Throwable e) {
            statusCode = 0;
        }

        try {
            response = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
        }
        catch (Throwable e) {
            response = null;
        }

        logResponse(statusCode, response);

        return super.parseNetworkError(volleyError);
    }

    private void logResponse(@Nullable int statusCode, @Nullable String response) {
        Log.d(TAG, "Url: " + getUrl());
        Log.d(TAG, "Status code: " + statusCode);
        Log.d(TAG, "Response: " + response);
    }
}
