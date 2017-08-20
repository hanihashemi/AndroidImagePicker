package com.hanihashemi.imagepicker.api.entity;

import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Contains details about the file that was chosen.
 */
public class ChosenImage implements Parcelable {
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
    private final static String STRING_FORMAT = "Type: %s, QueryUri: %s, Original Path: %s, MimeType: %s, Size: %s";
    private long id;
    private String queryUri;
    private String originalPath;
    private String mimeType;
    private long size;
    private String extension;
    private Date createdAt;
    private String type;
    private String displayName;
    private boolean success;
    private String tempFile = "";
    private String directoryType;
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
        this.id = in.readLong();
        this.queryUri = in.readString();
        this.originalPath = in.readString();
        this.mimeType = in.readString();
        this.size = in.readLong();
        this.extension = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.type = in.readString();
        this.displayName = in.readString();
        this.success = in.readByte() != 0;
        this.tempFile = in.readString();
        this.directoryType = in.readString();
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

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getThumbnailSmallPath() {
        return thumbnailSmallPath;
    }

    public void setThumbnailSmallPath(String thumbnailSmallPath) {
        this.thumbnailSmallPath = thumbnailSmallPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * If this file has been successfully processed.
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Display name of the file
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Internal use
     *
     * @return
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Internal use
     *
     * @return
     */
    public String getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(String directoryType) {
        this.directoryType = directoryType;
    }

    public String getQueryUri() {
        return queryUri;
    }

    public void setQueryUri(String queryUri) {
        this.queryUri = queryUri;
    }

    /**
     * Path to the processed file. This will always be a local path on the device.
     *
     * @return
     */
    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    /**
     * Get mimetype of the file
     *
     * @return
     */
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Get the size of the processed file in bytes
     *
     * @return
     */
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * For internal use
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the extension of the file
     * Ex. .pdf, .jpeg, .mp4
     *
     * @return
     */
    public String getFileExtensionFromMimeType() {
        String extension = "";
        if (mimeType != null) {
            String[] parts = mimeType.split("/");
            if (parts.length >= 2) {
                if (!parts[1].equals("*")) {
                    extension = "." + parts[1];
                }
            }
        }
        return extension;
    }

    /**
     * Get only the file extension (Ex. jpg, mp4, pdf etc)
     *
     * @return
     */
    public String getFileExtensionFromMimeTypeWithoutDot() {
        return getFileExtensionFromMimeType().replace(".", "");
    }

    @Override
    public String toString() {
        return String.format(STRING_FORMAT, type, queryUri, originalPath, mimeType, getHumanReadableSize(false));
    }

    /**
     * Get File size in a pretty format.
     *
     * @param si
     * @return
     */
    public String getHumanReadableSize(boolean si) {
        int unit = si ? 1000 : 1024;
        if (size < unit) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format(Locale.ENGLISH, "%.1f %sB", size / Math.pow(unit, exp), pre);
    }

    /**
     * Get Duration (for audio and video) in a pretty format
     *
     * @param duration
     * @return
     */
    public String getHumanReadableDuration(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public String getTempFile() {
        return tempFile;
    }

    public void setTempFile(String tempFile) {
        this.tempFile = tempFile;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

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
        dest.writeLong(this.id);
        dest.writeString(this.queryUri);
        dest.writeString(this.originalPath);
        dest.writeString(this.mimeType);
        dest.writeLong(this.size);
        dest.writeString(this.extension);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeString(this.type);
        dest.writeString(this.displayName);
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        dest.writeString(this.tempFile);
        dest.writeString(this.directoryType);
        dest.writeInt(this.orientation);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.thumbnailSmallPath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lng);
    }
}
