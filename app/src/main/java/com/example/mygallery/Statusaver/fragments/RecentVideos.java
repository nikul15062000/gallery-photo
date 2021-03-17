package com.example.mygallery.Statusaver.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.example.mygallery.R;
import com.example.mygallery.Statusaver.DownloadActivity;
import com.example.mygallery.Statusaver.RecStatusActivity;
import com.example.mygallery.Statusaver.adapter.RecVideoAdapter;
import com.example.mygallery.Statusaver.adapter.RecWappAdapter;
import com.example.mygallery.Statusaver.model.StatusModel;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.mygallery.Statusaver.util.Utils.download;

public class RecentVideos extends Fragment {

    GridView imagegrid;
    ArrayList<StatusModel> f = new ArrayList<>();
    RecVideoAdapter myAdapter;
    ArrayList<StatusModel> filesToDelete = new ArrayList<>();
    LinearLayout actionLay;
    RelativeLayout downloadIV, deleteIV;
    TextView medianotfound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.saver_sta_photos, container, false);

        medianotfound = rootView.findViewById(R.id.medianotfound);
        imagegrid = (GridView) rootView.findViewById(R.id.WorkImageGrid);
        f.clear();

        populateGrid();

        if (f.size()==0) {
            medianotfound.setVisibility(View.VISIBLE);
            imagegrid.setVisibility(View.GONE);
        }

        actionLay = rootView.findViewById(R.id.actionLay);
        deleteIV = rootView.findViewById(R.id.deleteIV);
        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!filesToDelete.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int success = -1;
                                    ArrayList<StatusModel> deletedFiles = new ArrayList<>();

                                    for (StatusModel details : filesToDelete) {
                                        File file = new File(details.getFilePath());
                                        if (file.exists()) {
                                            if (file.delete()) {
                                                deletedFiles.add(details);
                                                if (success == 0) {
                                                    return;
                                                }
                                                success = 1;
                                            } else {
                                                success = 0;
                                            }
                                        } else {
                                            success = 0;
                                        }
                                    }

                                    filesToDelete.clear();
                                    for (StatusModel deletedFile : deletedFiles) {
                                        f.remove(deletedFile);
                                    }
                                    myAdapter.notifyDataSetChanged();
                                    if (success == 0) {
                                        Toast.makeText(getContext(), "Couldn't Delete Some Files", Toast.LENGTH_SHORT).show();
                                    } else if (success == 1) {
                                        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    actionLay.setVisibility(View.GONE);
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

        downloadIV = rootView.findViewById(R.id.downloadIV);
        downloadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!filesToDelete.isEmpty()) {

                    int success = -1;
                    ArrayList<StatusModel> deletedFiles = new ArrayList<>();

                    for (StatusModel details : filesToDelete) {
                        File file = new File(details.getFilePath());
                        if (file.exists()) {
                            if (download(getActivity(), details.getFilePath())) {
                                deletedFiles.add(details);
                                if (success == 0) {
                                    return;
                                }
                                success = 1;
                            } else {
                                success = 0;
                            }
                        } else {
                            success = 0;
                        }
                    }
                    filesToDelete.clear();
                    for (StatusModel deletedFile : deletedFiles) {
                        f.contains(deletedFile.selected = false);
                    }
                    myAdapter.notifyDataSetChanged();
                    if (success == 0) {
                        Toast.makeText(getContext(), "Couldn't Saved Some Files", Toast.LENGTH_SHORT).show();
                    } else if (success == 1) {
                        Toast.makeText(getActivity(), "All Status Are Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                    actionLay.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        myAdapter.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 10 && resultCode == 10) {
//            myAdapter.notifyDataSetChanged();
//
//            populateGrid();
//            actionLay.setVisibility(View.GONE);
//        }
//    }

    public void populateGrid() {
        File file =new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + ".Statuses");
        getVideo(file);
        myAdapter = new RecVideoAdapter(this, f, new RecWappAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int arg0, List<StatusModel> arrayList) {
                if (!arrayList.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int success = -1;
                                    ArrayList<StatusModel> deletedFiles = new ArrayList<>();

                                    File file = new File(arrayList.get(arg0).getFilePath());
                                    if (file.exists()) {
                                        if (file.delete()) {
                                            deletedFiles.add(arrayList.get(arg0));
                                            if (success == 0) {
                                                return;
                                            }
                                            success = 1;
                                        } else {
                                            success = 0;
                                        }
                                    } else {
                                        success = 0;
                                    }

									arrayList.clear();
                                    for (StatusModel deletedFile : deletedFiles) {
                                        f.remove(deletedFile);
                                    }
                                    myAdapter.notifyDataSetChanged();
                                    if (success == 0) {
                                        Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                                    } else if (success == 1) {
                                        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
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
        imagegrid.setAdapter(myAdapter);

    }

    public void getVideo(File dir) {
        ArrayList<File> fileList = new ArrayList<File>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {

                    fileList.add(listFile[i]);
                    getVideo(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".mp4")) {
                        fileList.clear();
                        fileList.add(listFile[i]);
                        f.add(new StatusModel(String.valueOf(listFile[i])));
                    }
                }
            }
        }
    }

}
