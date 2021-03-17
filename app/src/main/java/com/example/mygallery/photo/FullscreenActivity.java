package com.example.mygallery.photo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.photo.utils.picture_Adapter;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.mygallery.photo.utils.picture_Adapter.path;

public class FullscreenActivity extends AppCompatActivity {

    PhotoView fullimage;
    PhotoView image;
    ImageView  share, wallpaper, delete;
    private static final String TAG = "wallpaperActivity";
    public static Toolbar mytoolbar;
    public  static LinearLayout layoutgone;
    ImageDetails images;
    int i=0;
    TextView date;
    int dates;
    int position;
    FullScreenImageAdapter fullImageAdapter;
    ViewPager viewpage;
    LinearLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

//        fullimage = findViewById(R.id.fullimage);

        share = findViewById(R.id.share);
        wallpaper = findViewById(R.id.wallpaper);
        delete = findViewById(R.id.delete);
        image = findViewById(R.id.image);
        layoutgone = findViewById(R.id.layoutgone);
        viewpage = findViewById(R.id.viewpage);


        mytoolbar = findViewById(R.id.mytoolbar);
        mytoolbar.inflateMenu(R.menu.image_menu);
        setSupportActionBar(mytoolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        dates =  getIntent().getIntExtra("date",0);
        date=findViewById(R.id.date);

        images= path;
        position = getIntent().getIntExtra("position", 0);
        fullImageAdapter = new FullScreenImageAdapter(FullscreenActivity.this, picture_Adapter.pictureList);
        viewpage.setAdapter(fullImageAdapter);
        viewpage.setCurrentItem(position);




        Log.e(TAG, "onCreate: "+picture_Adapter.pictureList.get(viewpage.getCurrentItem()).getImagePath() );
        File file = new File(picture_Adapter.pictureList.get(viewpage.getCurrentItem()).getImagePath());
        Date lastModDate = new Date(file.lastModified());



        SimpleDateFormat timeStampFormat = new SimpleDateFormat("EEE dd-MMM-yyyy", Locale.US );
        Date GetDate = new Date(String.valueOf(lastModDate));
        String DateStr = timeStampFormat.format(GetDate);


        date.setText(""+DateStr);

//        Glide.with(this).load(images.getPicturePath()).into(fullimage);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFileToShare = new File(picture_Adapter.pictureList.get(viewpage.getCurrentItem()).getImagePath());
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
        });
        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = FileProvider.getUriForFile(
                        FullscreenActivity.this,
                        "com.example.mygallery.provider",
                       new File(picture_Adapter.pictureList.get(viewpage.getCurrentItem()).getImagePath()));
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType((imageUri), "image/*");
                intent.putExtra("mimeType", "image/*");
                startActivity(Intent.createChooser(intent, " Set as:"));

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picture_Adapter.pictureList.size() > 0) {
                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(FullscreenActivity.this);
                    alertDialog.setTitle("Confirm Delete....");
                    alertDialog.setMessage("Are you sure, You Want To Delete This Status?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            int currentItem = 0;

                            File file = new File(picture_Adapter.pictureList.get(viewpage.getCurrentItem()).getImagePath());
                            if (file.exists()) {
                                 file.delete();

                                if (picture_Adapter.pictureList.size() > 0 && viewpage.getCurrentItem() < picture_Adapter.pictureList.size()) {
                                    currentItem = viewpage.getCurrentItem();
                                }

                                picture_Adapter.pictureList.remove(viewpage.getCurrentItem());
                                fullImageAdapter = new FullScreenImageAdapter(FullscreenActivity.this, picture_Adapter.pictureList);
                                viewpage.setAdapter(fullImageAdapter);

                                if (picture_Adapter.pictureList.size() > 0) {
                                    viewpage.setCurrentItem(currentItem);
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

            }
        });

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;

            case R.id.asset:
                Drawable drawable = fullimage.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                WallpaperManager manager = WallpaperManager.getInstance(FullscreenActivity.this);
                try {
                    if (bitmap != null) {
                        manager.setBitmap(bitmap);
                        Toast.makeText(FullscreenActivity.this, "Wallapaper changed..", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.share:
                Drawable drawables = fullimage.getDrawable();
                Bitmap bitmaps = ((BitmapDrawable) drawables).getBitmap();
                String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmaps, "title", null);
                Uri uri = Uri.parse(bitmappath);
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "share image"));
                return true;

            case R.id.delet:
                Dialog delete_dia = new Dialog(FullscreenActivity.this);
                Window window = delete_dia.getWindow();
                delete_dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
                delete_dia.setContentView(R.layout.deletedialognew);
                delete_dia.setCanceledOnTouchOutside(true);
                delete_dia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView textView=(TextView)window.findViewById(R.id.txtdellll);
                RelativeLayout okk=(RelativeLayout)window.findViewById(R.id.ok_btn);
                RelativeLayout cancle=(RelativeLayout)window.findViewById(R.id.cancel_btn);
                textView.setText("Do you want to delete following Image?");
                okk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(images.getImagePath());
                        file.delete();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(images.getImagePath()))));
                        Toast.makeText(FullscreenActivity.this, "Delet this image...", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(FullscreenActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("back",1);
                        startActivity(intent);
                        finish();
                    }
                });
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_dia.dismiss();
                    }
                });
                delete_dia.show();

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(wallpaperActivity.this, FolderListActivity.class));
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);

        return true;
    }


}