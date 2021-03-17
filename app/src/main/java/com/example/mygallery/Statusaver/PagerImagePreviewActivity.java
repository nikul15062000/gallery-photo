package com.example.mygallery.Statusaver;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;


import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.FullscreenImageAdapter;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.Statusaver.util.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class PagerImagePreviewActivity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<StatusModel> imageList;
    int position;
    Toolbar myToolbar;

    ImageView shareIV, deleteIV, wAppIV,download;
    LinearLayout downloadIV;
    FullscreenImageAdapter fullscreenImageAdapter;
    String statusdownload;
    ImageView backIV;
    TextView downtext,deletetext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saver_activity_pager_preview);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Photo Status");
        myToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(myToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);



        backIV = findViewById(R.id.backIV);
        viewPager = findViewById(R.id.viewPager);
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


        fullscreenImageAdapter = new FullscreenImageAdapter(PagerImagePreviewActivity.this, imageList);
        viewPager.setAdapter(fullscreenImageAdapter);
        viewPager.setCurrentItem(position);


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
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareIV:
                    if (imageList.size() > 0) {
                        if (isImageFile(imageList.get(viewPager.getCurrentItem()).getFilePath())) {
                            File imageFileToShare = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            share.setType("image/*");
                            Uri photoURI = FileProvider.getUriForFile(
                                    getApplicationContext(), getApplicationContext()
                                            .getPackageName() + ".provider", imageFileToShare);
                            share.putExtra(Intent.EXTRA_STREAM,
                                    photoURI);
                            startActivity(Intent.createChooser(share, "Share via"));

                        }
                    } else {
                        finish();
                    }
                    break;

                case R.id.download:
                    if (imageList.size() > 0) {
                        try {
                            download(imageList.get(viewPager.getCurrentItem()).getFilePath());
                        } catch (Exception e) {
                            Toast.makeText(PagerImagePreviewActivity.this, "Sorry we can't move file.try with other file.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        finish();
                    }
                    break;

                case R.id.deleteIV:
                    if (imageList.size() > 0) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PagerImagePreviewActivity.this);
                        alertDialog.setTitle("Confirm Delete....");
                        alertDialog.setMessage("Are you sure, You Want To Delete This Status?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                int currentItem = 0;

                                File file = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
                                if (file.exists()) {
                                    file.delete();

                                    if (imageList.size() > 0 && viewPager.getCurrentItem() < imageList.size()) {
                                        currentItem = viewPager.getCurrentItem();
                                    }

                                    imageList.remove(viewPager.getCurrentItem());
                                    fullscreenImageAdapter = new FullscreenImageAdapter(PagerImagePreviewActivity.this, imageList);
                                    viewPager.setAdapter(fullscreenImageAdapter);

                                    if (imageList.size() > 0) {
                                        viewPager.setCurrentItem(currentItem);
                                        imageList.remove(imageList.get(position).getFilePath());

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
                    if (isImageFile(imageList.get(viewPager.getCurrentItem()).getFilePath())) {
                        File imageFileToShare = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        share.setType("image/*");
                        share.setPackage("com.whatsapp");
                        Uri photoURI = FileProvider.getUriForFile(
                                getApplicationContext(), getApplicationContext()
                                        .getPackageName() + ".provider", imageFileToShare);
                        share.putExtra(Intent.EXTRA_STREAM,
                                photoURI);
                        startActivity(Intent.createChooser(share, "Share via"));

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
            Toast.makeText(PagerImagePreviewActivity.this, "Status is Saved Successfully", Toast.LENGTH_SHORT).show();
            download.setVisibility(View.GONE);
            downtext.setVisibility(View.GONE);
            deletetext.setVisibility(View.VISIBLE);
            deleteIV.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(PagerImagePreviewActivity.this, "Failed to Download", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }



    public boolean copyFileInSavedDir(String file) {
        try {
            if (isImageFile(file)) {
                FileUtils.copyFileToDirectory(new File(file), getDir("Images"));
                Utils.mediaScanner(PagerImagePreviewActivity.this, getDir("Images") + "/", file, "image/*");
            } else {
                FileUtils.copyFileToDirectory(new File(file), getDir("Videos"));
                Utils.mediaScanner(PagerImagePreviewActivity.this, getDir("Videos") + "/", file, "video/*");
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
