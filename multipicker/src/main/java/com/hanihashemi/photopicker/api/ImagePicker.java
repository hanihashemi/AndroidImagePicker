package com.hanihashemi.photopicker.api;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.hanihashemi.photopicker.api.callbacks.ImagePickerCallback;
import com.hanihashemi.photopicker.api.exceptions.PickerException;
import com.hanihashemi.photopicker.core.ImagePickerImpl;

/**
 * Choose an image(s) on your device. Gallery, Google Photos, Dropbox etc.
 */
public final class ImagePicker extends ImagePickerImpl {
    /**
     * Constructor for choosing an image from an {@link Activity}
     *
     * @param activity
     */
    public ImagePicker(Activity activity) {
        super(activity, Picker.PICK_IMAGE_DEVICE);
    }

    /**
     * Constructor for choosing an image from a {@link Fragment}
     *
     * @param fragment
     */
    public ImagePicker(Fragment fragment) {
        super(fragment, Picker.PICK_IMAGE_DEVICE);
    }

    /**
     * Constructor for choosing an image from a {@link android.app.Fragment}
     *
     * @param appFragment
     */
    public ImagePicker(android.app.Fragment appFragment) {
        super(appFragment, Picker.PICK_IMAGE_DEVICE);
    }

    /**
     * Allows you to select multiple images at once. This will only work for the applications that
     * support multiple image selection.
     */
    public void allowMultiple(boolean multiple) {
        this.allowMultiple = multiple;
    }

    /**
     * Triggers Image selection
     */
    public void pickImage() {
        try {
            super.pick();
        } catch (PickerException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public static class Builder {
        private ImagePicker imagePicker;

        public Builder(Activity activity, ImagePickerCallback callback) {
            imagePicker = new ImagePicker(activity);
            imagePicker.setImagePickerCallback(callback);
        }

        public Builder(Fragment fragment, ImagePickerCallback callback) {
            imagePicker = new ImagePicker(fragment);
            imagePicker.setImagePickerCallback(callback);
        }

        public Builder(android.app.Fragment fragment, ImagePickerCallback callback) {
            imagePicker = new ImagePicker(fragment);
            imagePicker.setImagePickerCallback(callback);
        }

        /**
         * Enable generation of metadata for the image. Default value is {@link Boolean#TRUE}
         *
         * @return Builder
         */
        public Builder shouldGenerateMetadata(boolean generateMetadata) {
            imagePicker.shouldGenerateMetadata(generateMetadata);
            return this;
        }

        /**
         * Enable generation of thumbnails. Default value is {@link Boolean#TRUE}
         */
        public Builder shouldGenerateThumbnails(boolean generateThumbnails) {
            imagePicker.shouldGenerateThumbnails(generateThumbnails);
            return this;
        }

        /**
         * Use this method to set the max size of the generated image. The final bitmap will be downscaled based on
         * these values.
         */
        public Builder ensureMaxSize(int width, int height) {
            imagePicker.ensureMaxSize(width, height);
            return this;
        }

        /**
         * Default cache location is {@link CacheLocation#EXTERNAL_STORAGE_APP_DIR}
         */
        public Builder setCacheLocation(int cacheLocation) {
            imagePicker.setCacheLocation(cacheLocation);
            return this;
        }

        /**
         * Allows you to select multiple images at once. This will only work for the applications that
         * support multiple image selection. {@link Boolean#FALSE}
         */
        public Builder allowMultiple(boolean multiple) {
            imagePicker.allowMultiple(multiple);
            return this;
        }

        public ImagePicker build() {
            return imagePicker;
        }
    }
}