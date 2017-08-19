package com.kbeanie.multipicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hanihashemi.imagepicker.api.CacheLocation;
import com.hanihashemi.imagepicker.api.CameraImagePicker;
import com.hanihashemi.imagepicker.api.ImagePicker;
import com.hanihashemi.imagepicker.api.callbacks.ImagePickerCallback;
import com.hanihashemi.imagepicker.api.entity.ChosenImage;
import com.hanihashemi.imagepicker.core.PickerImpl;

import java.util.List;

/**
 * Created by kbibek on 2/19/16.
 */
public class ImagePickerActivity extends AppCompatActivity implements ImagePickerCallback, View.OnClickListener {
    public static final String TAG = "ImagePickerActivity";
    private ListView lvResults;

    private PickerImpl picker;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_activity);

        getSupportActionBar().setTitle("Image Picker");
        getSupportActionBar().setSubtitle("Activity example");

        lvResults = (ListView) findViewById(R.id.lvResults);
        findViewById(R.id.btGallerySingleImage).setOnClickListener(this);
        findViewById(R.id.btGalleryMultipleImages).setOnClickListener(this);
        findViewById(R.id.btCameraImage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btGallerySingleImage:
                pickImageSingle();
                break;
            case R.id.btGalleryMultipleImages:
                pickImageMultiple();
                break;
            case R.id.btCameraImage:
                takePicture();
                break;
        }
    }

    public void pickImageSingle() {
        picker = new ImagePicker.Builder(this, this)
                .shouldCrop(true)
                .build();
        picker.pickImage();
    }

    public void pickImageMultiple() {
        picker = new ImagePicker.Builder(this, this)
                .allowMultiple(true)
                .ensureMaxSize(500, 500)
                .shouldGenerateMetadata(false)
                .shouldGenerateThumbnails(true)
                .setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR)
                .build();
        picker.pickImage();
    }

    public void takePicture() {
        picker = new CameraImagePicker.Builder(this, this)
                .build();
        picker.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.getActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        MediaResultsAdapter adapter = new MediaResultsAdapter(images, this);
        lvResults.setAdapter(adapter);

        for (ChosenImage image : images) {
            Log.d(TAG, "==> original image path: " + image.getOriginalPath());
            Log.d(TAG, "==> big thumbnail image path: " + image.getThumbnailPath());
            Log.d(TAG, "==> small thumbnail image path: " + image.getThumbnailSmallPath());
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}