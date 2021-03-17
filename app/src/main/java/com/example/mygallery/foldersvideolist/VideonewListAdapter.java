package com.example.mygallery.foldersvideolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideonewListAdapter extends RecyclerView.Adapter<VideonewListAdapter.ViewHolder> {
    Activity context;
    private LayoutInflater mInflater;
    ArrayList<MediaFile> videoList=new ArrayList<>();

    private static final String TAG = "VideoListAdapter";
    OnItemSelectedListener mListener;
    OnItemSelectedListener onItemdetail;
    OnItemSelectedListener onItemraname;
    OnItemSelectedListener onItemview;


    public VideonewListAdapter(VideoListActivity videoListActivity, ArrayList<MediaFile> videoList, OnItemSelectedListener onItemSelectedListener) {
        this.context = videoListActivity;
        this.videoList=videoList;
        mInflater = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mListener=onItemSelectedListener;
        onItemraname=onItemSelectedListener;
        onItemdetail=onItemSelectedListener;
        onItemview=onItemSelectedListener;

    }


    public interface OnItemSelectedListener {
        public void onItemSelected(View v,int position);
        public void onItemdetail(View v,int position);
        public void onItemraname(View v,int position);
        public void onItemView(View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= mInflater.inflate(R.layout.video_list_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MediaFile videoFile = (MediaFile) getItem(position);
        if (videoList.get(position).getThumbnailBitmap() == null) {

            Glide.with(this.context)
                    .load(videoList.get(position).getPath())
                    .transition(withCrossFade())
                    .centerCrop()
                    .apply(new RequestOptions().override(170, 100)
                            .placeholder(R.color.black)
                            .error(R.drawable.ic_folder).centerCrop()
                    )
                    .into(holder.thumbImage);
        }
        holder.txtTitle.setText(videoList.get(position).getFileName());
        holder.txtTitle.setSelected(true);
        holder.txtSize.setText(" Size: " + FileDataHelper.getFileSize(videoList.get(position).getSize()));
        holder.txtduration.setText(FileDataHelper.getFileDurationFormated(videoList.get(position).getDuration()));
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
                                    .getPackageName() + ".provider", new File(videoList.get(position).getPath()));
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemview.onItemView(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbImage;
        TextView txtSize;
        TextView txtTitle;
        TextView txtduration;
        ImageView menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtSize = (TextView) itemView.findViewById(R.id.txtSize);
            txtduration = (TextView) itemView.findViewById(R.id.txtduration);
            thumbImage = (ImageView) itemView.findViewById(R.id.imgIcon);
            menu = (ImageView) itemView.findViewById(R.id.menu);

        }
    }
}
