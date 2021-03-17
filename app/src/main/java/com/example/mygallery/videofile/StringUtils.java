package com.example.mygallery.videofile;

import android.content.Context;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class StringUtils {
    public static String getMimeType(String path) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(path.lastIndexOf(46) + 1));
    }

    public static String getPhotoNameByPath(String path) {
        String[] b = path.split("/");
        String fi = b[b.length - 1];
        return fi.substring(0, fi.lastIndexOf(46));
    }

    public static String getBucketPathByImagePath(String path) {
        String[] b = path.split("/");
        String c = "";
        for (int x = 0; x < b.length - 1; x++) {
            c = c + b[x] + "/";
        }
        return c.substring(0, c.length() - 1);
    }

    public static void showToast(Context x, String s) {
        Toast.makeText(x, s, Toast.LENGTH_SHORT).show();
    }
}
