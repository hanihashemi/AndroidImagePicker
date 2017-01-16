package com.kbeanie.multipicker.api;

/**
 * Created by kbibek on 2/18/16.
 */
public interface Picker {
    /**
     * Pick an image from the user's device
     */
    int PICK_IMAGE_DEVICE = 3111;
    /**
     * Take a picture using the user's camera
     */
    int PICK_IMAGE_CAMERA = 4222;
}
