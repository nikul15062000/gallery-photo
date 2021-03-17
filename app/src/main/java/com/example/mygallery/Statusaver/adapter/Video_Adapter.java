package com.example.mygallery.Statusaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.VideoitemActivity;
import com.example.mygallery.Statusaver.model.VideoModel;
import com.example.mygallery.photo.utils.pictureFacer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video_Adapter extends RecyclerView.Adapter<Video_Adapter.ViewHolder> {
   public ArrayList<VideoModel> arrayList;
    Context context;
    AdapterCallBackListener deleteinter;

    public interface AdapterCallBackListener {

        void onRowClick(ArrayList<VideoModel> imageList, int position);
    }

    public Video_Adapter(FragmentActivity activity, ArrayList<VideoModel> arrayList,AdapterCallBackListener deleteinter) {
        context=activity;
        this.arrayList=arrayList;
        this.deleteinter=deleteinter;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=  inflater.inflate(R.layout.video_list_save,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load("file://"+arrayList.get(position).getThumb()).into(holder.gridImage);

        holder.videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, VideoitemActivity.class);
                intent.putExtra("position",position);
                intent.putParcelableArrayListExtra("list",arrayList);
                context.startActivity(intent);
            }
        });
        holder.delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteinter.onRowClick(arrayList,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gridImage,videobtn,delet;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridImage=itemView.findViewById(R.id.gridImage);
            videobtn=itemView.findViewById(R.id.videobtn);
            delet=itemView.findViewById(R.id.delet);
        }
    }
}
