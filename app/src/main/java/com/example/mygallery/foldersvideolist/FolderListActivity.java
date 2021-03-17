package com.example.mygallery.foldersvideolist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video.Media;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.PhotoGalleryActivity;
import com.example.mygallery.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FolderListActivity extends AppCompatActivity {
    int count;
    String filename;
    FolderAdapter folderAdapter;
    RecyclerView folderListView;
    String folderType;

    private Cursor mediaCursor;
    ArrayList<ArrayList<MediaFile>> mediaFiles = new ArrayList();
    ArrayList<MediaFolder> mediaFolders;
    public static LinearLayout nativeAdContainer;
    public RelativeLayout rel_lay_appid;
    Dialog dialogg, dialog_filelist, reanme_dia, delete_dia;
    Toolbar myToolbar;
    RelativeLayout rl_bttom_photos, rl_bttom_video;

    String PICK_MODE = "video";

    FoldernewAdapter foldernewAdapter;

    int resumePosition = 0;
    int snowDensity;
    public RelativeLayout rl_google;
    private RecyclerView recycle_app_list;
    String[] thumbColumns = new String[]{"_data", "video_id"};
    private int video_column_index;
    LinearLayout video, photo;
    int i = 0;

    public static int Item_Per_Ad=2;

//    public static final int NUMBER_OF_ADS = 5;
//    private AdLoader adLoader;
//    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
//    private List<Object> mRecyclerViewItems = new ArrayList<>();


   public static int num=1;
    int value = 0;
    public static com.google.android.gms.ads.InterstitialAd mInterstitialAd;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        setUpActionBar();
        showgoogleFull();
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));


        i = getIntent().getIntExtra("back", 0);
        rl_google = findViewById(R.id.rl_google);
        rel_lay_appid = findViewById(R.id.recycle_appid_layout);
        recycle_app_list = findViewById(R.id.recycle_app_list);
        video = findViewById(R.id.video);

        snowDensity = getSharedPreferences("YOUR_PREF_NAME", 0).getInt("SNOW_DENSITY", 0);

        folderType = PICK_MODE;
        folderListView = (RecyclerView) findViewById(R.id.folderListView);

        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 1);
        folderListView.setLayoutManager(manager);

