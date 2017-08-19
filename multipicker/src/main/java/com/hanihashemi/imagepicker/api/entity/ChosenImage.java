package com.hanihashemi.imagepicker.api.entity;

import android.media.ExifInterface;
import android.os.Parcel;

/**
 * Contains details about the image that was chosen
 */
public class ChosenImage extends ChosenFile {
    public static final Creator<ChosenImage> CREATOR = new Creator<ChosenImage>() {
        @Override
        public ChosenImage createFromParcel(Parcel source) {
            return new ChosenImage(source);
        }

        @Override
        public ChosenImage[] newArray(int size) {
            return new ChosenImage[size];
        }
    };
    private final static String STRING_FORMAT = "Height: %s, Width: %s, Orientation: %s";
    private int orientation;
    private String thumbnailPath;
    private String thumbnailSmallPath;
    private int width;
    private int height;
    private float lat;
    private float lng;

    public ChosenImage() {

    }

    protected ChosenImage(Parcel in) {
        super(in);
        this.orientation = in.readInt();
        this.thumbnailPath = in.readString();
        this.thumbnailSmallPath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.lat = in.readFloat();
        this.lng = in.readFloat();
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public boolean hasLocation() {
        return lat > 1 && lng > 1;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    /**
     * Get orientation of the actual image
     *
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Get the path to the thumbnail(big) of the image
     *
     * @return
     */
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    /**
     * Get the path to the thumbnail(small) of the image
     *
     * @return
     */
    public String getThumbnailSmallPath() {
        return thumbnailSmallPath;
    }

    public void setThumbnailSmallPath(String thumbnailSmallPath) {
        this.thumbnailSmallPath = thumbnailSmallPath;
    }

    /**
     * Get the image width
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the image height;
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        String result = "";
        result += "type: " + getType() + "\r\n";
        result += "width: " + getWidth() + "\r\n";
        result += "height: " + getHeight() + "\r\n";
        result += "orientation: " + getOrientationName() + "\r\n";
        result += "has location: " + hasLocation() + "\r\n";
        if (hasLocation()) {
            result += "lat: " + getLat() + "\r\n";
            result += "lng: " + getLng() + "\r\n";
        }
        result += "original path: " + getOriginalPath() + "\r\n";
        result += "thumbnail: " + getThumbnailPath() + "\r\n";
        result += "small thumbnail: " + getThumbnailSmallPath() + "\r\n";

        return result;
    }

    /**
     * Get Orientation user friendly label
     *
     * @return
     */
    public String getOrientationName() {
        String orientationName = "NORMAL";
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                orientationName = "FLIP_HORIZONTAL";
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                orientationName = "FLIP_VERTICAL";
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientationName = "ROTATE_90";
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientationName = "ROTATE_180";
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientationName = "ROTATE_270";
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                orientationName = "TRANSPOSE";
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                orientationName = "TRANSVERSE";
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                orientationName = "UNDEFINED";
                break;
        }
        return orientationName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.orientation);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.thumbnailSmallPath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lng);
    }
}
