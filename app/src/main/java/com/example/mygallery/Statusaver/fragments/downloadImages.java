package com.example.mygallery.Statusaver.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.Image_Adapter;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.photo.utils.MarginDecoration;
import com.example.mygallery.photo.utils.pictureFacer;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.mygallery.videofile.NumericComparator.TAG;


public class downloadImages extends Fragment {

    RecyclerView imageRecycler;
    public static ArrayList<pictureFacer> allpictures;
    TextView text;
    public static Image_Adapter image_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_download_images, container, false);
        text = view.findViewById(R.id.text);
        allpictures = new ArrayList<>();
        imageRecycler = view.findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(getActivity()));
        getFromSdcard();

        Log.e(TAG, "getAllImagesByFolder: main list " + allpictures.size());
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2);
        imageRecycler.setLayoutManager(manager);

        image_adapter = new Image_Adapter(allpictures, getContext(), new Image_Adapter.AdapterCallBackListener() {
            @Override
            public void onRowClick(ArrayList<pictureFacer> imageList, int position) {

                if (!imageList.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ArrayList<pictureFacer> deletedFiles = new ArrayList<>();
                                    File file = new File(imageList.get(position).getImageUri());
                                    if (file.exists()) {
                                        file.delete();
                                        deletedFiles.add(imageList.get(position));
                                        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                                    }
                                    for (pictureFacer deletedFile : deletedFiles) {
                                        allpictures.remove(deletedFile);
                                    }
                                    image_adapter.notifyDataSetChanged();
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
        imageRecycler.setAdapter(image_adapter);
        image_adapter.notifyDataSetChanged();

        if (allpictures.size() == 0) {
            text.setVisibility(View.VISIBLE);
            imageRecycler.setVisibility(View.GONE);
        }
        return view;
    }

    public void getFromSdcard() {
        File file = new File(
                Environment
                        .getExternalStorageDirectory().toString() + File.separator + getResources().getString(R.string.app_name) + "/StatusSaverImages");
        allpictures = new ArrayList<>();

        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {
                allpictures.add(new pictureFacer(listFile[i].getAbsolutePath()));
                Log.e(TAG, "getFromSdcard: =============" + allpictures.size());
            }
        }
    }


}