package com.example.mygallery.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mygallery.R;
import com.example.mygallery.utility.AppList;
import com.bumptech.glide.Glide;

import java.util.List;


public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {

    private List<AppList> moviesList;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView img;

        public MyViewHolder(View view) {
            super(view);
            textViewName= (TextView) view.findViewById(R.id.textViewName);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }
    public AppListAdapter(Context context, List<AppList> moviesList) {
        this.moviesList = moviesList;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_layou_dash, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String str = moviesList.get(position).getIcon();
        Glide.with(context)
                .load(moviesList.get(position).getIcon())
                .into(holder.img);
        holder.textViewName.setText(moviesList.get(position).getName());
        holder.textViewName.setSelected(true);
    }
    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
