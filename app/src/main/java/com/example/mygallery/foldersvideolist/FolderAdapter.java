package com.example.mygallery.foldersvideolist;

import android.app.Activity;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;

import java.io.File;
import java.util.ArrayList;

import static com.example.mygallery.foldersvideolist.VideoListActivity.videoListAdapter;

public class FolderAdapter extends ArrayAdapter<MediaFolder> {
    Activity context;
    ArrayList<ArrayList<MediaFile>> mediaFiles = new ArrayList();
    String type;
    public static ArrayList<MediaFolder> videoList = new ArrayList();
    Dialog dialogg, dialog_filelist, reanme_dia, delete_dia;
    ItemClickListener itemClickListener ,onItemClickdetail,onItemClickrename;

//    public static int AD_COUNT = 0;

    public interface ItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemClickdetail(View view, int position);
        public void onItemClickrename(View view, int position);

    }

    static class ViewHolder {
        TextView folderName;
        TextView videos;
        ImageView menu;

        public ViewHolder(View view) {
            folderName = (TextView) view.findViewById(R.id.folder_name);
            videos = (TextView) view.findViewById(R.id.no_of_videos);
            menu = (ImageView) view.findViewById(R.id.menu);
        }
    }

    public FolderAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<MediaFolder> objects, String type, ArrayList<ArrayList<MediaFile>> mediaFiles, ItemClickListener itemClickListener ) {
        super(context, resource, objects);
        this.type = type;
        this.context = context;
        this.mediaFiles = mediaFiles;
        this.itemClickListener = itemClickListener;
        this.onItemClickdetail = itemClickListener;
        this.onItemClickrename = itemClickListener;

    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.tempfolderlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        MediaFolder mediaFolder = (MediaFolder) getItem(position);
        viewHolder.folderName.setText(mediaFolder.getDisplayName());
        String videoString = " " + this.type;
        if (mediaFolder.getNumberOfMediaFiles() > 1) {
            videoString = videoString + "s";
        }


//        if (AD_COUNT % 3 == 0) {
//
////show ad
//
//        }else{
//
////not show
//
//        }
//
//        AD_COUNT++;


        int poss = getPosition(getItem(position));
        viewHolder.videos.setText(mediaFolder.getNumberOfMediaFiles() + videoString);
        viewHolder.menu.setTag(getItem(position));


        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.menu);
                popupMenu.getMenuInflater().inflate(R.menu.video_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i = menuItem.getItemId();
                        if (i == R.id.delete) {
                            int pos = position;
                            itemClickListener.onItemClick(view, position);
                            return true;
                        } else if (i == R.id.details) {
                            onItemClickdetail.onItemClickdetail(view, position);
                            return true;
                        } else if (i == R.id.rename) {
                            onItemClickrename.onItemClickrename(view,position);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }

        });


        return view;
    }


}
