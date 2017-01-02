package com.kbeanie.multipicker.sample.utils;

import android.content.Context;

import com.kbeanie.multipicker.api.CacheLocation;


/**
 * Created by kbibek on 2/26/16.
 */
public class PickerUtils {
    public static int getSavedCacheLocation(Context context) {
        switch (0) {
            case 0:
                return CacheLocation.EXTERNAL_STORAGE_APP_DIR;
            case 1:
                return CacheLocation.EXTERNAL_STORAGE_PUBLIC_DIR;
            case 2:
                return CacheLocation.EXTERNAL_CACHE_DIR;
            case 3:
                return CacheLocation.INTERNAL_APP_DIR;
        }
        return CacheLocation.EXTERNAL_STORAGE_APP_DIR;
    }
}
