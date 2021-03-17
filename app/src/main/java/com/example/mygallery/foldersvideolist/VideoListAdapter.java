package com.example.mygallery.foldersvideolist;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mygallery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoListAdapter extends ArrayAdapter<MediaFile> {
    Activity context;
    private LayoutInflater mInflater;

    private static final String TAG = "VideoListAdapter";
    OnItemSelectedListener mListener,onItemdetail,onItemraname;


    public interface OnItemSelectedListener {
        public void onItemSelected(View v,int position);
        public void onItemdetail(View v,int position);
        public void onItemraname(View v,int position);
    }

    public VideoListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<MediaFile> objects,OnItemSelectedListener itemlistnar) {
        super(context, resource, objects);
        this.context = context;
        mInflater = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mListener=itemlistnar;
        onItemraname=itemlistnar;
        onItemdetail=itemlistnar;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        VideoListItemViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.video_list_item, parent, false);
            holder = new VideoListItemViewHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            holder = (VideoListItemViewHolder) convertView.getTag();
            holder.position = position;
        }
        MediaFile videoFile = (MediaFile) getItem(position);
        if (videoFile.getThumbnailBitmap() == null) {

            Glide.with(this.context)
                    .load(videoFile.getPath())
                    .transition(withCrossFade())
                    .centerCrop()
                    .apply(new RequestOptions().override(170, 100)
                            .placeholder(R.color.black)
                            .error(R.drawable.ic_folder).centerCrop()
                    )
                    .into(holder.thumbImage);
        }
        holder.txtTitle.setText(videoFile.getFileName());
        holder.txtTitle.setSelected(true);
        holder.txtSize.setText(" Size: " + FileDataHelper.getFileSize(videoFile.getSize()));
        holder.txtduration.setText(FileDataHelper.getFileDurationFormated(videoFile.getDuration()));
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu =new PopupMenu(context,holder.menu);
                popupMenu.getMenuInflater().inflate(R.menu.video_share_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i = menuItem.getItemId();
                        if (i == R.id.delete) {
                            mListener.onItemSelected(view,position);
                            return true;
                        } else if (i == R.id.details) {
                            mListener.onItemdetail(view,position);
                            return true;
                        } else if (i == R.id.rename) {
                             mListener.onItemraname(view,position);
                            return true;
                        }
                        else if (i == R.id.share) {
                            Uri videoURI = FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext()
                                    .getPackageName() + ".provider", new File(videoFile.getPath()));
                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("*/*");
                            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);
                            context.startActivity(videoshare);

                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        return convertView;
    }
}
