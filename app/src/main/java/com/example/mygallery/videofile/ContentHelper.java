package com.example.mygallery.videofile;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
/*import androidx.annotation.NonNull;*/
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;
/*import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;*/
import android.util.Log;

import com.example.mygallery.R;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;


public class ContentHelper {
    private static final String TAG = "ContentHelper";

    private static void scanFile(Context context, String[] paths) {
        MediaScannerConnection.scanFile(context, paths, null, null);
    }

    public static boolean deleteFilesInFolder(Context context, @NonNull File folder) {
        boolean totalSuccess = true;
        String[] children = folder.list();
        if (children != null) {
            for (String child : children) {
                File file = new File(folder, child);
                if (!(file.isDirectory() || deleteFile(context, file))) {
                    Log.w(TAG, "Failed to delete file" + child);
                    totalSuccess = false;
                }
            }
        }
        return totalSuccess;
    }

    public static boolean deleteFile(Context context, @NonNull File file) {
        boolean success = file.delete();
        if (!success && VERSION.SDK_INT >= 21) {
            DocumentFile document = getDocumentFile(context, file, false, false);
            success = document != null && document.delete();
        }
        if (!success && VERSION.SDK_INT == 19) {
            ContentResolver resolver = context.getContentResolver();
            if (null != null) {
                try {
                    resolver.delete(null, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "Error when deleting file " + file.getAbsolutePath(), e);
                    return false;
                }
            }
            if (file.exists()) {
                success = false;
            } else {
                success = true;
            }
        }
        if (success) {
            scanFile(context, new String[]{file.getPath()});
        }
        return success;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static HashSet<File> getStorageRoots(Context context) {
        HashSet<File> paths = new HashSet();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("asd", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    paths.add(new File(file.getAbsolutePath().substring(0, index)));
                }
            }
        }
        return paths;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getSdcardPath(Context context) {
        for (File file : context.getExternalFilesDirs("external")) {
            if (!(file == null || file.equals(context.getExternalFilesDir("external")))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index >= 0) {
                    return new File(file.getAbsolutePath().substring(0, index)).getPath();
                }
                Log.w("asd", "Unexpected external file dir: " + file.getAbsolutePath());
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static DocumentFile getDocumentFile(Context context, @NonNull File file, boolean isDirectory, boolean createDirectories) {
        Uri treeUri = getTreeUri(context);
        if (treeUri == null) {
            return null;
        }
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        String sdcardPath = getSavedSdcardPath(context);
        String suffixPathPart = null;
        if (sdcardPath == null) {
            Iterator it = getStorageRoots(context).iterator();
            while (it.hasNext()) {
                File root = (File) it.next();
                if (!(root == null || file.getPath().indexOf(root.getPath()) == -1)) {
                    suffixPathPart = file.getAbsolutePath().substring(file.getPath().length());
                }
            }
        } else if (file.getPath().indexOf(sdcardPath) != -1) {
            suffixPathPart = file.getAbsolutePath().substring(sdcardPath.length());
        }
        if (suffixPathPart == null) {
            Log.d(TAG, "unable to find the document file, filePath:" + file.getPath() + " root: " + sdcardPath);
            return null;
        }
        if (suffixPathPart.startsWith(File.separator)) {
            suffixPathPart = suffixPathPart.substring(1);
        }
        String[] parts = suffixPathPart.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (document.findFile(parts[i]) != null) {
                document = document.findFile(parts[i]);
            } else if (i < parts.length - 1) {
                if (!createDirectories) {
                    return null;
                }
                document = document.createDirectory(parts[i]);
            } else if (!isDirectory) {
                return document.createFile("image", parts[i]);
            } else {
                document = document.createDirectory(parts[i]);
            }
        }
        return document;
    }

    private static Uri getTreeUri(Context context) {
        String uriString = PreferenceUtil.getInstance(context).getString(context.getString(R.string.preference_internal_uri_extsdcard_photos), null);
        if (uriString == null) {
            return null;
        }
        return Uri.parse(uriString);
    }

    @SuppressLint("NewApi")
    public static void saveSdCardInfo(Context context, @Nullable Uri uri) {
        String str;
        Editor editor = PreferenceUtil.getInstance(context).getEditor();
        String string = context.getString(R.string.preference_internal_uri_extsdcard_photos);
        if (uri == null) {
            str = null;
        } else {
            str = uri.toString();
        }
        editor.putString(string, str);
        editor.putString("sd_card_path", getSdcardPath(context));
        editor.commit();
    }

    private static String getSavedSdcardPath(Context context) {
        return PreferenceUtil.getInstance(context).getString("sd_card_path", null);
    }
}
