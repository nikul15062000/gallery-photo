package com.example.mygallery.Statusaver.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.Statusaver.PagerImagePreviewActivity;
import com.example.mygallery.Statusaver.model.StatusModel;

import java.io.File;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.example.mygallery.Statusaver.adapter.Image_Adapter.filenameadapter;

import static com.example.mygallery.Statusaver.fragments.RecentImages.f;
import static com.example.mygallery.Statusaver.fragments.RecentImages.myAdapter;
import static com.example.mygallery.Statusaver.util.Utils.download;


public class RecWappAdapter extends BaseAdapter {

    Fragment context;
    List<StatusModel> arrayList;
    int width;
    LayoutInflater inflater;

    OnItemClickListener listener;



    private static final String TAG = "RecWappAdapter";

    public interface OnItemClickListener {
        void onItemClick(int arg0, List<StatusModel> arrayList);
    }


    public RecWappAdapter(Fragment context, List<StatusModel> arrayList, OnItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;

        inflater = (LayoutInflater) context.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        width = displayMetrics.widthPixels;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        View grid = inflater.inflate(R.layout.saver_ins_row_status, null);

//        ImageView play = grid.findViewById(R.id.play);
        ImageView share = grid.findViewById(R.id.share);
        ImageView download = grid.findViewById(R.id.download);
        ImageView delet = grid.findViewById(R.id.delet);


        String name1=getFileNameFromPath(arrayList.get(arg0).getFilePath());
        File file = new File(
                Environment
                        .getExternalStorageDirectory().toString() + File.separator + context.getResources().getString(R.string.app_name) + "/StatusSaverImages/"+name1);

       if (file.exists()){
           download.setVisibility(View.GONE);
           delet.setVisibility(View.VISIBLE);
       }else {
           download.setVisibility(View.VISIBLE);
           delet.setVisibility(View.GONE);
       }



//        if (isVideoFile(arrayList.get(arg0).getFilePath())) {
//            play.setVisibility(View.VISIBLE);
//        } else {
//            play.setVisibility(View.GONE);
//        }


        grid.setLayoutParams(new GridView.LayoutParams((width * 460 / 1080),
                (width * 460 / 1080)));
        ImageView imageView = (ImageView) grid
                .findViewById(R.id.gridImage);


        Glide.with(context.getActivity()).load(arrayList.get(arg0).getFilePath()).into(imageView);

        CheckBox checkbox = grid.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((StatusModel) arrayList.get(arg0)).setSelected(isChecked);

            }
        });
        if (arrayList.get(arg0).isSelected()) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }

        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "click");
                Intent intent = new Intent(context.getActivity(), PagerImagePreviewActivity.class);
                intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) arrayList);
                intent.putExtra("position", arg0);
                intent.putExtra("statusdownload", "");
                context.startActivityForResult(intent, 10);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imageFileToShare = new File(arrayList.get(arg0).getFilePath());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setType("image/*");
                Uri photoURI = FileProvider.getUriForFile(
                        context.getContext().getApplicationContext(), context.getContext().getApplicationContext()
                                .getPackageName() + ".provider", imageFileToShare);
                share.putExtra(Intent.EXTRA_STREAM,
                        photoURI);
                context.startActivity(Intent.createChooser(share, "Share via"));
            }
        });




        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  path = arrayList.get(arg0).getFilePath();

                getFileNameFromPath(path);


                Uri.fromFile(new File(path));
                if (!arrayList.isEmpty()) {
                    int success = -1;
                    ArrayList<StatusModel> deletedFiles = new ArrayList<>();
                    File file = new File(arrayList.get(arg0).getFilePath());
                    if (file.exists()) {
                        if (download(context.getContext(), arrayList.get(arg0).getFilePath())) {
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

                    if (success == 0) {
                        Toast.makeText(context.getContext(), "Couldn't Saved Some Files", Toast.LENGTH_SHORT).show();
                    } else if (success == 1) {
                        Toast.makeText(context.getContext(), "All Status Are Saved Successfully", Toast.LENGTH_SHORT).show();

                        download.setVisibility(View.GONE);
                        delet.setVisibility(View.VISIBLE);

                    }

                } else {
                    Toast.makeText(context.getContext(), "Couldn't Saved Some Files", Toast.LENGTH_SHORT).show();

                }
            }
        });





        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!arrayList.isEmpty()) {
                    new AlertDialog.Builder(context.getContext())
                            .setMessage("Are you sure , You want to delete selected files?")
                            .setCancelable(true)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int success = -1;
                                    ArrayList<StatusModel> deletedFiles = new ArrayList<>();
                                    File file = new File(arrayList.get(arg0).getFilePath());
                                    if (file.exists()) {
                                        file.delete();
                                        deletedFiles.add(arrayList.get(arg0));
                                        Toast.makeText(context.getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context.getContext(), "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                                    }

                                    arrayList.clear();
                                    for (StatusModel deletedFile : deletedFiles) {
                                        f.remove(deletedFile);
                                    }
                                    myAdapter.notifyDataSetChanged();

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

        return grid;
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getFileNameFromPath(String path) {

        return path.substring(path.lastIndexOf("/") + 1);
    }
}
