package com.example.mygallery.videofile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class SharedMediaActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public HandlingAlbums getAlbums() {
        return ((MyApplication) getApplicationContext()).getAlbums();
    }

    public Album getAlbum() {
        return ((MyApplication) getApplicationContext()).getAlbum();
    }
}
