package com.example.mygallery.photo.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mygallery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;


public class recyclerViewPagerImageIndicator extends RecyclerView.Adapter<indicatorHolder> {

    ArrayList<pictureFacer> pictureList;
    Context pictureContx;
    private final imageIndicatorListener imageListerner;
    public static Bitmap bitmap;
    public static String path,path1,path2;


    public recyclerViewPagerImageIndicator(ArrayList<pictureFacer> pictureList, Context pictureContx, imageIndicatorListener imageListerner) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.imageListerner = imageListerner;
    }


    @NonNull
    @Override
    public indicatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.photo_indicator_holder, parent, false);
        return new indicatorHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull indicatorHolder holder, final int position) {

        final pictureFacer pic = pictureList.get(position);

        holder.positionController.setBackgroundColor(pic.getSelected() ? Color.parseColor("#00000000") : Color.parseColor("#8c000000"));

        Glide.with(pictureContx)
                .load(pic.getPicturePath())
                .apply(new RequestOptions().centerCrop())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pic.setSelected(true);
                notifyDataSetChanged();
//                imageListerner.onImageIndicatorClicked(position);
//                Intent intent=new Intent(pictureContx, wallpaperActivity.class);
//                pictureContx.startActivity(intent);
                path=pic.getPicturePath();


            }
        });
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
