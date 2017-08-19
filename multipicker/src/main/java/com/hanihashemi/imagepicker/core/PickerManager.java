package com.hanihashemi.imagepicker.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

import com.hanihashemi.imagepicker.api.CacheLocation;
import com.hanihashemi.imagepicker.api.exceptions.PickerException;
import com.hanihashemi.imagepicker.utils.FileUtils;

import java.io.File;
import java.util.UUID;

/**
 * Abstract class for all types of Pickers
 */
public abstract class PickerManager {
    protected Activity activity;
    protected boolean allowMultiple;
    int cacheLocation = CacheLocation.EXTERNAL_STORAGE_APP_DIR;
    Bundle extras;
    private Fragment fragment;
    private android.app.Fragment appFragment;

    @SuppressWarnings("WeakerAccess")
    public PickerManager(Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("WeakerAccess")
    public PickerManager(Fragment fragment) {
        this.fragment = fragment;
    }

    @SuppressWarnings("WeakerAccess")
    public PickerManager(android.app.Fragment appFragment) {
        this.appFragment = appFragment;
    }

    /**
     * Default cache location is {@link CacheLocation#EXTERNAL_STORAGE_APP_DIR}
     *
     * @param cacheLocation {@link CacheLocation}
     */
    public void setCacheLocation(int cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    /**
     * Triggers pick image
     */
    public abstract void pickImage();

    /**
     * This method should be called after {@link Activity#onActivityResult(int, int, Intent)} is  called.
     */
    public abstract void getActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    String buildFilePath(String extension, String type) throws PickerException {
        String directoryPath = getDirectory(type);
        return directoryPath + File.separator + UUID.randomUUID().toString() + "." + extension;
    }

    private String getDirectory(String type) throws PickerException {
        String directory = null;
        switch (cacheLocation) {
            case CacheLocation.EXTERNAL_STORAGE_APP_DIR:
                directory = FileUtils.getExternalFilesDir(type, getActivity());
                break;
            case CacheLocation.EXTERNAL_CACHE_DIR:
                directory = FileUtils.getExternalCacheDir(getActivity());
                break;
            case CacheLocation.INTERNAL_APP_DIR:
                directory = FileUtils.getInternalFileDirectory(getActivity());
                break;
        }
        return directory;
    }

    Activity getActivity() {
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getActivity();
        } else if (appFragment != null) {
            return appFragment.getActivity();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void pickInternal(Intent intent, int type) {
        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }
        if (activity != null) {
            activity.startActivityForResult(intent, type);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, type);
        } else if (appFragment != null) {
            appFragment.startActivityForResult(intent, type);
        }
    }

    boolean isClipDataApi() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN);
    }

    String getNewFileLocation(String extension, String type) throws PickerException {
        File file;
        String filePathName = "";
        if (type.equals(Environment.DIRECTORY_PICTURES)) {
            filePathName = "pictures";
        }
        file = new File(getActivity().getFilesDir(), filePathName);
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();

        file = new File(file.getAbsolutePath(), UUID.randomUUID().toString() + "." + extension);
        return file.getAbsolutePath();
    }

    String getFileProviderAuthority() {
        return getActivity().getPackageName() + ".multipicker.fileprovider";
    }
}