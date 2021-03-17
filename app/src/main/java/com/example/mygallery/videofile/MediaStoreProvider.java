package com.example.mygallery.videofile;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Files;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MediaStoreProvider {
    private static ArrayList<String> excludedAlbums;

    public static ArrayList<Album> getAlbums(Context context, boolean hidden) {
        excludedAlbums = getExcludedFolders(context);
        return hidden ? getHiddenAlbums(context) : getAlbums(context);
    }

    private static ArrayList<Album> getHiddenAlbums(Context context) {
        ArrayList<Album> list = new ArrayList();
        Cursor cur = context.getContentResolver().query(Files.getContentUri("external"), new String[]{"_data", "parent"}, "media_type=0 and _data LIKE '%.nomedia'", null, null);
        if (cur != null && cur.moveToFirst()) {
            do {
                File folder = new File(cur.getString(0)).getParentFile();
                File[] files = folder.listFiles(new ImageFileFilter(true));
                if (files != null && files.length > 0) {
                    Album album = new Album(context, folder.getAbsolutePath(), -1, folder.getName(), files.length);
                    long lastMod = Long.MIN_VALUE;
                    File f = null;
                    for (File file : files) {
                        if (file.lastModified() > lastMod) {
                            f = file;
                            lastMod = file.lastModified();
                        }
                    }
                    if (!(f == null || isExcluded(f.getPath()))) {
                        album.addMedia(new Media(f.getPath(), f.lastModified()));
                        list.add(album);
                    }
                }
            } while (cur.moveToNext());
            cur.close();
        }
        return list;
    }

    private static boolean isExcluded(String path) {
        Iterator it = excludedAlbums.iterator();
        while (it.hasNext()) {
            if (path.startsWith((String) it.next())) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<Album> getAlbums(Context context) {
        String selection;
        String[] selectionArgs;
        ArrayList<Album> list = new ArrayList();
        String[] projection = new String[]{"parent", "bucket_display_name"};
        if (PreferenceUtil.getInstance(context).getBoolean("set_include_video", true)) {
            selection = "media_type=? or media_type=? ) GROUP BY ( parent ";
            selectionArgs = new String[]{String.valueOf(3), String.valueOf(3)};
        } else {
            selection = "media_type=? ) GROUP BY ( parent ";
            selectionArgs = new String[]{String.valueOf(3)};
        }
        Cursor cur = context.getContentResolver().query(Files.getContentUri("external"), projection, selection, selectionArgs, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                int idColumn = cur.getColumnIndex("parent");
                int nameColumn = cur.getColumnIndex("bucket_display_name");
                do {
                    Media media = getLastMedia(context, cur.getLong(idColumn));
                    if (!(media == null || media.getPath() == null)) {
                        String path = StringUtils.getBucketPathByImagePath(media.getPath());
                        if (!isExcluded(path)) {
                            Context context2 = context;
                            Album album = new Album(context2, path, cur.getLong(idColumn), cur.getString(nameColumn), getAlbumCount(context, cur.getLong(idColumn)));
                            if (album.addMedia(getLastMedia(context, album.getId()))) {
                                list.add(album);
                            }
                        }
                    }
                } while (cur.moveToNext());
            }
            cur.close();
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static ArrayList<String> getExcludedFolders(Context context) {
        ArrayList<String> list = new ArrayList();
        Iterator it = ContentHelper.getStorageRoots(context).iterator();
        while (it.hasNext()) {
            list.add(new File(((File) it.next()).getPath(), "Android").getPath());
        }
        list.addAll(CustomAlbumsHelper.getInstance(context).getExcludedFoldersPaths());
        return list;
    }

    private static int getAlbumCount(Context context, long id) {
        String[] selectionArgs = new String[]{String.valueOf(3), String.valueOf(3), String.valueOf(id)};
        Cursor cur = context.getContentResolver().query(Files.getContentUri("external"), new String[]{"parent"}, "( media_type=? or media_type=? ) and parent=?", selectionArgs, null);
        if (cur == null) {
            return 0;
        }
        int c = cur.getCount();
        cur.close();
        return c;
    }

    @Nullable
    private static Media getLastMedia(Context context, long albumId) {
        ArrayList<Media> list = getMedia(context, albumId, 1, true);
        return list.size() > 0 ? (Media) list.get(0) : null;
    }

    public static ArrayList<Media> getMedia(Context context, long albumId, boolean includeVideo) {
        return getMedia(context, albumId, -1, includeVideo);
    }

    private static ArrayList<Media> getMedia(Context context, long albumId, int n, boolean includeVideo) {
        String limit;
        String selection;
        String[] selectionArgs;
        if (n == -1) {
            limit = "";
        } else {
            limit = "LIMIT " + n;
        }
        ArrayList<Media> list = new ArrayList();
        String[] projection = new String[]{"_data", "datetaken", "mime_type", "_size", "orientation", "duration"};
        Uri images = Files.getContentUri("external");
        if (includeVideo) {
            selection = "( media_type=? or media_type=? ) and parent=?";
            selectionArgs = new String[]{String.valueOf(3), String.valueOf(3), String.valueOf(albumId)};
        } else {
            selection = "media_type=?  and parent=?";
            selectionArgs = new String[]{String.valueOf(3), String.valueOf(albumId)};
        }
        Cursor cur = context.getContentResolver().query(images, projection, selection, selectionArgs, " datetaken DESC " + limit);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    list.add(new Media(cur));
                } while (cur.moveToNext());
            }
            cur.close();
        }
        return list;
    }

}
