package com.example.mygallery.videofile;

import android.app.Application;



public class MyApplication extends Application {
    private HandlingAlbums albums = null;

    public Album getAlbum() {
        return this.albums.dispAlbums.size() > 0 ? this.albums.getCurrentAlbum() : Album.getEmptyAlbum();
    }

    public void onCreate() {
        this.albums = new HandlingAlbums(getApplicationContext());
        super.onCreate();

    }

    public HandlingAlbums getAlbums() {
        return this.albums;
    }

}
