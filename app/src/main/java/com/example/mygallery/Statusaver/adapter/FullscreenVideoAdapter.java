package com.example.mygallery.Statusaver.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.VideoPreviewActivity;
import com.example.mygallery.Statusaver.VideoitemActivity;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.Statusaver.model.VideoModel;
import com.example.mygallery.Statusaver.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class FullscreenVideoAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<VideoModel> imageList;
    private File file;

    public FullscreenVideoAdapter(VideoitemActivity videoitemActivity, ArrayList<VideoModel> imageList) {
        this.activity = videoitemActivity;
        this.imageList = imageList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.saver_preview_list_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView iconplayer = itemView.findViewById(R.id.iconplayer);

        this.file = new File( imageList.get(position).getPath());
        if (!this.file.isDirectory()) {
            if (!Utils.getBack( imageList.get(position).getPath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                try {
                    iconplayer.setVisibility(View.VISIBLE);
                    Glide.with(this.activity).load(this.file).into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.getBack( imageList.get(position).getPath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                    com.example.mygallery.Statusaver.fragments.Utils.mPath = imageList.get(position).getPath();
                    activity.startActivity(new Intent(activity, VideoPreviewActivity.class));
                }
            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return  imageList.size();
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
