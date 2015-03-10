package com.talent.taskmanager.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by chris on 14-12-28.
 */
public class FileOperationUtils {
    /**
     * Copy file from old path to new path
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean isOK = true;
        try {
            int byteRead = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fos = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteRead);
                }
                fos.flush();
                fos.close();
                inStream.close();
            } else {
                isOK = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isOK = false;
        }
        return isOK;
    }

    /**
     * Save bitmap to image file
     *
     * @param bitmap
     * @param filePath
     */
    public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get type of file
     *
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String end = file
                .getName()
                .substring(file.getName().lastIndexOf(".") + 1,
                        file.getName().length()).toLowerCase();
        String type = "";
        if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
                || end.equals("amr") || end.equals("mpeg") || end.equals("mp4")) {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg")) {
            type = "image";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }

    /**
     * Open file
     *
     * @param file
     * @param context
     */
    public static void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        context.startActivity(intent);
    }

    /**
     * Compress quality of image to less than 100kB. {@link:
     * http://www.104zz.iteye.com/blog/1694762}
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * Compress image in proportion by its path
     *
     * @param srcPath
     * @return
     */
    public static Bitmap compressImageBySrc(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);
    }

    /**
     * Compress image in proportion by its original bitmap
     *
     * @param image
     * @return
     */
    public static Bitmap compressImageByBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // if > 1M, avoid out of memory when do BitmapFactory.decodeStream.
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }

    /**
     * Get path of file by its uri.
     *
     * @param uri
     * @param context
     * @return
     */
    public static String getFilePathByUri(Uri uri, Context context) {
        String path = null;
        if ("content".equals(uri.getScheme())) {
            // Start with content://
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor mediaCursor = context.getContentResolver().query(uri, proj,
                    null, null, null);
            int index = mediaCursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            mediaCursor.moveToFirst();
            path = mediaCursor.getString(index);
            mediaCursor.close();
        } else if ("file".equals(uri.getScheme())) {
            // Start with file://, may include Chinese character
            path = uri.toString().replace("file://", "");
            try {
                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * eg: /../../**.mp3 -> **.mp3
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path) {
        if (path == null)
            return null;
        int start = path.lastIndexOf("/");
        if (start != -1) {
            return path.substring(start + 1, path.length());
        } else {
            return null;
        }
    }

}
