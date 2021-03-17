package com.example.mygallery.videofile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.FolderListActivity;

import java.io.File;


public class SplashScreen extends SharedMediaActivity {
    public static final String ACTION_OPEN_ALBUM = "videoplayer.maxplayer.smartplayer.OPEN_ALBUM";
    static final int ALBUMS_BACKUP = 60;
    static final int ALBUMS_PREFETCHED = 23;
    static final String CONTENT = "content";
    static final int PHOTOS_PREFETCHED = 2;
    private static final int PICK_MEDIA_REQUEST = 44;
    static final String PICK_MODE = "pick_mode";
    static final String TAG = "SplashScreen";
    private boolean PICK_INTENT = false;
    private final int READ_EXTERNAL_STORAGE_ID = 12;
    private PreferenceUtil SP;
    private Album album;
    public  static Bundle b;

    private class PrefetchAlbumsData extends AsyncTask<Boolean, Boolean, Boolean> {
        private PrefetchAlbumsData() {
        }

        protected Boolean doInBackground(Boolean... arg0) {
            getAlbums().restoreBackup(getApplicationContext());
            if (getAlbums().dispAlbums.size() != 0) {
                return Boolean.valueOf(false);
            }
//            getAlbums().loadAlbums(getApplicationContext(), false);
            return Boolean.valueOf(true);
        }

        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent i = new Intent(SplashScreen.this, FolderListActivity.class);
                        b = new Bundle();
                        b.putInt("content", result.booleanValue() ? 23 : 60);
                        b.putBoolean(SplashScreen.PICK_MODE, SplashScreen.this.PICK_INTENT);
                        i.putExtras(b);
                        if (SplashScreen.this.PICK_INTENT) {
                           startActivityForResult(i, 44);
                        } else {
                            startActivity(i);
                            finish();
                        }
                        if (result.booleanValue()) {
                            getAlbums().saveBackup(SplashScreen.this.getApplicationContext());
                        }
                    }
                }
            }.start();


        }
    }

    private class PrefetchPhotosData extends AsyncTask<Void, Void, Void> {
        private PrefetchPhotosData() {
        }

        protected Void doInBackground(Void... arg0) {
            SplashScreen.this.album.updatePhotos(SplashScreen.this.getApplicationContext());
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
            Bundle b = new Bundle();
            SplashScreen.this.getAlbums().addAlbum(0, SplashScreen.this.album);
            b.putInt("content", 2);
            i.putExtras(b);
            startActivity(i);
            finish();
        }
    }

    @SuppressLint("ResourceAsColor")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_splash);
        this.SP = PreferenceUtil.getInstance(getApplicationContext());
        ((ProgressBar) findViewById(R.id.progress_splash)).getIndeterminateDrawable().setColorFilter( R.color.colorAccent, Mode.SRC_ATOP);
        getWindow().getDecorView().setSystemUiVisibility(1792);
        setNavBarColor();
        setStatusBarColor();
        if (PermissionUtils.isDeviceInfoGranted(this)&& PermissionUtils.isDeviceInfoGrantednextt(this)) {
            if (getIntent().getAction().equals(ACTION_OPEN_ALBUM)) {
                Bundle data = getIntent().getExtras();
                if (data != null) {
                    String ab = data.getString("albumPath");
                    if (ab != null) {
                        File dir = new File(ab);
                        this.album = new Album(getApplicationContext(), dir.getAbsolutePath(), (long) data.getInt("albumId", -1), dir.getName(), -1);
                        new PrefetchPhotosData().execute(new Void[0]);
                    }
                } else {
                    StringUtils.showToast(getApplicationContext(), "Album not found");
                }
            } else {
                new PrefetchAlbumsData().execute(new Boolean[0]);
            }
            if (getIntent().getAction().equals("android.intent.action.GET_CONTENT") || getIntent().getAction().equals("android.intent.action.PICK")) {
                this.PICK_INTENT = true;
                return;
            }

            return;
        }
        PermissionUtils.requestPermissions(this, 12, "android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE");
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 44 && resultCode == -1) {
            setResult(-1, data);
            finish();
        }

    }

    public void setNavBarColor() {
        if (VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ColorPalette.getTransparentColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    protected void setStatusBarColor() {
        if (VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                boolean granted;
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    granted = false;
                } else {
                    granted = true;
                }
                if (granted) {
                    new PrefetchAlbumsData().execute(new Boolean[]{Boolean.valueOf(this.SP.getBoolean(getString(R.string.preference_auto_update_media), false))});
                    return;
                }
                Toast.makeText(this, getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show();
                return;
            default:
                return;
        }

    }

}
