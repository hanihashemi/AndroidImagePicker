package com.hanihashemi.imagepicker.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.hanihashemi.imagepicker.api.CameraImagePicker;
import com.hanihashemi.imagepicker.api.ImagePicker;
import com.hanihashemi.imagepicker.api.Picker;
import com.hanihashemi.imagepicker.api.callbacks.ImagePickerCallback;
import com.hanihashemi.imagepicker.api.entity.ChosenImage;
import com.hanihashemi.imagepicker.api.exceptions.PickerException;
import com.hanihashemi.imagepicker.core.threads.ImageProcessorThread;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to pick images (Stored or capture a new image using the device's camera)
 * This class cannot be used directly. User {@link ImagePicker} or {@link CameraImagePicker}
 */
public abstract class PickerImpl extends PickerManager {
    private final static String TAG = PickerImpl.class.getSimpleName();
    protected ImagePickerCallback callback;
    private boolean generateThumbnails = true;
    private boolean generateMetadata = true;
    private int maxWidth = -1;
    private int maxHeight = -1;
    private String cameraFilePath;

    /**
     * @param activity {@link Activity}
     */
    public PickerImpl(Activity activity) {
        super(activity);
    }

    /**
     * @param fragment {@link Fragment}
     */
    public PickerImpl(Fragment fragment) {
        super(fragment);
    }

    /**
     * @param appFragment {@link android.app.Fragment}
     */
    public PickerImpl(android.app.Fragment appFragment) {
        super(appFragment);
    }

    /**
     * Enable generation of thumbnails. Default value is {@link Boolean#FALSE}
     */
    public void shouldGenerateThumbnails(boolean generateThumbnails) {
        this.generateThumbnails = generateThumbnails;
    }

    /**
     * Enable generation of metadata for the image. Default value is {@link Boolean#FALSE}
     */
    public void shouldGenerateMetadata(boolean generateMetadata) {
        this.generateMetadata = generateMetadata;
    }

    public void setImagePickerCallback(ImagePickerCallback callback) {
        this.callback = callback;
    }

    /**
     * Use this method to set the max size of the generated image. The final bitmap will be downscaled based on
     * these values.
     */
    public void ensureMaxSize(int width, int height) {
        if (width > 0 && height > 0) {
            this.maxWidth = width;
            this.maxHeight = height;
        }
    }

    @Override
    public void pickImage() {
        try {
            if (callback == null) {
                throw new PickerException("ImagePickerCallback is null!!! Please set one.");
            }
            if (this instanceof ImagePicker) {
                checkWriteExternalStoragePermission();
            } else if (this instanceof CameraImagePicker) {
                checkCameraPermission();
            }
        } catch (PickerException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    private String pickLocalImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (extras != null) {
            intent.putExtras(extras);
        }
        // For reading from external storage (Content Providers)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickInternal(intent, Picker.PICK_IMAGE_DEVICE);
        return null;
    }

    private void checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            pickLocalImage();
        else Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                            pickLocalImage();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private String takePictureWithCamera() throws PickerException {
        Uri uri;
        String tempFilePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempFilePath = getNewFileLocation("jpeg", Environment.DIRECTORY_PICTURES);
            File file = new File(tempFilePath);
            uri = FileProvider.getUriForFile(getContext(), getFileProviderAuthority(), file);
            Log.d(TAG, "takeVideoWithCamera: Temp Uri: " + uri.getPath());
        } else {
            tempFilePath = buildFilePath("jpeg", Environment.DIRECTORY_PICTURES);
            uri = Uri.fromFile(new File(tempFilePath));
        }
        cameraFilePath = tempFilePath;

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (extras != null) {
            intent.putExtras(extras);
        }

        pickInternal(intent, Picker.PICK_IMAGE_CAMERA);
        return tempFilePath;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                takePictureWithCamera();
            } catch (PickerException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        } else Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            try {
                                takePictureWithCamera();
                            } catch (PickerException e) {
                                e.printStackTrace();
                                if (callback != null) {
                                    callback.onError(e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Call this method from
     * {@link Activity#onActivityResult(int, int, Intent)}
     * OR
     * {@link Fragment#onActivityResult(int, int, Intent)}
     * OR
     * {@link android.app.Fragment#onActivityResult(int, int, Intent)}
     */
    @Override
    public void getActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                handleGalleryData(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                handleCameraData(cameraFilePath);
            }
        }
    }

    private void handleCameraData(String path) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("Camera Path cannot be null. Re-initialize with correct path value.");
        } else {
            List<String> uris = new ArrayList<>();
            uris.add(Uri.fromFile(new File(path)).toString());
            processImages(uris);
        }
    }

    @SuppressLint("NewApi")
    private void handleGalleryData(Intent intent) {
        List<String> uris = new ArrayList<>();
        if (intent != null) {
            if (intent.getDataString() != null && isClipDataApi() && intent.getClipData() == null) {
                String uri = intent.getDataString();
                Log.d(TAG, "handleGalleryData: " + uri);
                uris.add(uri);
            } else if (isClipDataApi()) {
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    Log.d(TAG, "handleGalleryData: Multiple images with ClipData");
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Log.d(TAG, "Item [" + i + "]: " + item.getUri().toString());
                        uris.add(item.getUri().toString());
                    }
                }
            }
            if (intent.hasExtra("uris")) {
                ArrayList<Uri> paths = intent.getParcelableArrayListExtra("uris");
                for (int i = 0; i < paths.size(); i++) {
                    uris.add(paths.get(i).toString());
                }
            }

            processImages(uris);
        }
    }

    private void processImages(List<String> uris) {
        ImageProcessorThread thread = new ImageProcessorThread(getContext(), getImageObjects(uris), cacheLocation);
        if (maxWidth != -1 && maxHeight != -1) {
            thread.setOutputImageDimensions(maxWidth, maxHeight);
        }
        thread.setShouldGenerateThumbnails(generateThumbnails);
        thread.setShouldGenerateMetadata(generateMetadata);
        thread.setImagePickerCallback(callback);
        thread.start();
    }

    private List<ChosenImage> getImageObjects(List<String> uris) {
        List<ChosenImage> images = new ArrayList<>();
        for (String uri : uris) {
            ChosenImage image = new ChosenImage();
            image.setQueryUri(uri);
            image.setDirectoryType(Environment.DIRECTORY_PICTURES);
            image.setType("image");
            images.add(image);
        }
        return images;
    }
}