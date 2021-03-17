package com.example.mygallery.photo.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mygallery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.photo.FullscreenActivity;
import com.example.mygallery.photo.ImageDetails;

import java.util.ArrayList;

import static androidx.core.view.ViewCompat.setTransitionName;
import static com.example.mygallery.videofile.NumericComparator.TAG;

public class picture_Adapter extends RecyclerView.Adapter<picture_Adapter.PicHolder> {

    public static ArrayList<ImageDetails> pictureList;
    private Context pictureContx;
    public static ImageDetails path;

    public picture_Adapter(ArrayList<ImageDetails> pictureList, Context pictureContx) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;


    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View cell = inflater.inflate(R.layout.photo_pic_gallery_item, container, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {

        Glide.with(pictureContx)
                .load(pictureList.get(position).getImagePath())
                .apply(new RequestOptions().centerCrop())
                .into(holder.picture);

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                path=pictureList.get(position);
                Intent i=new Intent(pictureContx, FullscreenActivity.class);
                i.putExtra("path", String.valueOf(pictureList.get(position)));
                i.putExtra("position", position);
                pictureContx.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    public class PicHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public PicHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.image);
        }
    }
}
