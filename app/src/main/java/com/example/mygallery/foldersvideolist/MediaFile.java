package com.example.mygallery.foldersvideolist;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MediaFile implements Serializable {
    private String album;
    private String artist;
    private int duration;
    private String fileName;
    private String folderName;
    private String folderPath;
    private int id;
    private String musicInfoString;
    private String path;
    private int resumePosition;
    private long size;
    private Bitmap thumbnailBitmap;

    public MediaFile() {
        setId(0);
        setFileName("");
        setPath("");
        setFolderPath("");
        setFolderName("");
        setArtist("");
        setAlbum("");
        setSize(0);
        setDuration(0);
        setResumePosition(0);
        setThumbnailBitmap(null);
        setMusicInfoString(null);
    }


    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getResumePosition() {
        return this.resumePosition;
    }

    public void setResumePosition(int resumePosition) {
        this.resumePosition = resumePosition;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Bitmap getThumbnailBitmap() {
        return this.thumbnailBitmap;
    }

    public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
        this.thumbnailBitmap = thumbnailBitmap;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setMusicInfoString(String musicInfoString) {
        this.musicInfoString = musicInfoString;
    }
}
