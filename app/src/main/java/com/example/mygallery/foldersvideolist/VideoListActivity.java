package com.example.mygallery.foldersvideolist;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.videoplay.VideoViewActivity;

import java.io.File;
import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {
    String FolderName = "";

    boolean isWritePermissionGranted = false;
    Toolbar myToolbar;
    public static LinearLayout nativeAdContainer;
    public static LinearLayout adView;
    public RelativeLayout rl_google;
    Dialog dialog_filelist,reanme_dia,delete_dia;
    public static int posi;



    public static int resumePosition = 0;

    public static ArrayList<MediaFile> videoList = new ArrayList();

    public static VideoListAdapter videoListAdapter;

    public static VideonewListAdapter videonewListAdapter;

    public static RecyclerView videoListView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        setUpActionBar();
        SharedPreferences settings = getSharedPreferences("YOUR_PREF_NAME", 0);
        myToolbar.setTitle(FolderName);
        rl_google=(RelativeLayout)findViewById(R.id.rl_google);

        videoList = (ArrayList) getIntent().getSerializableExtra("FOLDER_ITEMS");

        FolderName = ((MediaFile) videoList.get(0)).getFolderName();
        videoListView = (RecyclerView) findViewById(R.id.PhoneVideoList);
        init_phone_video_grid();

        RecyclerView.LayoutManager manager=new GridLayoutManager(VideoListActivity.this,1);
        videoListView.setLayoutManager(manager);

    }

    public void setUpActionBar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void init_phone_video_grid() {
        if (videoList.size() <= 0) {
            setResult(-1);
            onBackPressed();
            return;
        }
        //R.layout.video_list_item,
        videonewListAdapter = new VideonewListAdapter(this,  videoList, new VideonewListAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View v, int position) {
                final MediaFile videoFile = (MediaFile) videoList.get(position);
                deletedialog(videoFile, position);
            }

            @Override
            public void onItemdetail(View v, int position) {
                final MediaFile videoFile = (MediaFile) videoList.get(position);
                propertydialog(v,videoFile, position);
            }

            @Override
            public void onItemraname(View v, int position) {
                final MediaFile videoFile = (MediaFile) videoList.get(position);
                renamedialognew(videoFile, position);

            }

            @Override
            public void onItemView(View view, int position) {
                posi=position;
                String pathhh=videoList.get(position).getPath();
                Intent intent = new Intent(VideoListActivity.this, VideoViewActivity.class);
                intent.putExtra("ITEM_POSITION", position);
                intent.putExtra("FOLDER_ITEMS", videoList);
                startActivityForResult(intent, 100);
            }
        });
        videoListView.setAdapter(videonewListAdapter);

    }

    public void propertydialog(View anchor, MediaFile videoFile, int position){
        dialog_filelist = new Dialog(VideoListActivity.this);
        Window window = dialog_filelist.getWindow();
        dialog_filelist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_filelist.setContentView(R.layout.filedetails_videolist);
        dialog_filelist.setCanceledOnTouchOutside(true);
        dialog_filelist.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView t_filename = (TextView) window.findViewById(R.id.t_showfilename);
        TextView t_loaction = (TextView) window.findViewById(R.id.t_showlocation);
        TextView t_size = (TextView) window.findViewById(R.id.t_showsize);
         TextView t_date = (TextView) window.findViewById(R.id.t_showdate);
        TextView t_resolution = (TextView) window.findViewById(R.id.t_showresolution);
        t_filename.setText(videoFile.getFileName());
        t_loaction.setText(videoFile.getPath());
        t_size.setText(FileDataHelper.getFileSize(videoFile.getSize()));
        t_date.setText(FileDataHelper.getFileDurationFormated(videoFile.getDuration()));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoFile.getPath());
        t_resolution.setText(metaRetriever.extractMetadata(18) + " x " + metaRetriever.extractMetadata(19));
        dialog_filelist.show();
    }

    public boolean isStoragePermissionGranted() {
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != 0) {
        }
    }

    public void renamedialognew(MediaFile videoFile, int position){
        reanme_dia = new Dialog(VideoListActivity.this);
        Window window = reanme_dia.getWindow();
        reanme_dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reanme_dia.setContentView(R.layout.renamedialog);
        reanme_dia.setCanceledOnTouchOutside(true);
        reanme_dia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final EditText edt_name=(EditText)window.findViewById(R.id.edt_text);
        RelativeLayout okk=(RelativeLayout) window.findViewById(R.id.ok_btn);
        RelativeLayout cancel=(RelativeLayout) window.findViewById(R.id.cancel_btn);
        final String currentFileName = videoFile.getFileName().substring(0, videoFile.getFileName().lastIndexOf(46));
        edt_name.setSelection(edt_name.getText().length());
        final MediaFile mediaFile = videoFile;
        final int i = position;
        okk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = Boolean.valueOf(false);
                String newFileName = edt_name.getText().toString().trim();
                File from = new File(mediaFile.getFolderPath(), mediaFile.getFileName());
                from.setWritable(true);
                File to = new File(mediaFile.getFolderPath(), mediaFile.getFileName().replace(currentFileName, newFileName));
                if (newFileName.isEmpty()) {
                    edt_name.setError("Invalid input");
                } else if (newFileName.equals(currentFileName)) {
                    wantToCloseDialog = Boolean.valueOf(true);
                } else if (to.exists()) {
                    edt_name.setError("File name already exists");
                } else if (from.renameTo(to)) {
                    wantToCloseDialog = Boolean.valueOf(true);
                    ((MediaFile) videoList.get(i)).setFileName(mediaFile.getFileName().replace(currentFileName, newFileName));
                    ((MediaFile) videoList.get(i)).setPath(mediaFile.getPath().replace(currentFileName, newFileName));
                    resumePosition = videoListView.getVerticalScrollbarPosition();
                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(from)));
                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(to)));
                    videoListAdapter.notifyDataSetChanged();
                    Toast.makeText(VideoListActivity.this, "Renamed file to: " + newFileName, Toast.LENGTH_SHORT).show();
                } else {
                    edt_name.setError("Invalid format");
                }
                if (wantToCloseDialog.booleanValue()) {
                    reanme_dia.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reanme_dia.dismiss();
            }
        });
        reanme_dia.show();
    }

    public void deletedialog(final MediaFile videoFile, final int position){
        delete_dia = new Dialog(VideoListActivity.this);
        Window window = delete_dia.getWindow();
        delete_dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delete_dia.setContentView(R.layout.deletedialognew);
        delete_dia.setCanceledOnTouchOutside(true);
        delete_dia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView textView=(TextView)window.findViewById(R.id.txtdellll);
        RelativeLayout okk=(RelativeLayout)window.findViewById(R.id.ok_btn);
        RelativeLayout cancle=(RelativeLayout)window.findViewById(R.id.cancel_btn);
         textView.setText("Do you want to delete following video?");
        okk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resumePosition = videoListView.getVerticalScrollbarPosition();
                if (getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, "_data=?", new String[]{videoFile.getPath()}) > 0) {
                    videoList.remove(position);
                    videoListAdapter.notifyDataSetChanged();
                    Toast.makeText(VideoListActivity.this, "Deleted: " + videoFile.getFileName(), Toast.LENGTH_SHORT).show();
                }
                delete_dia.dismiss();
            }
        });
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_dia.dismiss();
            }
        });
        delete_dia.show();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            moveTaskToBack(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume() {
        super.onResume();
        this.myToolbar.setTitle(this.FolderName);
    }

    protected void onPause() {
        super.onPause();
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
