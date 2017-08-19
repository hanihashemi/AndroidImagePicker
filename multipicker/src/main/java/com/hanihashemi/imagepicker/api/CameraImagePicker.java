package com.hanihashemi.imagepicker.api;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.hanihashemi.imagepicker.api.callbacks.ImagePickerCallback;
import com.hanihashemi.imagepicker.api.exceptions.PickerException;
import com.hanihashemi.imagepicker.core.PickerImpl;

/**
 * Capture an image using the device's camera.
 */
public final class CameraImagePicker extends PickerImpl {
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

    public static class Builder {
        private CameraImagePicker cameraImagePicker;

        public Builder(Activity activity, ImagePickerCallback callback) {
            cameraImagePicker = new CameraImagePicker(activity);
            cameraImagePicker.setImagePickerCallback(callback);
        }

        public Builder(Fragment fragment, ImagePickerCallback callback) {
            cameraImagePicker = new CameraImagePicker(fragment);
            cameraImagePicker.setImagePickerCallback(callback);
        }

        public Builder(android.app.Fragment fragment, ImagePickerCallback callback) {
            cameraImagePicker = new CameraImagePicker(fragment);
            cameraImagePicker.setImagePickerCallback(callback);
        }

        /**
         * Enable generation of metadata for the image. Default value is {@link Boolean#TRUE}
         *
         * @return Builder
         */
        public Builder shouldGenerateMetadata(boolean generateMetadata) {
            cameraImagePicker.shouldGenerateMetadata(generateMetadata);
            return this;
        }

        /**
         * Enable generation of thumbnails. Default value is {@link Boolean#TRUE}
         */
        public Builder shouldGenerateThumbnails(boolean generateThumbnails) {
            cameraImagePicker.shouldGenerateThumbnails(generateThumbnails);
            return this;
        }

        /**
         * Use this method to set the max size of the generated image. The final bitmap will be downscaled based on
         * these values.
         */
        public Builder ensureMaxSize(int width, int height) {
            cameraImagePicker.ensureMaxSize(width, height);
            return this;
        }

        /**
         * Default cache location is {@link CacheLocation#EXTERNAL_STORAGE_APP_DIR}
         */
        public Builder setCacheLocation(int cacheLocation) {
            cameraImagePicker.setCacheLocation(cacheLocation);
            return this;
        }

        public CameraImagePicker build() {
            return cameraImagePicker;
        }
    }
}