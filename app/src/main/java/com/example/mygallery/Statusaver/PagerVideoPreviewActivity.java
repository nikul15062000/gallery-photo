package com.example.mygallery.Statusaver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.FullscreenVideoPlayAdapter;
import com.example.mygallery.Statusaver.adapter.RecVideoAdapter;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.Statusaver.util.Utils;
import com.example.mygallery.videoview.CustomVideoView;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.example.mygallery.Statusaver.adapter.RecVideoAdapter.pathpos;


public class PagerVideoPreviewActivity extends AppCompatActivity {

    VideoView videoview;
    ArrayList<StatusModel> imageList;
    int position;
    Toolbar myToolbar;

    ImageView shareIV, deleteIV, wAppIV,download;
    LinearLayout downloadIV;
    FullscreenVideoPlayAdapter fullscreenImageAdapter;
    String statusdownload;
    ImageView backIV;
    TextView downtext,deletetext;
    private static final String TAG = "PagerVideoPreviewActivi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_video_preview);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Video Status");
        myToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(myToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);


        backIV = findViewById(R.id.backIV);
        videoview = findViewById(R.id.videoview);
        shareIV = findViewById(R.id.shareIV);
        downloadIV = findViewById(R.id.downloadIV);
        wAppIV = findViewById(R.id.wAppIV);
        deleteIV = findViewById(R.id.deleteIV);
        download = findViewById(R.id.download);
        downtext = findViewById(R.id.downtext);
        deletetext = findViewById(R.id.deletetext);

        imageList = getIntent().getParcelableArrayListExtra("images");
        position = getIntent().getIntExtra("position", 0);
        statusdownload = getIntent().getStringExtra("statusdownload");

//        fullscreenImageAdapter = new FullscreenVideoPlayAdapter(PagerVideoPreviewActivity.this, imageList);
//        viewPager.setAdapter(fullscreenImageAdapter);
//        viewPager.setCurrentItem(position);
//
        Log.e(TAG, "onCreate: ===============>>>>>>>>>>>>"+pathpos );






        //For now we just picked an arbitrary item to play


        Log.e(TAG, "onCreate: ===============>>videopart>>>>>>>>>>"+imageList.get(position).getFilePath() );


        shareIV.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
        deleteIV.setOnClickListener(clickListener);
        wAppIV.setOnClickListener(clickListener);






    }
    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoview.setVideoPath(pathpos);
        videoview.start();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareIV:
                    if (imageList.size() > 0) {
                        if (isVideoFile(imageList.get(position).getFilePath())) {

                            Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                    .getPackageName() + ".provider", new File(imageList.get(position).getFilePath()));
                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("*/*");
                            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);
                            startActivity(videoshare);

                        }
                    } else {
                        finish();
                    }
                    break;

                case R.id.download:
                    if (imageList.size() > 0) {
                        try {

                            download(imageList.get(position).getFilePath());

                        } catch (Exception e) {

                            Toast.makeText(PagerVideoPreviewActivity.this, "Sorry we can't move file.try with other file.", Toast.LENGTH_LONG).show();

                        }
                    } else {

                        finish();

                    }
                    break;

                case R.id.deleteIV:
                    if (imageList.size() > 0) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PagerVideoPreviewActivity.this);
                        alertDialog.setTitle("Confirm Delete....");
                        alertDialog.setMessage("Are you sure, You Want To Delete This Status?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                File file = new File(imageList.get(position).getFilePath());
                                if (file.exists()) {
                                   file.delete();
                                    if (imageList.size() > 0) {
                                        imageList.remove(position);
                                        onBackPressed();
                                    } else {
                                        finish();
                                    }
                                }
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.show();
                    } else {
                        finish();
                    }
                    break;

                case R.id.wAppIV:

                     if (isVideoFile(imageList.get(position).getFilePath())) {
                        Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                .getPackageName() + ".provider", new File(imageList.get(position).getFilePath()));
                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("*/*");
                        videoshare.setPackage("com.whatsapp");
                        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);
                        startActivity(videoshare);

                    }



                    break;

                default:
                    break;
            }
        }
    };



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void download(String path) {
        if (copyFileInSavedDir(path)) {
            Toast.makeText(PagerVideoPreviewActivity.this, "Status is Saved Successfully", Toast.LENGTH_SHORT).show();
            download.setVisibility(View.GONE);
            downtext.setVisibility(View.GONE);
            deletetext.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(PagerVideoPreviewActivity.this, "Failed to Download", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


    public boolean copyFileInSavedDir(String file) {
        try {
            if (isImageFile(file)) {
                FileUtils.copyFileToDirectory(new File(file), getDir("Images"));
                Utils.mediaScanner(PagerVideoPreviewActivity.this, getDir("Images") + "/", file, "image/*");
            } else {
                FileUtils.copyFileToDirectory(new File(file), getDir("Videos"));
                Utils.mediaScanner(PagerVideoPreviewActivity.this, getDir("Videos") + "/", file, "video/*");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getDir(String folder) {

        File rootFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + getResources().getString(R.string.app_name) + File.separator + folder);
        rootFile.mkdirs();

        return rootFile;

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.download:
//                if (imageList.size() > 0) {
//                    try {
//                        download(imageList.get(viewPager.getCurrentItem()).getFilePath());
//                    } catch (Exception e) {
//                        Toast.makeText(PagerPreviewActivity.this, "Sorry we can't move file.try with other file.", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    finish();
//                }
//
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_download, menu);
//
//        return true;
//    }
}