package com.example.mygallery.Statusaver.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.Video_Adapter;
import com.example.mygallery.Statusaver.model.VideoModel;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class downloadvideo extends Fragment {

    RecyclerView videoListView;
    Video_Adapter videoAdapter;
    ArrayList<VideoModel> videoList = new ArrayList();
    TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloadvideo, container, false);

        text = view.findViewById(R.id.text);
        videoListView = view.findViewById(R.id.videoListView);

        videoList.clear();


        updateSongList();

        if (videoList.size() == 0) {
            text.setVisibility(View.VISIBLE);
            videoListView.setVisibility(View.GONE);
        }

        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2);
        videoListView.setLayoutManager(manager);
        videoAdapter = new Video_Adapter(getActivity(), videoList, new Video_Adapter.AdapterCallBackListener() {
            @Override
            public void onRowClick(ArrayList<VideoModel> videoList, int position) {

                if (!videoList.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ArrayList<VideoModel> deletedFiles = new ArrayList<>();
                                    File file = new File(videoList.get(position).getThumb());
                                    if (file.exists()) {
                                        file.delete();
                                        deletedFiles.add(videoList.get(position));
                                        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                                    }

                                    for (VideoModel deletedFile : deletedFiles) {
                                        videoList.remove(deletedFile);
                                    }
                                    videoAdapter.notifyDataSetChanged();
                                }
                            })
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
            }
        });
        videoListView.setAdapter(videoAdapter);

        return view;
    }



    public void updateSongList() {
        File videoFiles = new File(Environment
                .getExternalStorageDirectory().toString() + File.separator + getResources().getString(R.string.app_name) + "/StatusSaverVideos");

        if (videoFiles.isDirectory()) {
            videoList = new ArrayList<>();
            if (videoFiles.isDirectory()) {
                File[] listFile = videoFiles.listFiles();
                Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                for (int i = 0; i < listFile.length; i++) {

                    videoList.add(new VideoModel(listFile[i].getAbsolutePath()));

                }
            }
        }

    }


}