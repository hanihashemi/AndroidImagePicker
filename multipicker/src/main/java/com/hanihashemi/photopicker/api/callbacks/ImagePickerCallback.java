package com.hanihashemi.photopicker.api.callbacks;

import com.hanihashemi.photopicker.api.entity.ChosenImage;

import java.util.List;

/**
 * Created by kbibek on 2/23/16.
 */
public interface ImagePickerCallback extends PickerCallback {
    void onImagesChosen(List<ChosenImage> images);
}
