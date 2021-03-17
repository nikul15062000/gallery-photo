package com.example.mygallery.photo.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygallery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.photo.ImageDisplay;
import com.example.mygallery.photo.ImageFolderDetails;
import com.example.mygallery.photo.MainActivity;


import java.util.ArrayList;

public class pictureFolderAdapter extends RecyclerView.Adapter<pictureFolderAdapter.FolderHolder>{

    private ArrayList<ImageFolderDetails> folders;
    private Context folderContx;
    private static final String TAG = "pictureFolderAdapter";


    public pictureFolderAdapter(ArrayList<ImageFolderDetails> folders, Context folderContx) {
        this.folders = folders;
        this.folderContx = folderContx;

    }


    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View coll = inflater.inflate(R.layout.photo_picture_folder_item, parent, false);
        return new FolderHolder(coll);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        final ImageFolderDetails folder = folders.get(position);

        Glide.with(folderContx)
                .load(folder.getListImage().get(0).getImagePath())
                .apply(new RequestOptions().centerCrop())
                .into(holder.folderPic);

        String text = ""+folder.getFolderName();
        String folderSizeString=""+folder.getListImage().size()+" Images";
        holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);

        holder.folderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(folderContx, ImageDisplay.class);
                move.putExtra("list",folder.getListImage() );
                move.putExtra("folderName", folder.getFolderName());
                folderContx.startActivity(move);
//                listenToClick.onPicClicked(folder.getFolderName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class FolderHolder extends RecyclerView.ViewHolder{
        ImageView folderPic;
        TextView folderName;
        TextView folderSize;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
            folderSize=itemView.findViewById(R.id.folderSize);
        }
    }

}
