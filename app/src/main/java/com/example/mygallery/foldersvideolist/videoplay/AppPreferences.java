package com.example.mygallery.foldersvideolist.videoplay;

import android.content.Context;
import android.preference.PreferenceManager;

import com.example.mygallery.foldersvideolist.MediaFile;


public class AppPreferences {
    public static void setResumePositionByPath(Context c, MediaFile mediaFile) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(mediaFile.getPath(), mediaFile.getResumePosition()).commit();
    }
    public static int getResumePositionByPath(Context c, String path) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(path, 0);
    }
}
