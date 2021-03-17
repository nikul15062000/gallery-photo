package com.example.mygallery.foldersvideolist;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;

import com.example.mygallery.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileDataHelper {
    public static String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        return new DecimalFormat("#,##0.#").format(((double) size) / Math.pow(1024.0d, (double) digitGroups)) + " " + new String[]{"B", "KB", "MB", "GB", "TB"}[digitGroups];
    }

    public static String getFileDurationFormated(int duration) {
        int mns = (duration % 3600000) / 60000;
        int scs = ((duration % 3600000) % 60000) / 1000;
        if (duration / 3600000 > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(duration / 3600000), Integer.valueOf(mns), Integer.valueOf(scs)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(mns), Integer.valueOf(scs)});
    }


}
