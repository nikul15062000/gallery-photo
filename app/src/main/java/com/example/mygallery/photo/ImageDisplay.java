package com.example.mygallery.photo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mygallery.R;
import com.example.mygallery.photo.fragments.pictureBrowserFragment;
import com.example.mygallery.photo.utils.MarginDecoration;
import com.example.mygallery.photo.utils.PicHolder;
import com.example.mygallery.photo.utils.itemClickListener;
import com.example.mygallery.photo.utils.pictureFacer;
import com.example.mygallery.photo.utils.picture_Adapter;
import com.example.mygallery.videofile.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


public class ImageDisplay extends AppCompatActivity  {

    RecyclerView imageRecycler;
    ArrayList<ImageDetails> allpictures;
    ProgressBar load;
    String foldePath;
    TextView folderName;
    Toolbar toolbar;

    private static final String TAG = "ImageDisplay";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity_image_display);

        String foldername= (String) getIntent().getSerializableExtra("folderName");
        
        toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle(foldername);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        foldePath =  getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if(allpictures.isEmpty()){
            load.setVisibility(View.VISIBLE);
            allpictures= (ArrayList<ImageDetails>) getIntent().getSerializableExtra("list");
//            allpictures = getAllImagesByFolder(foldePath);
            Log.e(TAG, "onCreatealladapterpicture================: "+allpictures );
            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this));
            RecyclerView.LayoutManager manager=new GridLayoutManager(ImageDisplay.this,3);
            imageRecycler.setLayoutManager(manager);
            load.setVisibility(View.GONE);
        }
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }






}
