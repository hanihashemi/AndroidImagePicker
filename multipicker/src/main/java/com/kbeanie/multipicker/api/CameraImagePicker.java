package com.kbeanie.multipicker.api;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.kbeanie.multipicker.api.exceptions.PickerException;
import com.kbeanie.multipicker.core.ImagePickerImpl;

/**
 * Capture an image using the device's camera.
 */
public final class CameraImagePicker extends ImagePickerImpl {
    /**
     * Constructor for triggering capture from an {@link Activity}
     * @param activity
     */
    public CameraImagePicker(Activity activity) {
        super(activity, Picker.PICK_IMAGE_CAMERA);
    }

    /**
     * Constructor for triggering capture from a {@link Fragment}
     * @param fragment
     */
    public CameraImagePicker(Fragment fragment) {
        super(fragment, Picker.PICK_IMAGE_CAMERA);
    }

    /**
     * Constructor for triggering capture from a {@link android.app.Fragment}
     * @param appFragment
     */
    public CameraImagePicker(android.app.Fragment appFragment) {
        super(appFragment, Picker.PICK_IMAGE_CAMERA);
    }

    /**
     * Triggers image capture using the device's camera
     * @return
     */
    public void pickImage() {
        try {
            pick();
        } catch (PickerException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    @Override
    public void setCacheLocation(int cacheLocation) {
        if(cacheLocation == CacheLocation.INTERNAL_APP_DIR){
            throw new RuntimeException("Cannot use CacheLocation.INTERNAL_APP_DIR for taking pictures. Please use another cache location.");
        }
        super.setCacheLocation(cacheLocation);
    }
}