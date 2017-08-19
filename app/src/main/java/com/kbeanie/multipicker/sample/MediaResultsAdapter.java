package com.kbeanie.multipicker.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hanihashemi.imagepicker.api.entity.ChosenImage;

import java.io.File;
import java.util.List;

/**
 * Created by kbibek on 2/24/16.
 */
public class MediaResultsAdapter extends BaseAdapter {
    private final static String TAG = MediaResultsAdapter.class.getSimpleName();

    private final static int TYPE_IMAGE = 0;

    private final static String FORMAT_IMAGE_VIDEO_DIMENSIONS = "%sw x %sh";
    private final static String FORMAT_IMAGE_LOCATION = "%s x %s";
    private final static String FORMAT_ORIENTATION = "Ortn: %s";

    private final Context context;
    private List<? extends ChosenImage> files;

    public MediaResultsAdapter(List<? extends ChosenImage> files, Context context) {
        this.files = files;
        this.context = context;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: " + files.size());
        ChosenImage file = (ChosenImage) getItem(position);
        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            switch (itemViewType) {
                case TYPE_IMAGE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.adapter_images, null);
                    break;
            }
        }

        switch (itemViewType) {
            case TYPE_IMAGE:
                showImage(file, convertView);
                break;
        }
        return convertView;
    }

    private void showImage(ChosenImage file, View view) {
        final ChosenImage image = file;

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(file.getDisplayName());

        TextView tvCompleteMimeType = (TextView) view.findViewById(R.id.tvCompleteMimeType);
        tvCompleteMimeType.setText(file.getMimeType());

        ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
        if (image.getThumbnailSmallPath() != null) {
            Glide.with(context).load(Uri.fromFile(new File(image.getThumbnailSmallPath()))).into(ivImage);
        }

        TextView tvDimension = (TextView) view.findViewById(R.id.tvDimension);
        tvDimension.setText(String.format(FORMAT_IMAGE_VIDEO_DIMENSIONS, image.getWidth(), image.getHeight()));

        TextView tvMimeType = (TextView) view.findViewById(R.id.tvMimeType);
        tvMimeType.setText(file.getFileExtensionFromMimeTypeWithoutDot());

        TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
        tvSize.setText(file.getHumanReadableSize(false));

        TextView tvOrientation = (TextView) view.findViewById(R.id.tvOrientation);
        tvOrientation.setText(String.format(FORMAT_ORIENTATION, image.getOrientationName()));

        TextView tvLocation = (TextView) view.findViewById(R.id.tvLoction);
        if (image.hasLocation()) {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(String.format(FORMAT_IMAGE_LOCATION, image.getLat(), image.getLng()));
        } else
            tvLocation.setVisibility(View.GONE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Tapped: " + image.getOriginalPath());
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra("chosen", image);
                intent.putExtra("uri", image.getOriginalPath());
                intent.putExtra("mimetype", image.getMimeType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_IMAGE;
    }
}
