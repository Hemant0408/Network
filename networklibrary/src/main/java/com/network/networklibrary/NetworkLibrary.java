package com.network.networklibrary;

/**
 * Created by Hemant on 5/26/2017.
 */

public class NetworkLibrary {

    private static String base_url;

    public static String getBase_url() {
        return base_url;
    }

    public static void setBase_url(String base_url) {
        NetworkLibrary.base_url = base_url;
    }
}
