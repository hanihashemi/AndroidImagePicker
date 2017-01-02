package com.kbeanie.multipicker.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.util.List;

/**
 * Created by kbibek on 2/19/16.
 */
public class ImagePickerActivity extends AppCompatActivity implements ImagePickerCallback, View.OnClickListener {
    private ListView lvResults;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.initialize(this);
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
        imagePicker = new ImagePicker(this);
        imagePicker.setFolderName("Random");
        imagePicker.setRequestId(1234);
        imagePicker.ensureMaxSize(500, 500);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        imagePicker.pickImage();
    }

    public void pickImageMultiple() {
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(this);
        imagePicker.allowMultiple();
        imagePicker.pickImage();
    }

    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_CACHE_DIR);
        cameraPicker.setImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        cameraPicker.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                cameraPicker.submit(data);
            }
        }
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        MediaResultsAdapter adapter = new MediaResultsAdapter(images, this);
        lvResults.setAdapter(adapter);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
