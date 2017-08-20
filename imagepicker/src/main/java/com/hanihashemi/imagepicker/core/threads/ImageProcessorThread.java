package com.hanihashemi.imagepicker.core.threads;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.TypedValue;

import com.hanihashemi.imagepicker.R;
import com.hanihashemi.imagepicker.api.callbacks.ImagePickerCallback;
import com.hanihashemi.imagepicker.api.entity.ChosenImage;
import com.hanihashemi.imagepicker.api.exceptions.PickerException;
import com.hanihashemi.imagepicker.utils.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

/**
 * Created by kbibek on 2/20/16.
 */
public final class ImageProcessorThread extends FileProcessorThread {
    private final static String TAG = ImageProcessorThread.class.getSimpleName();

    private boolean shouldGenerateThumbnails;
    private boolean shouldGenerateMetadata;
    private boolean shouldCrop;
    private int maxImageWidth = -1;
    private int maxImageHeight = -1;
    private ImagePickerCallback callback;
    private UCrop.Options options;

    public ImageProcessorThread(Context context, List<ChosenImage> paths, int cacheLocation) {
        super(context, paths, cacheLocation);
    }

    /**
     * UCrop options
     */
    public void setUCropOptions(UCrop.Options options) {
        this.options = options;
    }

    public void setShouldCrop(boolean shouldCrop) {
        this.shouldCrop = shouldCrop;
    }

    public void setShouldGenerateThumbnails(boolean shouldGenerateThumbnails) {
        this.shouldGenerateThumbnails = shouldGenerateThumbnails;
    }

    public void setImagePickerCallback(ImagePickerCallback callback) {
        this.callback = callback;
    }

    private int fetchColor(int colorId) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{colorId});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    public void run() {
        super.run();
        if (shouldCrop) {
            UCrop.Options alterOptions = new UCrop.Options();
            alterOptions.setActiveWidgetColor(fetchColor(R.attr.colorAccent));
            alterOptions.setToolbarColor(fetchColor(R.attr.colorPrimary));

            UCrop.of(Uri.fromFile(new File(files.get(0).getOriginalPath())), Uri.fromFile(new File(files.get(0).getOriginalPath())))
                    .withAspectRatio(1, 1)
                    .withOptions(this.options == null ? alterOptions : this.options)
                    .start((Activity) context);
        } else {
            postProcessImages();
            onDone();
        }
    }

    private void onDone() {
        try {
            if (callback != null) {
                getActivityFromContext().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onImagesChosen((List<ChosenImage>) files);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void postProcessImages() {
        for (ChosenImage image : files) {
            try {
                postProcessImage(image);
                image.setSuccess(true);
            } catch (PickerException e) {
                e.printStackTrace();
                image.setSuccess(false);
            }
        }
    }

    private ChosenImage postProcessImage(ChosenImage image) throws PickerException {
        if (maxImageWidth != -1 && maxImageHeight != -1) {
            image = ensureMaxWidthAndHeight(maxImageWidth, maxImageHeight, image);
        }
        Logger.d(TAG, "postProcessImage: " + image.getMimeType());
        if (shouldGenerateMetadata) {
            try {
                image = generateMetadata(image);
            } catch (Exception e) {
                Logger.d(TAG, "postProcessImage: Error generating metadata");
                e.printStackTrace();
            }
        }
        if (shouldGenerateThumbnails) {
            image = generateThumbnails(image);
        }
        Logger.d(TAG, "postProcessImage: " + image);
        return image;
    }

    private ChosenImage generateMetadata(ChosenImage image) {
        float[] latLong = getLatLong(image.getOriginalPath());
        if (latLong != null) {
            image.setLat(latLong[0]);
            image.setLng(latLong[1]);
        }
        image.setWidth(Integer.parseInt(getWidthOfImage(image.getOriginalPath())));
        image.setHeight(Integer.parseInt(getHeightOfImage(image.getOriginalPath())));
        image.setOrientation(getOrientation(image.getOriginalPath()));
        return image;
    }

    private ChosenImage generateThumbnails(ChosenImage image) throws PickerException {
        String thumbnailBig = downScaleAndSaveImage(image.getOriginalPath(), THUMBNAIL_BIG);
        image.setOriginalPath(thumbnailBig);
        String thumbnailSmall = downScaleAndSaveImage(image.getOriginalPath(), THUMBNAIL_SMALL);
        image.setThumbnailSmallPath(thumbnailSmall);
        return image;
    }

    public void setShouldGenerateMetadata(boolean shouldGenerateMetadata) {
        this.shouldGenerateMetadata = shouldGenerateMetadata;
    }

    public void setOutputImageDimensions(int maxWidth, int maxHeight) {
        this.maxImageWidth = maxWidth;
        this.maxImageHeight = maxHeight;
    }
}