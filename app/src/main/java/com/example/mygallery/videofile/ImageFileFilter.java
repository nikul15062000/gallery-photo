package com.example.mygallery.videofile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class ImageFileFilter implements FilenameFilter {
    private static String[] gifsExtensions = new String[]{"gif"};
    private static String[] imagesExtensions = new String[]{"jpg", "png", "jpe", "jpeg", "bmp", "webp"};
    private static String[] videoExtensions = new String[]{"mp4", "mkv", "webm", "avi"};
    private HashSet<String> extensions;

    private ImageFileFilter(FilterMode filter) {
        this.extensions = new HashSet();
        switch (filter) {
            case IMAGES:
                this.extensions.addAll(Arrays.asList(imagesExtensions));
                return;
            case VIDEO:
                this.extensions.addAll(Arrays.asList(videoExtensions));
                return;
            case GIF:
                this.extensions.addAll(Arrays.asList(gifsExtensions));
                return;
            case NO_VIDEO:
                this.extensions.addAll(Arrays.asList(imagesExtensions));
                this.extensions.addAll(Arrays.asList(gifsExtensions));
                return;
            default:
                this.extensions.addAll(Arrays.asList(imagesExtensions));
                this.extensions.addAll(Arrays.asList(videoExtensions));
                this.extensions.addAll(Arrays.asList(gifsExtensions));
                return;
        }
    }

    public ImageFileFilter(boolean includeVideo) {
        this(includeVideo ? FilterMode.ALL : FilterMode.NO_VIDEO);
    }

    public boolean accept(File dir, String filename) {
        if (new File(dir, filename).isFile()) {
            Iterator it = this.extensions.iterator();
            while (it.hasNext()) {
                if (filename.toLowerCase().endsWith((String) it.next())) {
                    return true;
                }
            }
        }
        return false;
    }
}
