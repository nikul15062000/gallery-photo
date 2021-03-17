package com.example.mygallery.videofile;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


public class StorageProvider {
    private PreferenceUtil SP;
    private ArrayList<File> excludedFolders;
    private boolean includeVideo = true;

    public StorageProvider(Context context) {
        this.SP = PreferenceUtil.getInstance(context);
        this.excludedFolders = getExcludedFolders(context);
    }

    public ArrayList<Album> getAlbums(Context context, boolean hidden) {
        ArrayList<Album> list = new ArrayList();
        this.includeVideo = this.SP.getBoolean("set_include_video", false);
        Iterator it;
        if (hidden) {
            it = ContentHelper.getStorageRoots(context).iterator();
            while (it.hasNext()) {
                fetchRecursivelyHiddenFolder(context, (File) it.next(), list);
            }
        } else {
            it = ContentHelper.getStorageRoots(context).iterator();
            while (it.hasNext()) {
                fetchRecursivelyFolder(context, (File) it.next(), list);
            }
        }
        return list;
    }

    private ArrayList<File> getExcludedFolders(Context context) {
        ArrayList<File> list = new ArrayList();
        Iterator it = ContentHelper.getStorageRoots(context).iterator();
        while (it.hasNext()) {
            list.add(new File(((File) it.next()).getPath(), "Android"));
        }
        list.addAll(CustomAlbumsHelper.getInstance(context).getExcludedFolders());
        return list;
    }

    private void fetchRecursivelyHiddenFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        if (!this.excludedFolders.contains(dir)) {
            File[] folders = dir.listFiles(new FoldersFileFilter());
            if (folders != null) {
                for (File temp : folders) {
                    File nomedia = new File(temp, ".nomedia");
                    if (!this.excludedFolders.contains(temp) && (nomedia.exists() || temp.isHidden())) {
                        checkAndAddFolder(context, temp, albumArrayList);
                    }
                    fetchRecursivelyHiddenFolder(context, temp, albumArrayList);
                }
            }
        }
    }

    private void fetchRecursivelyFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        if (!this.excludedFolders.contains(dir)) {
            checkAndAddFolder(context, dir, albumArrayList);
            File[] children = dir.listFiles(new FoldersFileFilter());
            if (children != null) {
                for (File temp : children) {
                    File nomedia = new File(temp, ".nomedia");
                    if (!(this.excludedFolders.contains(temp) || temp.isHidden() || nomedia.exists())) {
                        fetchRecursivelyFolder(context, temp, albumArrayList);
                    }
                }
            }
        }
    }

    private void checkAndAddFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        File[] files = dir.listFiles(new ImageFileFilter(this.includeVideo));
        if (files != null && files.length > 0) {
            Album asd = new Album(context, dir.getAbsolutePath(), -1, dir.getName(), files.length);
            long lastMod = Long.MIN_VALUE;
            File choice = null;
            for (File file : files) {
                if (file.lastModified() > lastMod) {
                    choice = file;
                    lastMod = file.lastModified();
                }
            }
            if (choice != null) {
                asd.addMedia(new Media(choice.getAbsolutePath(), choice.lastModified()));
            }
            albumArrayList.add(asd);
        }
    }

    public static ArrayList<Media> getMedia(String path, boolean includeVideo) {
        ArrayList<Media> list = new ArrayList();
        for (File image : new File(path).listFiles(new ImageFileFilter(includeVideo))) {
            list.add(new Media(image));
        }
        return list;
    }
}
