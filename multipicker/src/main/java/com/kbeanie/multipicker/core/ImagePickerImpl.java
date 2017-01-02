package com.kbeanie.multipicker.core;

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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.EmptyMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.exceptions.PickerException;
import com.kbeanie.multipicker.core.threads.ImageProcessorThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to pick images (Stored or capture a new image using the device's camera)
 * This class cannot be used directly. User {@link ImagePicker} or {@link CameraImagePicker}
 */
public abstract class ImagePickerImpl extends PickerManager {
    private final static String TAG = ImagePickerImpl.class.getSimpleName();
    protected ImagePickerCallback callback;
    private boolean generateThumbnails = true;
    private boolean generateMetadata = true;
    private int maxWidth = -1;
    private int maxHeight = -1;
    private String cameraFilePath;

    /**
     * @param activity   {@link Activity}
     * @param pickerType {@link Picker#PICK_IMAGE_DEVICE}, {@link Picker#PICK_IMAGE_CAMERA}
     */
    public ImagePickerImpl(Activity activity, int pickerType) {
        super(activity, pickerType);
    }

    /**
     * @param fragment   {@link Fragment}
     * @param pickerType {@link Picker#PICK_IMAGE_DEVICE}, {@link Picker#PICK_IMAGE_CAMERA}
     */
    public ImagePickerImpl(Fragment fragment, int pickerType) {
        super(fragment, pickerType);
    }

    /**
     * @param appFragment {@link android.app.Fragment}
     * @param pickerType  {@link Picker#PICK_IMAGE_DEVICE}, {@link Picker#PICK_IMAGE_CAMERA}
     */
    public ImagePickerImpl(android.app.Fragment appFragment, int pickerType) {
        super(appFragment, pickerType);
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
    protected String pick() throws PickerException {
        if (callback == null) {
            throw new PickerException("ImagePickerCallback is null!!! Please set one.");
        }
        if (pickerType == Picker.PICK_IMAGE_DEVICE) {
            return pickLocalImage();
        } else if (pickerType == Picker.PICK_IMAGE_CAMERA) {
            return takePictureWithCamera();
        }
        return null;
    }

    private String pickLocalImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (extras != null) {
            intent.putExtras(extras);
        }
        // For reading from external storage (Content Providers)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        checkWriteExternalStoragePermission(intent);
        return null;
    }

    private void checkWriteExternalStoragePermission(final Intent intent) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            pickInternal(intent, Picker.PICK_IMAGE_DEVICE);
        else if (!Dexter.isRequestOngoing())
            Dexter.checkPermissions(new EmptyMultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    super.onPermissionsChecked(report);
                    if (report.areAllPermissionsGranted())
                        pickInternal(intent, Picker.PICK_IMAGE_DEVICE);
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

        checkCameraPermission(intent);
        return tempFilePath;
    }

    private void checkCameraPermission(final Intent intent) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            pickInternal(intent, Picker.PICK_IMAGE_CAMERA);
        else if (!Dexter.isRequestOngoing())
            Dexter.checkPermissions(new EmptyMultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    super.onPermissionsChecked(report);
                    if (report.areAllPermissionsGranted())
                        pickInternal(intent, Picker.PICK_IMAGE_CAMERA);
                }
            }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
    public void submit(Intent data) {
        if (pickerType == Picker.PICK_IMAGE_CAMERA) {
            handleCameraData(cameraFilePath);
        } else if (pickerType == Picker.PICK_IMAGE_DEVICE) {
            handleGalleryData(data);
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

    private void onError(final String errorMessage) {
        try {
            if (callback != null) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(errorMessage);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void processImages(List<String> uris) {
        ImageProcessorThread thread = new ImageProcessorThread(getContext(), getImageObjects(uris), cacheLocation);
        if (maxWidth != -1 && maxHeight != -1) {
            thread.setOutputImageDimensions(maxWidth, maxHeight);
        }
        thread.setRequestId(requestId);
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
