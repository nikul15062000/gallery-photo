package com.example.mygallery.Statusaver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.FullImageAdapter;
import com.example.mygallery.Statusaver.adapter.FullscreenImageAdapter;
import com.example.mygallery.Statusaver.adapter.Image_Adapter;
import com.example.mygallery.Statusaver.adapter.Video_Adapter;
import com.example.mygallery.Statusaver.fragments.downloadImages;
import com.example.mygallery.Statusaver.model.StatusModel;
import com.example.mygallery.Statusaver.model.VideoModel;
import com.example.mygallery.photo.utils.pictureFacer;
import com.example.mygallery.videofile.Media;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class FullimageActivity extends AppCompatActivity {

    ViewPager viewpage;
    ArrayList<pictureFacer> imageList = new ArrayList<>();
    int position;
    FullImageAdapter fullImageAdapter;
    Toolbar myToolbar;
    ImageView shareIV, deleteIV, wAppIV;


    private static final String TAG = "FullimageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Download Status");
        myToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(myToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        viewpage = findViewById(R.id.viewpage);

        shareIV = findViewById(R.id.shareIV);
        deleteIV = findViewById(R.id.deleteIV);
        wAppIV = findViewById(R.id.wAppIV);


        ///-------------------------

//            imageList= getIntent().getParcelableArrayListE
//            xtra("images");

        imageList = getIntent().getParcelableArrayListExtra("list");


        Log.e("TAG", "onCreate: ===============>>>>>>>>>" + imageList.size());
        position = getIntent().getIntExtra("path", 0);


        fullImageAdapter = new FullImageAdapter(FullimageActivity.this, imageList);
        viewpage.setAdapter(fullImageAdapter);
        viewpage.setCurrentItem(position);


        shareIV.setOnClickListener(clickListener);
        deleteIV.setOnClickListener(clickListener);
        wAppIV.setOnClickListener(clickListener);


    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareIV:
                    if (imageList.size() > 0) {
                        if (isImageFile(imageList.get(viewpage.getCurrentItem()).getImageUri())) {
                            File imageFileToShare = new File(imageList.get(viewpage.getCurrentItem()).getImageUri());
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

                case R.id.deleteIV:
                    if (!imageList.isEmpty()) {
                        new AlertDialog.Builder(FullimageActivity.this)
                                .setMessage("Are you sure , You want to delete selected files?")
                                .setCancelable(true)
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ArrayList<pictureFacer> deletedFiles = new ArrayList<>();


                                            File file = new File(imageList.get(position).getImageUri());
                                            if (file.exists()) {
                                                file.delete();
                                                Toast.makeText(FullimageActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(FullimageActivity.this, DownloadActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(FullimageActivity.this, "Couldn't delete some files", Toast.LENGTH_SHORT).show();
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


                    break;

                case R.id.wAppIV:
                    if (isImageFile(imageList.get(viewpage.getCurrentItem()).getImageUri())) {
                        File imageFileToShare = new File(imageList.get(viewpage.getCurrentItem()).getImageUri());
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

    public boolean delet(Context context, File fdelete) {

        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                fdelete.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri fileUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(fileUri, where, selectionArgs);
        if (fdelete.exists()) {
            contentResolver.delete(fileUri, where, selectionArgs);
        }

        return !fdelete.exists();
    }


    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
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


    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }
}