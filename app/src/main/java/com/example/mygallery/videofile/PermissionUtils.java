package com.example.mygallery.videofile;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public final class PermissionUtils {

    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == 0;
    }

    public static boolean isDeviceInfoGranted(Context context) {
        return checkPermission(context, "android.permission.READ_EXTERNAL_STORAGE");
    }
    public static boolean isDeviceInfoGrantednextt(Context context) {
        return checkPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
    }
    public static void requestPermissions(Object o, int permissionId, String... permissions) {
        if (o instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
        }
    }
}
