package com.example.mygallery.videofile;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.xmp.XmpDirectory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

class MetadataItem {
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_ROTATE_270 = 8;
    public static final int ORIENTATION_ROTATE_90 = 6;
    private static Set<Class<?>> usefullDirectories = new HashSet();
    private Date dateOriginal = null;
    private String exposureTime = null;
    private String fNumber = null;
    private int height = -1;
    private String iso = null;
    private GeoLocation location = null;
    private String make = null;
    private String model = null;
    private int orientation = -1;
    private int width = -1;

    static {
        usefullDirectories.add(ExifIFD0Directory.class);
        usefullDirectories.add(ExifSubIFDDirectory.class);
        usefullDirectories.add(GpsDirectory.class);
        usefullDirectories.add(XmpDirectory.class);
    }

    MetadataItem(File file) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        setWidth(options.outWidth);
        setHeight(options.outHeight);
        try {
            for (Directory directory : ImageMetadataReader.readMetadata(file).getDirectories()) {
                if (usefullDirectories.contains(directory.getClass())) {
                    if (directory.getClass().equals(ExifSubIFDDirectory.class) || directory.getClass().equals(ExifIFD0Directory.class)) {
                        ExifDirectoryBase d = (ExifDirectoryBase) directory;
                        if (d.containsTag(ExifDirectoryBase.TAG_MAKE)) {
                            setMake(d.getString(ExifDirectoryBase.TAG_MAKE));
                        }
                        if (d.containsTag(ExifDirectoryBase.TAG_MODEL)) {
                            setModel(d.getString(ExifDirectoryBase.TAG_MODEL));
                        }
                        if (d.containsTag(ExifDirectoryBase.TAG_ISO_EQUIVALENT)) {
                            setIso(d.getString(ExifDirectoryBase.TAG_ISO_EQUIVALENT));
                        }
                        if (d.containsTag(ExifDirectoryBase.TAG_EXPOSURE_TIME) && d.getRational(ExifDirectoryBase.TAG_EXPOSURE_TIME) != null) {
                            setExposureTime(new DecimalFormat("0.000").format(d.getRational(ExifDirectoryBase.TAG_EXPOSURE_TIME)));
                        }
                        if (d.containsTag(ExifDirectoryBase.TAG_FNUMBER)) {
                            setfNumber(d.getString(ExifDirectoryBase.TAG_FNUMBER));
                        }
                        if (d.containsTag(ExifDirectoryBase.TAG_DATETIME_ORIGINAL)) {
                            setDateOriginal(d.getDate(ExifDirectoryBase.TAG_DATETIME_ORIGINAL));
                        }
                    } else if (directory.getClass().equals(ExifSubIFDDirectory.class)) {
                    } else if (directory.getClass().equals(XmpDirectory.class)) {
                        XmpDirectory d2 = (XmpDirectory) directory;
                        if (d2.containsTag(13)) {
                            setDateOriginal(d2.getDate(13));
                        }
                        if (d2.containsTag(1)) {
                            setMake(d2.getString(1));
                        }
                        if (d2.containsTag(2)) {
                            setModel(d2.getString(2));
                        }
                        if (d2.containsTag(5)) {
                            setfNumber(d2.getString(5));
                        }
                    } else if (directory.getClass().equals(GpsDirectory.class)) {
                        setLocation(((GpsDirectory) directory).getGeoLocation());
                    }
                }
            }
        } catch (ImageProcessingException e) {
            Log.wtf("asd", "logMainTags: ImageProcessingException", e);
        } catch (IOException e2) {
            Log.wtf("asd", "logMainTags: IOException", e2);
        }
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int r2) {

        throw new UnsupportedOperationException("Method not decompiled: videoplayer.maxplayer.maxplayer.data.MetadataItem.setOrientation(int):void");
    }

    private void setDateOriginal(Date dateOriginal) {
        this.dateOriginal = dateOriginal;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMake(String make) {
        this.make = make;
    }

    private void setModel(String model) {
        this.model = model;
    }

    private void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    private void setIso(String iso) {
        this.iso = iso;
    }


    private void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }
}
