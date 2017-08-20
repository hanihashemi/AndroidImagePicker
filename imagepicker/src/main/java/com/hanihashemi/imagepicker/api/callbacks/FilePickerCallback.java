package com.hanihashemi.imagepicker.api.callbacks;

import com.hanihashemi.imagepicker.api.entity.ChosenImage;

import java.util.List;

/**
 * Created by kbibek on 2/23/16.
 */
public interface FilePickerCallback extends PickerCallback {
    void onFilesChosen(List<ChosenImage> files);
}