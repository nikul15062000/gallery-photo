package com.example.mygallery.Statusaver.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;


import com.example.mygallery.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {


    public static void mediaScanner(Context context, String newFilePath, String oldFilePath, String fileType) {
        try {
                MediaScannerConnection.scanFile(context, new String[]{newFilePath + new File(oldFilePath).getName()}, new String[]{fileType},
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            public void onMediaScannerConnected() {
                            }

                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBack(String paramString1, String paramString2) {
        Matcher localMatcher = Pattern.compile(paramString2).matcher(paramString1);
        if (localMatcher.find()) {
            return localMatcher.group(1);
        }
        return "";
    }

    public static boolean download(Context context, String path) {
        if (copyFileInSavedDir(context, path)) {
           return true;
        } else {
            return false;
        }
    }

    static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


    static boolean copyFileInSavedDir(Context context, String file) {
        try {
            if (isImageFile(file)) {
                FileUtils.copyFileToDirectory(new File(file), getDir(context,"StatusSaverImages"));
                Utils.mediaScanner(context, getDir(context,"StatusSaverImages") + "/", file, "image/*");
            } else {
                FileUtils.copyFileToDirectory(new File(file), getDir(context,"StatusSaverVideos"));
                Utils.mediaScanner(context, getDir(context,"StatusSaverVideos") + "/", file, "video/*");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static File getDir(Context context, String folder) {

        File rootFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + context.getResources().getString(R.string.app_name) + File.separator + folder);
        rootFile.mkdirs();

        return rootFile;

    }
}
