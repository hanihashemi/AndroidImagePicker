package com.hanihashemi.imagepicker.utils;

import android.util.Log;

import com.hanihashemi.imagepicker.BuildConfig;

/**
 * Created by hani on 8/19/17.
 */

public class Logger {
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message);
    }
}