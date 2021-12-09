package com.example.android.glass.glassdesign2;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class OpenTokConfig {
    // Replace with your OpenTok API key
    public static String API_KEY = "";
    // Replace with a generated Session ID
    public static String SESSION_ID = "";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static String TOKEN = "";

    public static boolean isValid() {

        if (TextUtils.isEmpty(OpenTokConfig.API_KEY)
                || TextUtils.isEmpty(OpenTokConfig.SESSION_ID)
                || TextUtils.isEmpty(OpenTokConfig.TOKEN)) {
            return false;
        }

        return true;
    }
    @NonNull
    public static String getDescription() {
        return "OpenTokConfig:" + "\n"
                + "API_KEY: " + OpenTokConfig.API_KEY + "\n"
                + "SESSION_ID: " + OpenTokConfig.SESSION_ID + "\n"
                + "TOKEN: " + OpenTokConfig.TOKEN + "\n";
    }
}

