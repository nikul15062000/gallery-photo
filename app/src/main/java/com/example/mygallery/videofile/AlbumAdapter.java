package com.example.mygallery.videofile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygallery.R;

import java.util.ArrayList;


public class AlbumAdapter extends Adapter<AlbumAdapter.ViewHolder> {
    private ArrayList<Album> albums;
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private BitmapDrawable placeholder;
    int txtval;
    boolean longclick;


    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        View layout;
        TextView nPhotos;
        TextView name;
        ImageView picture;
        ImageView selectedIcon;

        ViewHolder(View itemView) {
            super(itemView);
            this.picture = (ImageView) itemView.findViewById(R.id.album_preview);
            this.selectedIcon = (ImageView) itemView.findViewById(R.id.selected_icon);
            this.name = (TextView) itemView.findViewById(R.id.album_name);
            this.nPhotos = (TextView) itemView.findViewById(R.id.album_photos_count);
        }
    }

    public AlbumAdapter(ArrayList<Album> ph, int txtval, boolean longclick, Context context) {
        this.albums = ph;
        this.txtval=txtval;
        this.longclick=longclick;
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_album, parent, false);
        v.setOnClickListener(this.mOnClickListener);
        v.setOnLongClickListener(this.mOnLongClickListener);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Album a = (Album) this.albums.get(position);
        holder.name.setTag(a);
        if (VERSION.SDK_INT >= 24) {
            holder.name.setText(a.getName());
            holder.nPhotos.setText("" + a.getCount() + " Video");
            return;
        }
        holder.name.setText(a.getName());
        holder.nPhotos.setText("" + a.getCount() + " Video");
        if(DashboardActivity.longcheck){
            if(position==DashboardActivity.textval){
                holder.nPhotos.setTextColor(Color.BLUE);
            }else{
                holder.nPhotos.setTextColor(Color.BLACK);
            }

        }

    }


    public void setOnClickListener(OnClickListener lis) {
        this.mOnClickListener = lis;
    }

    public void setOnLongClickListener(OnLongClickListener lis) {
        this.mOnLongClickListener = lis;
    }

    public void swapDataSet(ArrayList<Album> asd) {
        if (!this.albums.equals(asd)) {
            this.albums = asd;
            notifyDataSetChanged();
        }
    }

    public int getItemCount() {
        return this.albums.size();
    }

    public void setFilter(ArrayList<Album> dispAlbums) {
        this.albums = new ArrayList();
        this.albums.addAll(dispAlbums);
        notifyDataSetChanged();
    }
}
