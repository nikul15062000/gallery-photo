package com.example.mygallery.Statusaver.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.model.VideoModel;
import com.example.mygallery.Statusaver.util.Utils;
import com.example.mygallery.photo.utils.pictureFacer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FullImageAdapter extends PagerAdapter {
    Context activity;
    ArrayList<pictureFacer> imageList;
    private File file;


    public FullImageAdapter( Context context, ArrayList<pictureFacer> imageList) {
        this.activity = context;
        this.imageList = imageList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.image_layout, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);



        this.file = new File( imageList.get(position).getImageUri());
        if (!this.file.isDirectory()) {
             if (!Utils.getBack( imageList.get(position).getImageUri(), "((\\.jpg|\\.png|\\.gif|\\.jpeg|\\.bmp)$)").isEmpty()) {

                Glide.with(this.activity).load(this.file).into(imageView);
            }
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }
}
