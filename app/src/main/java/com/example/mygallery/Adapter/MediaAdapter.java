package com.example.mygallery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
/*import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView.Adapter;*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygallery.videofile.Count;
import com.example.mygallery.videofile.Media;
import com.example.mygallery.R;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private BitmapDrawable drawable;
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private ArrayList<Media> medias;

    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        View gifIcon;
        ImageView icon;
        ImageView imageView,img_start_paly;
        View layout;
        TextView tvDuration;
        TextView tvVideoName;

        ViewHolder(View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.media_card_layout);
            this.imageView = (ImageView) itemView.findViewById(R.id.photo_preview);
            this.img_start_paly = (ImageView) itemView.findViewById(R.id.img_start_paly);
            this.gifIcon = itemView.findViewById(R.id.gif_icon);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);
            this.tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
        }
    }

    public MediaAdapter(ArrayList<Media> ph, Context context) {
        this.medias = ph;

    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_photo, parent, false);
        v.setOnClickListener(this.mOnClickListener);
        v.setOnLongClickListener(this.mOnLongClickListener);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Media f = (Media) this.medias.get(position);
        holder.tvVideoName.setTag(f);
        holder.tvDuration.setTag(f);
        holder.icon.setVisibility(View.GONE);
        if (f.isGif()) {
            (Glide.with(holder.imageView.getContext()).load(f.getPath())).into(holder.imageView);
            holder.gifIcon.setVisibility(View.VISIBLE);
        } else {
            Glide.with(holder.imageView.getContext())
                    .asBitmap()
                    .load(f.getUri())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .thumbnail(0.5f)
                    .placeholder(this.drawable)
                    .transition(GenericTransitionOptions.with(R.anim.fade_in))
                    .into(holder.imageView);


            holder.gifIcon.setVisibility(View.GONE);
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(holder.tvDuration.getContext(), Uri.fromFile(f.getFile()));
        } catch (Exception e) {

        }
        int kolicina = 0;
        try {
            kolicina = Integer.parseInt(retriever.extractMetadata(9));
        } catch (NumberFormatException e) {

        }

        if (f.isVideo()) {
            holder.icon.setVisibility(View.VISIBLE);
            holder.tvVideoName.setVisibility(View.VISIBLE);
            holder.tvVideoName.setText(f.getName());
            holder.tvVideoName.setTextColor(ContextCompat.getColor(holder.tvVideoName.getContext(), R.color.textPrimary));

            holder.tvDuration.setText("" + Count.toFormattedTime(kolicina));
        } else {
            holder.icon.setVisibility(View.GONE);
            holder.tvVideoName.setVisibility(View.GONE);
        }
        if (f.isSelected()) {
            holder.icon.setImageResource(R.drawable.ic_done);
            holder.img_start_paly.setVisibility(View.GONE);
            holder.icon.setVisibility(View.VISIBLE);
            holder.imageView.setColorFilter(-2013265920, Mode.SRC_ATOP);
            holder.layout.setPadding(15, 15, 15, 15);
            return;
        }
        holder.imageView.clearColorFilter();
        holder.icon.setVisibility(View.INVISIBLE);
        holder.img_start_paly.setVisibility(View.VISIBLE);
        holder.layout.setPadding(0, 0, 0, 0);
    }

    public int getItemCount() {
        return this.medias.size();
    }

    public void setOnClickListener(OnClickListener lis) {
        this.mOnClickListener = lis;
    }

    public void setOnLongClickListener(OnLongClickListener lis) {
        this.mOnLongClickListener = lis;
    }

    public void swapDataSet(ArrayList<Media> asd) {
        this.medias = asd;
        notifyDataSetChanged();
    }
}
