package com.kiyo.istoremanager.service;

public final class PhotoUrls {

    private PhotoUrls() {}

    public static String thumb(String url) {
        return transform(url, "c_fill,w_200,h_200,g_auto,f_auto,q_auto");
    }

    public static String full(String url) {
        return transform(url, "c_limit,w_1600,f_auto,q_auto");
    }

    private static String transform(String url, String transformation) {
        String marker = "/upload/";
        int i = url.indexOf(marker);
        if (i < 0) return url;
        return url.substring(0, i + marker.length()) + transformation + "/" + url.substring(i + marker.length());
    }
}