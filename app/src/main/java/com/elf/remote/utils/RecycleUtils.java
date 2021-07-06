package com.elf.remote.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class RecycleUtils {

    public static void recursiveRecycle(View root) {

        if (root == null)
            return;

        root.setBackground(null);

        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursiveRecycle(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView)) {
                group.removeAllViews();
            }
        }

        if (root instanceof ImageView) {
            ((ImageView) root).setImageDrawable(null);
        }
    }

    public static void recursiveRecycle(List<WeakReference<View>> recycleList) {
        for (WeakReference<View> ref : recycleList)
            recursiveRecycle(ref.get());
    }

    public static Bitmap getThumbnail(String path, Context context) {
        int columnIndex;
        long ImageId = 0;
        Bitmap thumbnail = null;
        Size thumbSize = new Size(100, 100);
        Uri uri = Uri.parse(path);
        String filePath = uri.getPath();

        String[] filePathColumn = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE};

        ContentResolver cor = context.getContentResolver();

        Cursor cursor = cor.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, filePathColumn, "_data = '" + filePath + "'", null, null);

        if (cursor != null) {
            cursor.moveToNext();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            ImageId = cursor.getLong(columnIndex);
            cursor.close();
        }

        Uri uri1 = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ImageId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                thumbnail = cor.loadThumbnail(uri1, thumbSize, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 4;
                thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
                        context.getContentResolver(), ImageId,
                        MediaStore.Video.Thumbnails.MINI_KIND,
                        bmOptions);
            }
        }
        return thumbnail;
    }
}