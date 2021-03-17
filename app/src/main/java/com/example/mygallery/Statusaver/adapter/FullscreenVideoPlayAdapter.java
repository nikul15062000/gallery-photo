package com.example.mygallery.Statusaver.adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.PagerVideoPreviewActivity;
import com.example.mygallery.Statusaver.adapter.FullscreenImageAdapter;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.Statusaver.util.Utils;


import java.io.File;
import java.util.ArrayList;

import static com.example.mygallery.videofile.NumericComparator.TAG;

public class FullscreenVideoPlayAdapter extends PagerAdapter {

    Activity activity;
    ArrayList<StatusModel> imageList;
    private File file;
    VideoView video;

    public FullscreenVideoPlayAdapter(PagerVideoPreviewActivity pagerVideoPreviewActivity, ArrayList<StatusModel> imageList) {
        this.activity = pagerVideoPreviewActivity;
        this.imageList = imageList;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.previewvideo_list_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView iconplayer = itemView.findViewById(R.id.iconplayer);
        video = itemView.findViewById(R.id.video);

        this.file = new File(imageList.get(position).getFilePath());
        if (!this.file.isDirectory()) {
            if (!Utils.getBack(imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                try {

                    Log.e(TAG, "instantiateItem: =================>>>>>>>>>" + imageList.get(position).getFilePath());



                    video.setVideoURI(Uri.parse(imageList.get(position).getFilePath()));

                    video.start();



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!Utils.getBack( imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
//                    com.example.mygallery.Statusaver.fragments.Utils.mPath = imageList.get(position).getFilePath();
//                    activity.startActivity(new Intent(activity, VideoPreviewActivity.class));
//                }
//            }
//        });
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
