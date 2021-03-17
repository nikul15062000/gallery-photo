package com.example.mygallery.Statusaver.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.mygallery.Statusaver.PagerVideoPreviewActivity;
import com.example.mygallery.Statusaver.model.StatusModel;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.example.mygallery.Statusaver.util.Utils.download;


public class RecVideoAdapter extends BaseAdapter {

    Fragment context;
     public static  List<StatusModel> arrayList;
    int width;
    LayoutInflater inflater;
    RecWappAdapter.OnItemClickListener listener;

    public static String pathpos;

    public interface OnItemClickListener {
        void onItemClick(int arg0, List<StatusModel> arrayList);
    }




    public RecVideoAdapter(Fragment context, List<StatusModel> arrayList, RecWappAdapter.OnItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener=listener;

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
//        if (isVideoFile(arrayList.get(arg0).getFilePath())) {
//            play.setVisibility(View.VISIBLE);
//        } else {
//            play.setVisibility(View.GONE);
//        }

        String name1=getFileNameFromPath(arrayList.get(arg0).getFilePath());

        File file = new File(
                Environment
                        .getExternalStorageDirectory().toString() + File.separator + context.getResources().getString(R.string.app_name) + "/StatusSaverVideos/"+name1);

        if (file.exists()){
            download.setVisibility(View.GONE);
            delet.setVisibility(View.VISIBLE);
        }else {
            download.setVisibility(View.VISIBLE);
            delet.setVisibility(View.GONE);
        }

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

                pathpos = arrayList.get(arg0).getFilePath();

                Intent intent = new Intent(context.getActivity(), PagerVideoPreviewActivity.class);
                intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) arrayList);
                intent.putExtra("position", arg0);
                intent.putExtra("statusdownload", "");
                context.startActivityForResult(intent, 10);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri videoURI = FileProvider.getUriForFile(context.getContext().getApplicationContext(), context.getContext().getApplicationContext()
                        .getPackageName() + ".provider", new File(arrayList.get(arg0).getFilePath()));
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.setType("*/*");
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);
                context.startActivity(videoshare);

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  path = arrayList.get(arg0).getFilePath();
                getFileNameFromPath(path);

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

                }
                else{
                    Toast.makeText(context.getContext(), "Couldn't Saved Some Files", Toast.LENGTH_SHORT).show();

                }
            }
        });
        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(arg0,arrayList);

            }
        });

        return grid;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
    }



    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    public static String getFileNameFromPath(String path) {

        return path.substring(path.lastIndexOf("/") + 1);
    }
}

