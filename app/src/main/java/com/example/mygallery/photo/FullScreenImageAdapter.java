package com.example.mygallery.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.util.Utils;
import com.example.mygallery.photo.utils.pictureFacer;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

import static com.example.mygallery.photo.FullscreenActivity.layoutgone;
import static com.example.mygallery.photo.FullscreenActivity.mytoolbar;

public class FullScreenImageAdapter extends PagerAdapter {
    Context activity;
    ArrayList<ImageDetails> imageList;
    private File file;
    int i=0;

    public FullScreenImageAdapter(Context context, ArrayList<ImageDetails> pictureList) {
        this.activity = context;
        this.imageList = pictureList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.image_zoom_item, container, false);

        PhotoView imageView = itemView.findViewById(R.id.imageView);

        this.file = new File( imageList.get(position).getImagePath());
        if (!this.file.isDirectory()) {
            if (!Utils.getBack( imageList.get(position).getImagePath(), "((\\.jpg|\\.png|\\.gif|\\.jpeg|\\.bmp)$)").isEmpty()) {

                Glide.with(activity).load(file).into(imageView);

            }
        }
        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i == 0) {
                    layoutgone.setVisibility(View.GONE);
                    mytoolbar.setVisibility(View.GONE);
                    i = 1;
                } else {
                    layoutgone.setVisibility(View.VISIBLE);
                    mytoolbar.setVisibility(View.VISIBLE);
                    i = 0;
                }
            }
        });



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