//        loadNativeAds();
    }


    public void renamedialognew(MediaFolder mediaFolder, int position) {
        reanme_dia = new Dialog(FolderListActivity.this);
        final EditText edt_name = (EditText) findViewById(R.id.edt_text);
        RelativeLayout okk = (RelativeLayout) findViewById(R.id.ok_btn);
        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel_btn);
        final String folderInFolderPath = mediaFolder.getPath().substring(0, mediaFolder.getPath().lastIndexOf("/"));
        edt_name.setSelection(edt_name.getText().length());
        final MediaFolder mediaFolder2 = mediaFolder;
        final int i = position;
        okk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = Boolean.valueOf(false);
                String newFileName = edt_name.getText().toString().trim();
                File from = new File(folderInFolderPath, mediaFolder2.getDisplayName());
                from.setWritable(true);
                File to = new File(folderInFolderPath, newFileName);
                if (newFileName.isEmpty()) {
                    edt_name.setError("Invalid input");
                } else if (newFileName.equals(mediaFolder2.getDisplayName())) {
                    wantToCloseDialog = Boolean.valueOf(true);
                } else if (to.exists()) {
                    edt_name.setError("File name already exists");
                } else if (from.renameTo(to)) {
                    wantToCloseDialog = Boolean.valueOf(true);
                    resumePosition = folderListView.getVerticalScrollbarPosition();
                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(from)));
                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(to)));
                    mediaFolder2.setPath(folderInFolderPath + newFileName);
                    mediaFolder2.setDisplayName(newFileName);
                    Iterator it = ((ArrayList) mediaFiles.get(i)).iterator();
                    while (it.hasNext()) {
                        MediaFile mediaFile = (MediaFile) it.next();
                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(mediaFile.getPath())));
                        mediaFile.setPath(mediaFolder2.getPath() + mediaFile.getFileName());
                        mediaFile.setFolderPath(mediaFolder2.getPath());
                        mediaFile.setFolderName(newFileName);
                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(mediaFile.getPath())));
                    }
                    foldernewAdapter.notifyDataSetChanged();
                    Toast.makeText(FolderListActivity.this, "Renamed folder to: " + newFileName, Toast.LENGTH_SHORT).show();
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

    public void deletedialog(final MediaFolder mediaFolder, final int position) {
        delete_dia = new Dialog(FolderListActivity.this);
        Window window = delete_dia.getWindow();
        delete_dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delete_dia.setContentView(R.layout.deletedialognew);
        delete_dia.setCanceledOnTouchOutside(true);
        delete_dia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView textView = (TextView) window.findViewById(R.id.txtdellll);
        RelativeLayout okk = (RelativeLayout) window.findViewById(R.id.ok_btn);
        RelativeLayout cancle = (RelativeLayout) window.findViewById(R.id.cancel_btn);
        okk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resumePosition = folderListView.getVerticalScrollbarPosition();
                int result = 0;
                Iterator it = ((ArrayList) mediaFiles.get(position)).iterator();
                while (it.hasNext()) {
                    MediaFile mediaFile = (MediaFile) it.next();
                    result = getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, "_data=?", new String[]{mediaFile.getPath()});
                }
                if (result > 0) {
                    mediaFiles.remove(position);
                    mediaFolders.remove(position);
                    foldernewAdapter.notifyDataSetChanged();
                    Toast.makeText(FolderListActivity.this, "Deleted All " + folderType + " in: " + mediaFolder.getDisplayName(), Toast.LENGTH_SHORT).show();
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != 0) {
        }
    }

    public void propertydialog(MediaFolder mediaFolder, int position) {
        dialog_filelist = new Dialog(FolderListActivity.this);
        Window window = dialog_filelist.getWindow();
        dialog_filelist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_filelist.setContentView(R.layout.propertydialog);
        dialog_filelist.setCanceledOnTouchOutside(true);
        dialog_filelist.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView t_filename = (TextView) window.findViewById(R.id.t_showfilename);
        TextView t_loaction = (TextView) window.findViewById(R.id.t_showlocation);
        TextView t_size = (TextView) window.findViewById(R.id.t_showsize);
        TextView t_resolution = (TextView) window.findViewById(R.id.t_showresolution);
        t_filename.setText(mediaFolder.getDisplayName());
        t_loaction.setText(mediaFolder.getPath());
        t_size.setText(FileDataHelper.getFileSize(mediaFolder.getMediaSize()));
        t_resolution.setText("" + mediaFolder.getNumberOfMediaFiles());
        dialog_filelist.show();

    }

    public boolean isStoragePermissionGranted() {
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setUpActionBar() {
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Folder Name");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void init_phone_music_grid() {
        System.gc();
        mediaCursor = managedQuery(Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "_display_name", "_size", "duration"}, null, null, null);
        count = mediaCursor.getCount();
        setMediaFolderData();
    }

    private void init_phone_video_grid() {
        System.gc();
        mediaCursor = managedQuery(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "_display_name", "_size", "duration"}, null, null, null);
        count = mediaCursor.getCount();
        setMediaFolderData();

        for (int j = 0; j < mediaFolders.size(); j++) {
            if (j%5==0){
                mediaFolders.add(j,null);
            }
        }

    }

    private void setMediaFolderData() {
        mediaFiles = new ArrayList();
        mediaFolders = new ArrayList();
        if (mediaCursor != null && count > 0) {
            int i = 0;
            while (mediaCursor.moveToNext()) {
                String data;
                String fileName;
                MediaFolder mediaFolder = new MediaFolder();
                MediaFile mediaFile = new MediaFile();
                if (folderType.contains("video")) {
                    data = mediaCursor.getString(mediaCursor.getColumnIndex("_data"));
                    fileName = mediaCursor.getString(mediaCursor.getColumnIndex("_display_name"));
                    mediaFile.setId(mediaCursor.getInt(mediaCursor.getColumnIndex("_id")));
                    mediaFile.setSize(mediaCursor.getLong(mediaCursor.getColumnIndex("_size")));
                    mediaFile.setDuration(mediaCursor.getInt(mediaCursor.getColumnIndex("duration")));
                } else {
                    data = mediaCursor.getString(mediaCursor.getColumnIndex("_data"));
                    fileName = mediaCursor.getString(mediaCursor.getColumnIndex("_display_name"));
                    mediaFile.setId(mediaCursor.getInt(mediaCursor.getColumnIndex("_id")));
                    mediaFile.setSize(mediaCursor.getLong(mediaCursor.getColumnIndex("_size")));
                    mediaFile.setDuration(mediaCursor.getInt(mediaCursor.getColumnIndex("duration")));
                }
                mediaFile.setFileName(fileName);
                mediaFile.setPath(data);
                mediaFolder.setPath(data.replace("/" + fileName, ""));
                mediaFile.setFolderPath(mediaFolder.getPath());
                mediaFolder.setDisplayName(mediaFolder.getPath().substring(mediaFolder.getPath().lastIndexOf("/") + 1));
                mediaFile.setFolderName(mediaFolder.getDisplayName());
                boolean check = false;
                int j = 0;
                Iterator it = mediaFolders.iterator();
                while (it.hasNext()) {
                    MediaFolder media_Folder = (MediaFolder) it.next();
                    if (media_Folder.getPath().equals(mediaFolder.getPath())) {
                        media_Folder.setNumberOfMediaFiles(media_Folder.getNumberOfMediaFiles() + 1);
                        media_Folder.setMediaSize(media_Folder.getMediaSize() + mediaFile.getSize());
                        media_Folder.setMediaDuration(media_Folder.getMediaDuration() + mediaFile.getDuration());
                        check = true;
                        ((ArrayList) mediaFiles.get(j)).add(mediaFile);
                        break;
                    }
                    j++;
                }
                if (!check) {
                    mediaFiles.add(new ArrayList());
                    ((ArrayList) mediaFiles.get(i)).add(mediaFile);
                    i++;
                    mediaFolder.setMediaSize(mediaFolder.getMediaSize() + mediaFile.getSize());
                    mediaFolder.setMediaDuration(mediaFolder.getMediaDuration() + mediaFile.getDuration());
                    mediaFolders.add(mediaFolder);
                }
            }
        }
        if (mediaFolders == null || mediaFolders.size() <= 0) {
            folderListView.setVisibility(View.GONE);
            findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
            return;
        }
        findViewById(R.id.empty_message).setVisibility(View.GONE);
        folderListView.setVisibility(View.VISIBLE);
        //R.layout.folder_list_item,
        foldernewAdapter = new FoldernewAdapter(this,  mediaFolders, folderType, mediaFiles, new FoldernewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final MediaFolder mediaFolder = (MediaFolder) mediaFolders.get(position);
                deletedialog(mediaFolder, i);
            }

            @Override
            public void onItemClickdetail(View view, int position) {
                 MediaFolder mediaFolder = mediaFolders.get(position);
                 propertydialog(mediaFolder, i);
            }

            @Override
            public void onItemClickrename(View view, int position) {
                MediaFolder mediaFolder = mediaFolders.get(position);
                renamedialognew(mediaFolder, i);
            }

            @Override
            public void onFolderItemclick(View view, int position) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gallery-93648-default-rtdb.firebaseio.com/");
                DatabaseReference myRef = database.getReference("numbar");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                         value = dataSnapshot.getValue(Integer.class);

                    }
                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {
                        Log.e("TAG", "Failed to read value.", error.toException());
                    }
                });



                MediaFolder mediaFolder = (MediaFolder) mediaFolders.get(position);
                Intent i = new Intent(FolderListActivity.this, VideoListActivity.class);
                i.putExtra("FOLDER_NAME", mediaFolder.getDisplayName());
                i.putExtra("FOLDER_PATH", mediaFolder.getPath());
                i.putExtra("FOLDER_ITEMS", (Serializable) mediaFiles.get(0));
                startActivityForResult(i, 1002);

                if (value==num){
                    num=0;

                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }

                }else {
                    num++;
                }

            }
        });
        folderListView.setAdapter(foldernewAdapter);

