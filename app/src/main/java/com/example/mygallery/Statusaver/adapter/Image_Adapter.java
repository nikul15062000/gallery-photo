package com.example.mygallery.Statusaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.FullimageActivity;
import com.example.mygallery.Statusaver.fragments.downloadImages;
import com.example.mygallery.photo.utils.pictureFacer;

import java.io.File;
import java.util.ArrayList;

import static androidx.core.view.ViewCompat.setTransitionName;
import static com.example.mygallery.videofile.NumericComparator.TAG;

public class Image_Adapter extends RecyclerView.Adapter<Image_Adapter.ViewHolder> {

    public  ArrayList<pictureFacer> imageList;
    private Context context;
    public static pictureFacer path;
    AdapterCallBackListener deleteinter;
   public static String filenameadapter;

    public interface AdapterCallBackListener {

        void onRowClick(ArrayList<pictureFacer> imageList, int position);
    }

    public Image_Adapter(ArrayList<pictureFacer> allpictures, Context context,AdapterCallBackListener deleteinter) {

        this.imageList=allpictures;
        this.context=context;
        this.deleteinter=deleteinter;
        Log.e(TAG, "Image_Adapter: ================="+imageList.size() );

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photo_pic_holder_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        pictureFacer image = imageList.get(position);

        String  pospath=imageList.get(position).getImageUri();
        File files =new File(pospath);
        filenameadapter=files.getName();



        Glide.with(context)
                .load(imageList.get(position).getImageUri())
                .apply(new RequestOptions().centerCrop())
                .into(holder.picture);

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                path=imageList.get(position);
                Intent i=new Intent(context, FullimageActivity.class);
                i.putExtra("path", position);
                i.putParcelableArrayListExtra("list",imageList);
                context.startActivity(i);

            }
        });
        holder.delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteinter.onRowClick(imageList,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture,delet;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture=itemView.findViewById(R.id.image);
            delet=itemView.findViewById(R.id.delet);
        }
    }
}
