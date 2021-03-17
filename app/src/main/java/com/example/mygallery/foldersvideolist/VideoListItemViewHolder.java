package com.example.mygallery.foldersvideolist;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygallery.R;


public class VideoListItemViewHolder {
    int position;
    ImageView thumbImage;
    TextView txtSize;
    TextView txtTitle;
    TextView txtduration;
    ImageView menu;

    VideoListItemViewHolder(View convertView, int position) {
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        txtSize = (TextView) convertView.findViewById(R.id.txtSize);
        txtduration = (TextView) convertView.findViewById(R.id.txtduration);
        thumbImage = (ImageView) convertView.findViewById(R.id.imgIcon);
        menu = (ImageView) convertView.findViewById(R.id.menu);
        this.position = position;
    }
}
