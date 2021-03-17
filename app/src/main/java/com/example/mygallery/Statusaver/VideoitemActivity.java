package com.example.mygallery.Statusaver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.adapter.FullscreenVideoAdapter;
import com.example.mygallery.Statusaver.adapter.RecVideoAdapter;
import com.example.mygallery.Statusaver.adapter.Video_Adapter;
import com.example.mygallery.Statusaver.fragments.downloadvideo;
import com.example.mygallery.Statusaver.model.VideoModel;
import com.example.mygallery.photo.utils.pictureFacer;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.example.mygallery.Statusaver.adapter.RecVideoAdapter.pathpos;

public class VideoitemActivity extends AppCompatActivity {
    VideoView videoview;
    ViewPager viewpage;
    ArrayList<VideoModel> imageList = new ArrayList<>();
    int position;
    FullscreenVideoAdapter fullscreenvideoAdapter;
    Toolbar myToolbar;
    ImageView shareIV, deleteIV, wAppIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoitem);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Download Status");
        myToolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(myToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

//        viewpage=findViewById(R.id.viewpage);
        videoview = findViewById(R.id.videoview);
        shareIV = findViewById(R.id.shareIV);
        deleteIV = findViewById(R.id.deleteIV);
        wAppIV = findViewById(R.id.wAppIV);

        imageList = getIntent().getParcelableArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);




//        fullscreenvideoAdapter = new FullscreenVideoAdapter(VideoitemActivity.this, imageList);
//        viewpage.setAdapter(fullscreenvideoAdapter);
//        viewpage.setCurrentItem(position);


        shareIV.setOnClickListener(clickListener);
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
        videoview.setVideoPath(imageList.get(position).getThumb());
        videoview.start();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareIV:
                    if (imageList.size() > 0) {
                        if (isVideoFile(imageList.get(position).getThumb())) {

                            Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                    .getPackageName() + ".provider", new File(imageList.get(position).getThumb()));
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

                case R.id.deleteIV:

                    if (!imageList.isEmpty()) {
                        new android.app.AlertDialog.Builder(VideoitemActivity.this)
                                .setMessage("Are you sure , You want to delete selected files?")
                                .setCancelable(true)
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        ArrayList<VideoModel> deletedFiles = new ArrayList<>();
                                        File file = new File(imageList.get(position).getThumb());
                                        if (file.exists()) {

                                            file.delete();
                                            Toast.makeText(VideoitemActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(VideoitemActivity.this, DownloadActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(VideoitemActivity.this, "Couldn't delete some files", Toast.LENGTH_SHORT).show();

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

                    if (isVideoFile(imageList.get(position).getThumb())) {
                        Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                .getPackageName() + ".provider", new File(imageList.get(position).getThumb()));
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


    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
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
}