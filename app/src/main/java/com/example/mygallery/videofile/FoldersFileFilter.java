package com.example.mygallery.videofile;

import java.io.File;
import java.io.FileFilter;

public class FoldersFileFilter implements FileFilter {
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}