//        if (resumePosition > 0 && resumePosition < mediaFolders.size()) {
//            folderListView.setSelection(resumePosition);
//        }

//        folderListView.setOnItemClickListener(onItemClickListener);


    }
    private void showgoogleFull() {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getString(R.string.Interstialads));
        final AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            public void onAdLoaded() {
                //                mInterstitialAd.show();
                Log.e("MyLog", "getload " );
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.e("MyLog", "gerror " + errorCode);
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {

            }

            @Override
            public void onAdClosed() {
                final AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
            }

        });
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folders_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_network_stream:
                Toast.makeText(getApplicationContext(), "Network Stream", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_view:
                Toast.makeText(getApplicationContext(), "View", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(FolderListActivity.this, PhotoGalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        finish();
    }


    protected void onResume() {
        super.onResume();
        initListView();
    }

    public void initListView() {
        if (folderType.contains("video")) {
            myToolbar.setTitle("Video Folders");
            init_phone_video_grid();
            return;
        }
        myToolbar.setTitle("Audio Folders");
        init_phone_music_grid();
    }

    protected void onPause() {
        super.onPause();
        resumePosition = folderListView.getVerticalScrollbarPosition();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == -1) {
            resumePosition = folderListView.getVerticalScrollbarPosition();
            if (folderType.contains("video")) {
                init_phone_video_grid();
            }
        }
    }




}
