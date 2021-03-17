package com.example.mygallery.videofile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
/*import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;*/
import android.app.AlertDialog;
import androidx.cardview.widget.CardView;
/*import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;*/
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/*import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SwitchCompat;*/
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.Adapter.MediaAdapter;
import com.example.mygallery.Affix.Affix;
import com.example.mygallery.Affix.SecurityHelper;
import com.example.mygallery.R;
import com.example.mygallery.utility.NetworkStatus;
import com.example.mygallery.utility.Util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends SharedMediaActivity implements SearchView.OnQueryTextListener {
    private static String TAG = "AlbumsAct";
    private int REQUEST_CODE_SD_CARD_PERMISSIONS = 42;
    private PreferenceUtil SP;
    Activity activity;
    public RelativeLayout rel_lay_appid;
    private  RecyclerView recycle_app_list;
    public static boolean longcheck=false;
    public  static   int textval=0;
    public RelativeLayout rl_google;


    private boolean menudeletecheck = false;
    private ImageView tool_back_icon, tool_delete_icon,tool_more_icon;
    private TextView tv_selctive, tool_text_title;
    private String adPlacementId;
    private OnClickListener albumOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            DashboardActivity dashboardActivity = DashboardActivity.this;
            dashboardActivity.count++;
            if (DashboardActivity.this.count == 6) {
                DashboardActivity.this.count = 0;
            }
            Album album = (Album) v.findViewById(R.id.album_name).getTag();
            if (DashboardActivity.this.editMode) {
                DashboardActivity.this.albumsAdapter.notifyItemChanged(DashboardActivity.this.getAlbums().toggleSelectAlbum(album));
                DashboardActivity.this.invalidateOptionsMenu();
                return;
            }
            DashboardActivity.this.getAlbums().setCurrentAlbum(album);
            Show_Data.albumname = DashboardActivity.this.getAlbums().getCurrentAlbum().getName();
            DashboardActivity.this.displayCurrentAlbumMedia(true);
            //  DashboardActivity.this.setRecentApp(DashboardActivity.this.getAlbums().getCurrentAlbum().getName());
        }
    };
    private OnLongClickListener albumOnLongCLickListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {

            longcheck=true;
            DashboardActivity.this.albumsAdapter.notifyItemChanged(DashboardActivity.this.getAlbums().toggleSelectAlbum((Album) v.findViewById(R.id.album_name).getTag()));
            DashboardActivity.this.editMode = true;
            invalidateOptionsMenu();
            return true;
        }
    };
    private AlbumAdapter albumsAdapter;
    private boolean albumsMode = true;
    private SelectAlbumBottomSheet bottomSheetDialogFragment;
    int count = 0;
    private CustomAlbumsHelper customAlbumsHelper = CustomAlbumsHelper.getInstance(this);
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;
    private boolean editMode = false;
    private boolean firstLaunch = true;
    private boolean hidden = false;
    //    private InterstitialAd interstitial;
//    private NativeAdsManager manager;
    private MediaAdapter mediaAdapter;
    //    private NativeAdScrollView nativeAdScrollView;
    private OnClickListener photosOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            //FirebaseCrash.logcat(4, DashboardActivity.TAG, "Dashboard VideoShow");
            Media m = (Media) v.findViewById(R.id.tvVideoName).getTag();
            if (DashboardActivity.this.pickMode) {
                DashboardActivity.this.setResult(-1, new Intent().setData(m.getUri()));
                DashboardActivity.this.finish();
            } else if (DashboardActivity.this.editMode) {
                DashboardActivity.this.mediaAdapter.notifyItemChanged(DashboardActivity.this.getAlbum().toggleSelectPhoto(m));
                DashboardActivity.this.invalidateOptionsMenu();
            } else {
                DashboardActivity.this.getAlbum().setCurrentPhotoIndex(m);
                Intent intent = new Intent(DashboardActivity.this, VideoPlayingActivity.class);
                intent.putExtra("songpostion", DashboardActivity.this.getAlbum().getCurrentMediaIndex());

                Show_Data.songpostion = DashboardActivity.this.getAlbum().getCurrentMediaIndex();
                intent.putExtra("videofilename", m.getPath());
                DashboardActivity.this.startActivity(intent);
            }
        }
    };
    private OnLongClickListener photosOnLongClickListener = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            menudeletecheck = true;
            Media m = (Media) v.findViewById(R.id.tvVideoName).getTag();
            if (DashboardActivity.this.editMode) {
                DashboardActivity.this.getAlbum().selectAllPhotosUpTo(DashboardActivity.this.getAlbum().getIndex(m), mediaAdapter);
            } else {
                DashboardActivity.this.mediaAdapter.notifyItemChanged(DashboardActivity.this.getAlbum().toggleSelectPhoto(m));
                DashboardActivity.this.editMode = true;
            }
            invalidateOptionsMenu();
            return true;
        }
    };


    protected void onDestroy() {
        super.onDestroy();

    }
    private SharedPreferences sharedPref;
    private boolean pickMode = false;
    private RecyclerView rvAlbums;
    private GridSpacingItemDecoration rvAlbumsDecoration;
    private RecyclerView rvMedia;
    private GridSpacingItemDecoration rvMediaDecoration;
    private SecurityHelper securityObj;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private class PrepareAlbumTask extends AsyncTask<Void, Integer, Void> {
        private PrepareAlbumTask() {
        }

        protected void onPreExecute() {
            DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
            DashboardActivity.this.toggleRecyclersVisibility(true);
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            DashboardActivity.this.getAlbums().loadAlbums(DashboardActivity.this.getApplicationContext(), DashboardActivity.this.hidden);
            return null;
        }

        protected void onPostExecute(Void result) {
            DashboardActivity.this.albumsAdapter.swapDataSet(DashboardActivity.this.getAlbums().dispAlbums);
            DashboardActivity.this.checkNothing();
            DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
            DashboardActivity.this.getAlbums().saveBackup(DashboardActivity.this.getApplicationContext());
        }
    }

    private class PreparePhotosTask extends AsyncTask<Void, Void, Void> {
        private PreparePhotosTask() {
        }

        protected void onPreExecute() {
            DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
            DashboardActivity.this.toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            DashboardActivity.this.getAlbum().updatePhotos(DashboardActivity.this.getApplicationContext());
            return null;
        }

        protected void onPostExecute(Void result) {
            DashboardActivity.this.mediaAdapter.swapDataSet(DashboardActivity.this.getAlbum().getMedia());
            if (!DashboardActivity.this.hidden) {
                HandlingAlbums.addAlbumToBackup(DashboardActivity.this.getApplicationContext(), DashboardActivity.this.getAlbum());
            }
            DashboardActivity.this.checkNothing();
            DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
        }
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_dashboard);
        this.tool_more_icon = (ImageView) findViewById(R.id.tool_more_icon);
        rel_lay_appid=(RelativeLayout)findViewById(R.id.recycle_appid_layout);
        recycle_app_list=(RecyclerView)findViewById(R.id.recycle_app_list);
        getWindow().setFlags(1024, 1024);
        tool_more_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // rel_lay_appid.setVisibility(View.VISIBLE);
                int visibilityone = rel_lay_appid.getVisibility();

                if (visibilityone == View.VISIBLE) {
                    rel_lay_appid.setVisibility(View.GONE);
                }else{
                    rel_lay_appid.setVisibility(View.VISIBLE);
                }

                Animation slide_down = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.from_righttoleft);
                rel_lay_appid.startAnimation(slide_down);
                rl_google=(RelativeLayout)findViewById(R.id.rl_google);


            }
        });
        this.activity = this;

        this.SP = PreferenceUtil.getInstance(getApplicationContext());
        this.albumsMode = true;
        this.editMode = false;
        this.securityObj = new SecurityHelper(this);
        initUI();
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void checkAppOpenCount() {
        int count = this.sharedPref.getInt(APP_OPEN_COUNT, 0);
        if (count == 5) {
            showReviewDialog(this);
        } else if (count < 5) {
            this.sharedPref.edit().putInt(APP_OPEN_COUNT, count + 1).apply();
        }
    }






    public void onResume() {
        super.onResume();
        try {
            this.securityObj.updateSecuritySetting();
            setupUI();
            getAlbums().clearSelectedAlbums();
            getAlbum().clearSelectedPhotos();
            if (!this.SP.getBoolean("auto_update_media", false)) {
                this.albumsAdapter.notifyDataSetChanged();
                this.mediaAdapter.notifyDataSetChanged();
            } else if (!this.albumsMode) {
                new PreparePhotosTask().execute(new Void[0]);
            } else if (!this.firstLaunch) {
                new PrepareAlbumTask().execute(new Void[0]);
            }
            invalidateOptionsMenu();
            this.firstLaunch = false;
        } catch (Exception e) {

        }
    }

    private void displayCurrentAlbumMedia(boolean reload) {
        this.toolbar.setTitle(getAlbum().getName());
        this.toolbar.setTitleTextColor(Color.WHITE);
        this.mediaAdapter.swapDataSet(getAlbum().getMedia());
        if (reload) {
            new PreparePhotosTask().execute(new Void[0]);
        }
        this.editMode = false;
        this.albumsMode = false;
        invalidateOptionsMenu();
    }

    private void displayAlbums() {
        displayAlbums(true);
    }

    private void displayAlbums(boolean reload) {

        this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
        if (reload) {
            new PrepareAlbumTask().execute(new Void[0]);
        }
        this.tool_back_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        this.albumsMode = true;
        this.editMode = false;
        invalidateOptionsMenu();
        this.mediaAdapter.swapDataSet(new ArrayList());
        this.rvMedia.scrollToPosition(0);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initUI() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        this.tool_back_icon = (ImageView) findViewById(R.id.tool_back_icon);
        this.tool_delete_icon = (ImageView) findViewById(R.id.tool_delete_icon);
        this.tv_selctive = (TextView) findViewById(R.id.tv_selctive);
        this.tool_text_title = (TextView) findViewById(R.id.tool_text_title);
        this.rvAlbums = (RecyclerView) findViewById(R.id.recyclerviewAlbums);
        this.rvMedia = (RecyclerView) findViewById(R.id.recyclerviewPhotos);
        this.rvAlbums.setHasFixedSize(true);
        this.rvAlbums.setItemAnimator(new DefaultItemAnimator());
        this.rvMedia.setHasFixedSize(true);
        this.rvMedia.setItemAnimator(new DefaultItemAnimator());
        this.albumsAdapter = new AlbumAdapter(getAlbums().dispAlbums,textval,longcheck, this);
        this.albumsAdapter.setOnClickListener(this.albumOnClickListener);
        this.albumsAdapter.setOnLongClickListener(this.albumOnLongCLickListener);
        this.rvAlbums.setAdapter(this.albumsAdapter);

        this.mediaAdapter = new MediaAdapter(getAlbum().getMedia(), this);
        this.mediaAdapter.setOnClickListener(this.photosOnClickListener);
        this.mediaAdapter.setOnLongClickListener(this.photosOnLongClickListener);
        this.rvMedia.setAdapter(this.mediaAdapter);
        int spanCount = this.SP.getInt("n_columns_folders", 1);
        this.rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(7, getApplicationContext()), true);
        this.rvAlbums.addItemDecoration(this.rvAlbumsDecoration);
        this.rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));
        spanCount = this.SP.getInt("n_columns_media", 1);
        this.rvMediaDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(7, getApplicationContext()), true);
        this.rvMedia.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
        this.rvMedia.addItemDecoration(this.rvMediaDecoration);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //this.swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        //  this.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (DashboardActivity.this.albumsMode) {
                    DashboardActivity.this.getAlbums().clearSelectedAlbums();
                    new PrepareAlbumTask().execute(new Void[0]);
                    return;
                }
                DashboardActivity.this.getAlbum().clearSelectedPhotos();
                new PreparePhotosTask().execute(new Void[0]);
            }
        });
        // setRecentApp(getString(R.string.AppName));
        setupUI();
    }

    private void updateColumnsRvs() {
        updateColumnsRvAlbums();
        updateColumnsRvMedia();
    }

    private void updateColumnsRvAlbums() {
        int spanCount = this.SP.getInt("n_columns_folders", 1);
        if (spanCount != ((GridLayoutManager) this.rvAlbums.getLayoutManager()).getSpanCount()) {
            this.rvAlbums.removeItemDecoration(this.rvAlbumsDecoration);
            this.rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(7, getApplicationContext()), true);
            this.rvAlbums.addItemDecoration(this.rvAlbumsDecoration);
            this.rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));
        }
    }

    private void updateColumnsRvMedia() {
        int spanCount = this.SP.getInt("n_columns_media", 1);
        if (spanCount != ((GridLayoutManager) this.rvMedia.getLayoutManager()).getSpanCount()) {
            ((GridLayoutManager) this.rvMedia.getLayoutManager()).getSpanCount();
            this.rvMedia.removeItemDecoration(this.rvMediaDecoration);
            this.rvMediaDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(7, getApplicationContext()), true);
            this.rvMedia.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
            this.rvMedia.addItemDecoration(this.rvMediaDecoration);
        }
    }

    @TargetApi(21)
    public final void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == -1 && requestCode == this.REQUEST_CODE_SD_CARD_PERMISSIONS) {
            Uri treeUri = resultData.getData();
            ContentHelper.saveSdCardInfo(getApplicationContext(), treeUri);
            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Toast.makeText(this, R.string.got_permission_wr_sdcard, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSdCardPermissions() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        AlertDialogsHelper.getTextDialog(this, dialogBuilder, R.string.sd_card_write_permission_title, R.string.sd_card_permissions_message);
        dialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (VERSION.SDK_INT >= 21) {
                    DashboardActivity.this.startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), DashboardActivity.this.REQUEST_CODE_SD_CARD_PERMISSIONS);
                }
            }
        });
        dialogBuilder.show();
    }

    private void setupUI() {
        updateColumnsRvs();
//        this.toolbar.setPopupTheme(getPopupToolbarStyle());
        this.toolbar.setBackgroundColor(Color.GRAY);
        //this.toolbar.getResources().getColor(R.color.colorPrimary);
        this.toolbar.setTitleTextColor(Color.WHITE);

//        this.swipeRefreshLayout.setColorSchemeColors(getAccentColor());
//        this.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
//        setStatusBarColor();
//        setNavBarColor();
//        this.rvAlbums.setBackgroundColor(getBackgroundColor());
//        this.rvMedia.setBackgroundColor(getBackgroundColor());
//        this.mediaAdapter.updatePlaceholder(getApplicationContext());
//        this.albumsAdapter.updateTheme();
        ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scrollbar);
    }

    int i;

    private void updateSelectedStuff() {
        if (this.albumsMode) {
            if (this.editMode) {
                tool_text_title.setVisibility(View.GONE);
                tool_more_icon.setVisibility(View.GONE);
                tv_selctive.setVisibility(View.VISIBLE);
                tool_delete_icon.setVisibility(View.VISIBLE);
                this.tv_selctive.setText(getAlbums().getSelectedCount() + "/" + getAlbums().dispAlbums.size());
                tool_delete_icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(DashboardActivity.this);
                            i = (DashboardActivity.this.albumsMode || !DashboardActivity.this.editMode) ? R.string.delete_album_message : R.string.delete_photos_message;
                            AlertDialogsHelper.getTextDialog(DashboardActivity.this, deleteDialog, R.string.delete, i);
                            deleteDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                            deleteDialog.setPositiveButton(getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new AsyncTask<String, Integer, Boolean>() {
                                        protected void onPreExecute() {
                                            DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
                                            super.onPreExecute();
                                        }

                                        protected Boolean doInBackground(String... arg0) {
                                            if (DashboardActivity.this.albumsMode) {
                                                return Boolean.valueOf(DashboardActivity.this.getAlbums().deleteSelectedAlbums(DashboardActivity.this));
                                            }
                                            if (DashboardActivity.this.editMode) {
                                                return Boolean.valueOf(DashboardActivity.this.getAlbum().deleteSelectedMedia(DashboardActivity.this.getApplicationContext()));
                                            }
                                            boolean succ = DashboardActivity.this.getAlbums().deleteAlbum(DashboardActivity.this.getAlbum(), DashboardActivity.this.getApplicationContext());
                                            DashboardActivity.this.getAlbum().getMedia().clear();
                                            return Boolean.valueOf(succ);
                                        }

                                        protected void onPostExecute(Boolean result) {
                                            if (!result.booleanValue()) {
                                                DashboardActivity.this.requestSdCardPermissions();
                                            } else if (DashboardActivity.this.albumsMode) {
                                                DashboardActivity.this.getAlbums().clearSelectedAlbums();
                                                DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
                                            } else if (DashboardActivity.this.getAlbum().getMedia().size() == 0) {
                                                DashboardActivity.this.getAlbums().removeCurrentAlbum();
                                                DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
                                                DashboardActivity.this.displayAlbums();
                                            } else {
                                                DashboardActivity.this.mediaAdapter.swapDataSet(DashboardActivity.this.getAlbum().getMedia());
                                            }
                                            DashboardActivity.this.invalidateOptionsMenu();
                                            DashboardActivity.this.checkNothing();
                                            DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);

                                            if (DashboardActivity.this.albumsMode) {
                                                DashboardActivity.this.getAlbums().clearSelectedAlbums();
                                                new PrepareAlbumTask().execute(new Void[0]);
                                                return;
                                            }
                                            DashboardActivity.this.getAlbum().clearSelectedPhotos();
                                            new PreparePhotosTask().execute(new Void[0]);

                                        }
                                    }.execute(new String[0]);

                                }
                            });
                            deleteDialog.show();
                        } catch (Exception e) {

                        }
//                        return true;
                    }
                });

                // this.toolbar.setTitle(getAlbums().getSelectedCount() + "/" + getAlbums().dispAlbums.size());
            } else {
                tool_text_title.setVisibility(View.VISIBLE);
                tool_more_icon.setVisibility(View.VISIBLE);
                tv_selctive.setVisibility(View.GONE);
                tool_delete_icon.setVisibility(View.GONE);
                this.toolbar.setTitle(getString(R.string.app_name));
                // this.toolbar.setNavigationIcon(R.drawable.backaero);
//                this.toolbar.setNavigationOnClickListener(new OnClickListener() {
//                    public void onClick(View v) {
//                        if (DashboardActivity.this.doubleBackToExitPressedOnce) {
//                            DashboardActivity.this.finish();
//                            return;
//                        }
//                        DashboardActivity.this.doubleBackToExitPressedOnce = true;
//                        Toast.makeText(DashboardActivity.this.getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                DashboardActivity.this.doubleBackToExitPressedOnce = false;
//                            }
//                        }, 2000);
//                    }
//                });
            }
        } else if (this.editMode) {
            tool_text_title.setVisibility(View.GONE);
            tv_selctive.setVisibility(View.VISIBLE);
            this.tv_selctive.setText(getAlbum().getSelectedCount() + "/" + getAlbum().getMedia().size());

            //this.toolbar.setTitle(getAlbum().getSelectedCount() + "/" + getAlbum().getMedia().size());
        } else {
            tool_text_title.setVisibility(View.VISIBLE);
            tv_selctive.setVisibility(View.GONE);
            tool_text_title.setText(getAlbum().getName());
            //this.toolbar.setTitle(getAlbum().getName());
            // this.toolbar.setNavigationIcon(R.drawable.backaero);
//            this.toolbar.setNavigationOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    DashboardActivity.this.displayAlbums();
//                }
//            });
        }
        if (this.editMode) {
//            this.toolbar.setNavigationIcon(R.drawable.backaero);
//            this.toolbar.setNavigationOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    DashboardActivity.this.finishEditMode();
//                }
//            });
        }
    }

    private void finishEditMode() {
        this.editMode = false;
        if (this.albumsMode) {
            getAlbums().clearSelectedAlbums();
            this.albumsAdapter.notifyDataSetChanged();
        } else {
            getAlbum().clearSelectedPhotos();
            this.mediaAdapter.notifyDataSetChanged();
        }
        invalidateOptionsMenu();
    }

    @SuppressLint("WrongConstant")
    private void checkNothing() {
        TextView a = (TextView) findViewById(R.id.nothing_to_show);
        a.setTextColor(Color.BLACK);
        int i = (!(this.albumsMode && getAlbums().dispAlbums.size() == 0) && (this.albumsMode || getAlbum().getMedia().size() != 0)) ? 8 : 0;
        a.setVisibility(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem findItem;
        CharSequence string;
        int i = R.string.clear_selected;
        boolean z = false;
        getMenuInflater().inflate(R.menu.menu_albums, menu);
        if (!this.albumsMode) {
            MenuItem findItem2 = menu.findItem(R.id.select_all);
            if (getAlbum().getSelectedCount() != this.mediaAdapter.getItemCount()) {
                i = R.string.select_all;
            }
            findItem2.setTitle(getString(i));
            MenuItem findItem3 = menu.findItem(R.id.ascending_sort_action);
            if (getAlbum().settings.getSortingOrder() == SortingOrder.ASCENDING) {
                z = true;
            }
            findItem3.setChecked(z);
            switch (getAlbum().settings.getSortingMode()) {
                case NAME:
                    menu.findItem(R.id.name_sort_action).setChecked(true);
                    break;
                case SIZE:
                    menu.findItem(R.id.size_sort_action).setChecked(true);
                    break;
                case NUMERIC:
                    menu.findItem(R.id.numeric_sort_action).setChecked(true);
                    break;
                case TYPE:
                    menu.findItem(R.id.type_sort_action).setChecked(true);
                    break;
                default:
                    menu.findItem(R.id.date_taken_sort_action).setChecked(true);
                    break;
            }
        }
        boolean z2;
        //findItem2 = menu.findItem(R.id.select_all);
        if (getAlbums().getSelectedCount() != this.albumsAdapter.getItemCount()) {
            i = R.string.select_all;
        }
        // findItem2.setTitle(getString(i));
        findItem = menu.findItem(R.id.ascending_sort_action);
        if (getAlbums().getSortingOrder() == SortingOrder.ASCENDING) {
            z2 = true;
        } else {
            z2 = false;
        }
        findItem.setChecked(z2);
        switch (getAlbums().getSortingMode()) {
            case NAME:
                menu.findItem(R.id.name_sort_action).setChecked(true);
                break;
            case SIZE:
                menu.findItem(R.id.size_sort_action).setChecked(true);
                break;
            case NUMERIC:
                menu.findItem(R.id.numeric_sort_action).setChecked(true);
                break;
            default:
                menu.findItem(R.id.date_taken_sort_action).setChecked(true);
                break;
        }
        //findItem = menu.findItem(R.id.hideAlbumButton);
//        if (this.hidden) {
//            string = getString(R.string.unhide);
//        } else {
//            string = getString(R.string.hide);
//        }
        //  findItem.setTitle(string);
//        menu.findItem(R.id.search_action).setIcon(getToolbarIcon(Icon.gmd_search));

        menu.findItem(R.id.delete_action).setIcon(R.drawable.ic_delete);
        //menu.findItem(R.id.delete_action).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


//        menu.findItem(R.id.sort_action).setIcon(getToolbarIcon(Icon.gmd_sort));
        menu.findItem(R.id.sharePhotos).setIcon(R.drawable.ic_share);
        // MenuItem searchItem = menu.findItem(R.id.search_action);
//        menu.findItem(R.id.settingsdemo).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                DashboardActivity.this.replaceNavigationFragment();
//                DashboardActivity.this.openCloseDrawer();
//                return false;
//            }
//        });
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setQueryHint(getString(R.string.coming_soon));
//        searchView.setOnQueryTextListener(this);
//        MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                DashboardActivity.this.albumsAdapter.setFilter(DashboardActivity.this.getAlbums().dispAlbums);
//                return true;
//            }
//
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//        });
        return true;
    }

    public boolean onQueryTextChange(String newText) {
        this.albumsAdapter.setFilter((ArrayList) filter(getAlbums().dispAlbums, newText));
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Album> filter(List<Album> models, String query) {
        query = query.toLowerCase();
        List<Album> filteredModelList = new ArrayList();
        for (Album model : models) {
            if (model.getName().toLowerCase().contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean z;
        boolean z2 = true;
        if (this.albumsMode) {
            this.editMode = getAlbums().getSelectedCount() != 0;
            menu.setGroupVisible(R.id.album_options_menu, this.editMode);
            menu.setGroupVisible(R.id.photos_option_men, false);
        } else {
            this.editMode = getAlbum().areMediaSelected();
            menu.setGroupVisible(R.id.photos_option_men, this.editMode);
            menu.setGroupVisible(R.id.album_options_menu, !this.editMode);
        }
        togglePrimaryToolbarOptions(menu);
        updateSelectedStuff();
        // menu.findItem(R.id.excludeAlbumButton).setVisible(this.editMode);
        menu.findItem(R.id.select_all).setVisible(this.editMode);
        MenuItem findItem = menu.findItem(R.id.installShortcut);
        if (this.albumsMode && this.editMode) {
            z = true;
        } else {
            z = false;
        }
        findItem.setVisible(z);
        findItem = menu.findItem(R.id.type_sort_action);
        if (this.albumsMode) {
            z = false;
        } else {
            z = true;
        }
        findItem.setVisible(z);
        findItem = menu.findItem(R.id.delete_action);
        if (!this.albumsMode || this.editMode) {
            z = true;
        } else {
            z = false;
        }
        findItem.setVisible(z);
        //findItem = menu.findItem(R.id.clear_album_preview);
        if (this.albumsMode || !getAlbum().hasCustomCover()) {
            z = false;
        } else {
            z = true;
        }
        findItem.setVisible(z);
        // findItem = menu.findItem(R.id.renameAlbum);
        if (!(this.albumsMode && getAlbums().getSelectedCount() == 1) && (this.albumsMode || this.editMode)) {
            z = false;
        } else {
            z = true;
        }
        findItem.setVisible(z);
//        if (getAlbums().getSelectedCount() == 1) {
//            menu.findItem(R.id.set_pin_album).setTitle(getAlbums().getSelectedAlbum(0).isPinned() ? getString(R.string.un_pin) : getString(R.string.pin));
//        }
//        findItem = menu.findItem(R.id.set_pin_album);
        if (this.albumsMode && getAlbums().getSelectedCount() == 1) {
            z = true;
        } else {
            z = false;
        }
        findItem.setVisible(z);
        //  findItem = menu.findItem(R.id.setAsAlbumPreview);
        if (this.albumsMode) {
            z = false;
        } else {
            z = true;
        }
        findItem.setVisible(z);
        MenuItem findItem2 = menu.findItem(R.id.affixPhoto);
        if (this.albumsMode || getAlbum().getSelectedCount() <= 1) {
            z2 = false;
        }
        findItem2.setVisible(z2);
        return super.onPrepareOptionsMenu(menu);
    }

    private void togglePrimaryToolbarOptions(Menu menu) {
        menu.setGroupVisible(R.id.general_action, !this.editMode);
//        if (!this.editMode) {
//            menu.findItem(R.id.search_action).setVisible(this.albumsMode);
//        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder;
        int i;
        switch (item.getItemId()) {
            case R.id.name_sort_action:
                if (this.albumsMode) {
                    getAlbums().setDefaultSortingMode(SortingMode.NAME);
                    getAlbums().sortAlbums(getApplicationContext());
                    this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.NAME);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                }
                item.setChecked(true);
                return true;
            case R.id.date_taken_sort_action:
                if (this.albumsMode) {
                    getAlbums().setDefaultSortingMode(SortingMode.DATE);
                    getAlbums().sortAlbums(getApplicationContext());
                    this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.DATE);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                }
                item.setChecked(true);
                return true;
            case R.id.size_sort_action:
                if (this.albumsMode) {
                    getAlbums().setDefaultSortingMode(SortingMode.SIZE);
                    getAlbums().sortAlbums(getApplicationContext());
                    this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.SIZE);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                }
                item.setChecked(true);
                return true;
            case R.id.type_sort_action:
                if (!this.albumsMode) {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.TYPE);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                    item.setChecked(true);
                }
                return true;
            case R.id.numeric_sort_action:
                if (this.albumsMode) {
                    getAlbums().setDefaultSortingMode(SortingMode.NUMERIC);
                    getAlbums().sortAlbums(getApplicationContext());
                    this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.NUMERIC);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                }
                item.setChecked(true);
                return true;
            case R.id.ascending_sort_action:
                if (this.albumsMode) {
                    getAlbums().setDefaultSortingAscending(item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    getAlbums().sortAlbums(getApplicationContext());
                    this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                } else {
                    getAlbum().setDefaultSortingAscending(getApplicationContext(), item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    getAlbum().sortPhotos();
                    this.mediaAdapter.swapDataSet(getAlbum().getMedia());
                }
                item.setChecked(!item.isChecked());
                return true;
            case R.id.sharePhotos:
                ArrayList<Uri> files = new ArrayList();
                Iterator it = getAlbum().getSelectedMedia().iterator();
                while (it.hasNext()) {
                    files.add(((Media) it.next()).getUri());
                }
                String uri_to_string = String.valueOf(files.get(0));
                Uri imageUrixyz = Uri.parse(uri_to_string);
                Intent shareIntent1 = new Intent();
                shareIntent1.setAction("android.intent.action.SEND_MULTIPLE");
                //shareIntent1.setPackage("com.whatsapp");
                shareIntent1.putExtra("android.intent.extra.STREAM", imageUrixyz);
                shareIntent1.setType("video/*");
                shareIntent1.putExtra(Intent.EXTRA_TEXT, "" + Util.getShareText(getApplicationContext(), ""));
                shareIntent1.putExtra(Intent.EXTRA_SUBJECT, "" + getApplicationContext().getString(R.string.app_name));
                shareIntent1.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    startActivity(Intent.createChooser(shareIntent1, getResources().getText(R.string.send_to)));
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                    Toast.makeText(DashboardActivity.this, "Facebook doesn't installed", Toast.LENGTH_LONG).show();
                }
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.SEND_MULTIPLE");
//                intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.sent_to_action));
//                ArrayList<Uri> files = new ArrayList();
//                Iterator it = getAlbum().getSelectedMedia().iterator();
//                while (it.hasNext()) {
//                    files.add(((Media) it.next()).getUri());
//                }
//                intent.putParcelableArrayListExtra("android.intent.extra.STREAM", files);
//                intent.setType(StringUtils.getGenericMIME(getAlbum().getSelectedMedia(0).getMimeType()));
//                finishEditMode();
//                startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
                return true;
//            case R.id.action_copy:
//                this.bottomSheetDialogFragment = new SelectAlbumBottomSheet();
//                this.bottomSheetDialogFragment.setTitle(getString(R.string.copy_to));
//                this.bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
//                    public void folderSelected(String path) {
//                        boolean success = DashboardActivity.this.getAlbum().copySelectedPhotos(DashboardActivity.this.getApplicationContext(), path);
//                        DashboardActivity.this.finishEditMode();
//                        DashboardActivity.this.bottomSheetDialogFragment.dismiss();
//                        if (!success) {
//                            DashboardActivity.this.requestSdCardPermissions();
//                        }
//                    }
//                });
//                this.bottomSheetDialogFragment.show(getSupportFragmentManager(), this.bottomSheetDialogFragment.getTag());
//                return true;
//            case R.id.action_move:
//                this.bottomSheetDialogFragment = new SelectAlbumBottomSheet();
//                this.bottomSheetDialogFragment.setTitle(getString(R.string.move_to));
//                this.bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
//                    public void folderSelected(String path) {
//                        DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
//                        if (DashboardActivity.this.getAlbum().moveSelectedMedia(DashboardActivity.this.getApplicationContext(), path) > 0) {
//                            if (DashboardActivity.this.getAlbum().getMedia().size() == 0) {
//                                DashboardActivity.this.getAlbums().removeCurrentAlbum();
//                                DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
//                                DashboardActivity.this.displayAlbums();
//                            }
//                            DashboardActivity.this.mediaAdapter.swapDataSet(DashboardActivity.this.getAlbum().getMedia());
//                            DashboardActivity.this.finishEditMode();
//                            DashboardActivity.this.invalidateOptionsMenu();
//                        } else {
//                            DashboardActivity.this.requestSdCardPermissions();
//                        }
//                        DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
//                        DashboardActivity.this.bottomSheetDialogFragment.dismiss();
//                    }
//                });
//                this.bottomSheetDialogFragment.show(getSupportFragmentManager(), this.bottomSheetDialogFragment.getTag());
//                return true;
//            case R.id.excludeAlbumButton:
//                AlertDialog.Builder excludeDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
//                View excludeDialogLayout = getLayoutInflater().inflate(R.layout.dialog_exclude, null);
//                TextView textViewExcludeTitle = (TextView) excludeDialogLayout.findViewById(R.id.text_dialog_title);
//                TextView textViewExcludeMessage = (TextView) excludeDialogLayout.findViewById(R.id.text_dialog_message);
//                Spinner spinnerParents = (Spinner) excludeDialogLayout.findViewById(R.id.parents_folder);
//                //spinnerParents.getBackground().setColorFilter(getIconColor(), Mode.SRC_ATOP);
//                ((CardView) excludeDialogLayout.findViewById(R.id.message_card)).setCardBackgroundColor(Color.WHITE);
//                // textViewExcludeTitle.setBackgroundColor(getPrimaryColor());
//                textViewExcludeTitle.setText(getString(R.string.exclude));
//                if (!this.albumsMode || getAlbums().getSelectedCount() <= 1) {
//                    textViewExcludeMessage.setText(R.string.exclude_album_message);
//                    spinnerParents.setAdapter(getSpinnerAdapter(this.albumsMode ? getAlbums().getSelectedAlbum(0).getParentsFolders() : getAlbum().getParentsFolders()));
//                } else {
//                    textViewExcludeMessage.setText(R.string.exclude_albums_message);
//                    spinnerParents.setVisibility(View.GONE);
//                }
//                textViewExcludeMessage.setTextColor(Color.BLACK);
//                excludeDialogBuilder.setView(excludeDialogLayout);
//                final Spinner spinner = spinnerParents;
//                excludeDialogBuilder.setPositiveButton(getString(R.string.exclude).toUpperCase(), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if (!DashboardActivity.this.albumsMode || DashboardActivity.this.getAlbums().getSelectedCount() <= 1) {
//                            DashboardActivity.this.customAlbumsHelper.excludeAlbum(spinner.getSelectedItem().toString());
//                            DashboardActivity.this.finishEditMode();
//                            DashboardActivity.this.displayAlbums(true);
//                            return;
//                        }
//                        DashboardActivity.this.getAlbums().excludeSelectedAlbums(DashboardActivity.this.getApplicationContext());
//                        DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
//                        DashboardActivity.this.invalidateOptionsMenu();
//                    }
//                });
//                excludeDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
//                excludeDialogBuilder.show();
//                return true;
//            case R.id.hideAlbumButton:
//                builder = new AlertDialog.Builder(DashboardActivity.this);
//                AlertDialogsHelper.getTextDialog(this, builder, this.hidden ? R.string.unhide : R.string.hide, this.hidden ? R.string.unhide_album_message : R.string.hide_album_message);
//                if (this.hidden) {
//                    i = R.string.unhide;
//                } else {
//                    i = R.string.hide;
//                }
//                builder.setPositiveButton(getString(i).toUpperCase(), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if (DashboardActivity.this.albumsMode) {
//                            if (DashboardActivity.this.hidden) {
//                                DashboardActivity.this.getAlbums().unHideSelectedAlbums(DashboardActivity.this.getApplicationContext());
//                            } else {
//                                DashboardActivity.this.getAlbums().hideSelectedAlbums(DashboardActivity.this.getApplicationContext());
//                            }
//                            DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
//                            DashboardActivity.this.invalidateOptionsMenu();
//                            return;
//                        }
//                        if (DashboardActivity.this.hidden) {
//                            DashboardActivity.this.getAlbums().unHideAlbum(DashboardActivity.this.getAlbum().getPath(), DashboardActivity.this.getApplicationContext());
//                        } else {
//                            DashboardActivity.this.getAlbums().hideAlbum(DashboardActivity.this.getAlbum().getPath(), DashboardActivity.this.getApplicationContext());
//                        }
//                        DashboardActivity.this.displayAlbums(true);
//                    }
//                });
//                if (!this.hidden) {
//                    builder.setNeutralButton(getString(R.string.exclude).toUpperCase(), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (DashboardActivity.this.albumsMode) {
//                                DashboardActivity.this.getAlbums().excludeSelectedAlbums(DashboardActivity.this.getApplicationContext());
//                                DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
//                                DashboardActivity.this.invalidateOptionsMenu();
//                                return;
//                            }
//                            DashboardActivity.this.customAlbumsHelper.excludeAlbum(DashboardActivity.this.getAlbum().getPath());
//                            DashboardActivity.this.displayAlbums(true);
//                        }
//                    });
//                }
//                builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
//                builder.show();
//                return true;
//            case R.id.renameAlbum:
//                CharSequence name;
//                builder = new AlertDialog.Builder(DashboardActivity.this);
//                final EditText editTextNewName = new EditText(getApplicationContext());
//                if (this.albumsMode) {
//                    name = getAlbums().getSelectedAlbum(0).getName();
//                } else {
//                    name = getAlbum().getName();
//                }
//                editTextNewName.setText(name);
//                AlertDialogsHelper.getInsertTextDialog(this, builder, editTextNewName, R.string.rename_album);
//                builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
//                builder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                AlertDialog renameDialog = builder.create();
//                renameDialog.show();
//                final AlertDialog alertDialog = renameDialog;
//                renameDialog.getButton(-1).setOnClickListener(new OnClickListener() {
//                    public void onClick(View dialog) {
//                        if (editTextNewName.length() != 0) {
//                            boolean success;
//                            DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
//                            if (DashboardActivity.this.albumsMode) {
//                                int index = DashboardActivity.this.getAlbums().dispAlbums.indexOf(DashboardActivity.this.getAlbums().getSelectedAlbum(0));
//                                DashboardActivity.this.getAlbums().getAlbum(index).updatePhotos(DashboardActivity.this.getApplicationContext());
//                                success = DashboardActivity.this.getAlbums().getAlbum(index).renameAlbum(DashboardActivity.this.getApplicationContext(), editTextNewName.getText().toString());
//                                DashboardActivity.this.albumsAdapter.notifyItemChanged(index);
//                            } else {
//                                success = DashboardActivity.this.getAlbum().renameAlbum(DashboardActivity.this.getApplicationContext(), editTextNewName.getText().toString());
//                                DashboardActivity.this.toolbar.setTitle(DashboardActivity.this.getAlbum().getName());
//                                DashboardActivity.this.mediaAdapter.notifyDataSetChanged();
//                            }
//                            alertDialog.dismiss();
//                            if (!success) {
//                                DashboardActivity.this.requestSdCardPermissions();
//                            }
//                            DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
//                            return;
//                        }
//                        StringUtils.showToast(DashboardActivity.this.getApplicationContext(), DashboardActivity.this.getString(R.string.insert_something));
//                        editTextNewName.requestFocus();
//                    }
//                });
//                return true;
            case R.id.select_all:
                if (this.albumsMode) {
                    if (getAlbums().getSelectedCount() == this.albumsAdapter.getItemCount()) {
                        this.editMode = false;
                        getAlbums().clearSelectedAlbums();
                    } else {
                        getAlbums().selectAllAlbums();
                    }
                    this.albumsAdapter.notifyDataSetChanged();
                } else {
                    if (getAlbum().getSelectedCount() == this.mediaAdapter.getItemCount()) {
                        this.editMode = false;
                        getAlbum().clearSelectedPhotos();
                    } else {
                        getAlbum().selectAllPhotos();
                    }
                    this.mediaAdapter.notifyDataSetChanged();
                }
                invalidateOptionsMenu();
                return true;
            case R.id.delete_action:
                if (menudeletecheck) {

                    try {
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(DashboardActivity.this);
                        i = (this.albumsMode || !this.editMode) ? R.string.delete_album_message : R.string.delete_photos_message;
                        AlertDialogsHelper.getTextDialog(this, deleteDialog, R.string.delete, i);
                        deleteDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                        deleteDialog.setPositiveButton(getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncTask<String, Integer, Boolean>() {
                                    protected void onPreExecute() {
                                        DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
                                        super.onPreExecute();
                                    }

                                    protected Boolean doInBackground(String... arg0) {
                                        if (DashboardActivity.this.albumsMode) {
                                            return Boolean.valueOf(DashboardActivity.this.getAlbums().deleteSelectedAlbums(DashboardActivity.this));
                                        }
                                        if (DashboardActivity.this.editMode) {
                                            return Boolean.valueOf(DashboardActivity.this.getAlbum().deleteSelectedMedia(DashboardActivity.this.getApplicationContext()));
                                        }
                                        boolean succ = DashboardActivity.this.getAlbums().deleteAlbum(DashboardActivity.this.getAlbum(), DashboardActivity.this.getApplicationContext());
                                        DashboardActivity.this.getAlbum().getMedia().clear();
                                        return Boolean.valueOf(succ);
                                    }

                                    protected void onPostExecute(Boolean result) {
                                        if (!result.booleanValue()) {
                                            DashboardActivity.this.requestSdCardPermissions();
                                        } else if (DashboardActivity.this.albumsMode) {
                                            DashboardActivity.this.getAlbums().clearSelectedAlbums();
                                            DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
                                        } else if (DashboardActivity.this.getAlbum().getMedia().size() == 0) {
                                            DashboardActivity.this.getAlbums().removeCurrentAlbum();
                                            DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
                                            DashboardActivity.this.displayAlbums();
                                        } else {
                                            DashboardActivity.this.mediaAdapter.swapDataSet(DashboardActivity.this.getAlbum().getMedia());
                                        }
                                        DashboardActivity.this.invalidateOptionsMenu();
                                        DashboardActivity.this.checkNothing();
                                        DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);

                                        if (DashboardActivity.this.albumsMode) {
                                            DashboardActivity.this.getAlbums().clearSelectedAlbums();
                                            new PrepareAlbumTask().execute(new Void[0]);
                                            return;
                                        }
                                        DashboardActivity.this.getAlbum().clearSelectedPhotos();
                                        new PreparePhotosTask().execute(new Void[0]);

                                    }
                                }.execute(new String[0]);

//
//
//                            Toast.makeText(activity, "nishahqwhy", Toast.LENGTH_SHORT).show();
//                            if (DashboardActivity.this.securityObj.isActiveSecurity() && DashboardActivity.this.securityObj.isPasswordOnDelete()) {
//                                AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(DashboardActivity.this);
//                                final EditText editTextPassword = DashboardActivity.this.securityObj.getInsertPasswordDialog(DashboardActivity.this, passwordDialogBuilder);
//                                passwordDialogBuilder.setNegativeButton(DashboardActivity.this.getString(R.string.cancel).toUpperCase(), null);
//                                passwordDialogBuilder.setPositiveButton(DashboardActivity.this.getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(DashboardActivity.this, "fdrgrdgtsehsejres", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                final AlertDialog passwordDialog = passwordDialogBuilder.create();
//                                passwordDialog.show();
//                                passwordDialog.getButton(-1).setOnClickListener(new OnClickListener() {
//                                    public void onClick(View v) {
//                                        if (DashboardActivity.this.securityObj.checkPassword(editTextPassword.getText().toString())) {
//                                            passwordDialog.dismiss();
////                                            new AsyncTask<String, Integer, Boolean>() {
////                                                protected void onPreExecute() {
////                                                    DashboardActivity.this.swipeRefreshLayout.setRefreshing(true);
////                                                    super.onPreExecute();
////                                                }
////
////                                                protected Boolean doInBackground(String... arg0) {
////                                                    if (DashboardActivity.this.albumsMode) {
////                                                        return Boolean.valueOf(DashboardActivity.this.getAlbums().deleteSelectedAlbums(DashboardActivity.this));
////                                                    }
////                                                    if (DashboardActivity.this.editMode) {
////                                                        return Boolean.valueOf(DashboardActivity.this.getAlbum().deleteSelectedMedia(DashboardActivity.this.getApplicationContext()));
////                                                    }
////                                                    boolean succ = DashboardActivity.this.getAlbums().deleteAlbum(DashboardActivity.this.getAlbum(), DashboardActivity.this.getApplicationContext());
////                                                    DashboardActivity.this.getAlbum().getMedia().clear();
////                                                    return Boolean.valueOf(succ);
////                                                }
////
////                                                protected void onPostExecute(Boolean result) {
////                                                    if (!result.booleanValue()) {
////                                                        DashboardActivity.this.requestSdCardPermissions();
////                                                    } else if (DashboardActivity.this.albumsMode) {
////                                                        DashboardActivity.this.getAlbums().clearSelectedAlbums();
////                                                        DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
////                                                    } else if (DashboardActivity.this.getAlbum().getMedia().size() == 0) {
////                                                        DashboardActivity.this.getAlbums().removeCurrentAlbum();
////                                                        DashboardActivity.this.albumsAdapter.notifyDataSetChanged();
////                                                        DashboardActivity.this.displayAlbums();
////                                                    } else {
////                                                        DashboardActivity.this.mediaAdapter.swapDataSet(DashboardActivity.this.getAlbum().getMedia());
////                                                    }
////                                                    DashboardActivity.this.invalidateOptionsMenu();
////                                                    DashboardActivity.this.checkNothing();
////                                                    DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
////                                                }
////                                            }.execute(new String[0]);
//                                            return;
//                                        }
//                                        Toast.makeText(DashboardActivity.this.getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
//                                        editTextPassword.getText().clear();
//                                        editTextPassword.requestFocus();
//                                    }
//                                });
//                                return;
//                            }
                                // /* anonymous class already generated */.execute(new String[0]);
                            }
                        });
                        deleteDialog.show();
                    } catch (Exception e) {
//                    FirebaseCrash.logcat(6, TAG, "DashBoard Activity Delete");
//                    FirebaseCrash.report(e);
                    }
                } else {
                    Toast.makeText(activity, "Please select one OR multiple video... ", Toast.LENGTH_SHORT).show();
                }
                return true;
//            case R.id.clear_album_preview:
//                if (!this.albumsMode) {
//                    getAlbum().removeCoverAlbum(getApplicationContext());
//                }
//                return true;
//            case R.id.set_pin_album:
//                getAlbums().getSelectedAlbum(0).settings.togglePin(getApplicationContext());
//                getAlbums().sortAlbums(getApplicationContext());
//                getAlbums().clearSelectedAlbums();
//                this.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
//                invalidateOptionsMenu();
//                return true;
//            case R.id.setAsAlbumPreview:
//                return true;
            case R.id.installShortcut:
                getAlbums().installShortcutForSelectedAlbums(getApplicationContext());
                finishEditMode();
                return true;
            case R.id.affixPhoto:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DashboardActivity.this);
                View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_affix, null);
                dialogLayout.findViewById(R.id.affix_title).setBackgroundColor(Color.LTGRAY);
                ((CardView) dialogLayout.findViewById(R.id.affix_card)).setCardBackgroundColor(Color.WHITE);
                final SwitchCompat swVertical = (SwitchCompat) dialogLayout.findViewById(R.id.affix_vertical_switch);
                final SwitchCompat swSaveHere = (SwitchCompat) dialogLayout.findViewById(R.id.save_here_switch);
                final RadioGroup radioFormatGroup = (RadioGroup) dialogLayout.findViewById(R.id.radio_format);
                TextView txtQuality = (TextView) dialogLayout.findViewById(R.id.affix_quality_title);
                final SeekBar seekQuality = (SeekBar) dialogLayout.findViewById(R.id.seek_bar_quality);
                int color = Color.BLACK;
                ((TextView) dialogLayout.findViewById(R.id.affix_vertical_title)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.compression_settings_title)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.save_here_title)).setTextColor(color);
                color = Color.GRAY;
                ((TextView) dialogLayout.findViewById(R.id.save_here_sub)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.affix_vertical_sub)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.affix_format_sub)).setTextColor(color);
                txtQuality.setTextColor(color);

                ImageView imageView = ((ImageView) dialogLayout.findViewById(R.id.affix_quality_icon));
                ImageView imageView1 = ((ImageView) dialogLayout.findViewById(R.id.affix_format_icon));
                ImageView imageView2 = ((ImageView) dialogLayout.findViewById(R.id.affix_vertical_icon));
                ImageView imageView3 = ((ImageView) dialogLayout.findViewById(R.id.save_here_icon));
                seekQuality.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.BLACK, Mode.SRC_IN));

                RadioButton radioButton = ((RadioButton) dialogLayout.findViewById(R.id.radio_jpeg));
                RadioButton radioButton1 = ((RadioButton) dialogLayout.findViewById(R.id.radio_png));
                RadioButton radioButton2 = ((RadioButton) dialogLayout.findViewById(R.id.radio_webp));

                final TextView textView = txtQuality;
                seekQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText(Html.fromHtml(String.format(Locale.getDefault(), "%s <b>%d</b>", new Object[]{DashboardActivity.this.getString(R.string.quality), Integer.valueOf(progress)})));
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                seekQuality.setProgress(90);
                swVertical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //  DashboardActivity.this.updateSwitchColor(swVertical, DashboardActivity.this.getAccentColor());
                    }
                });
                swSaveHere.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // DashboardActivity.this.updateSwitchColor(swSaveHere, DashboardActivity.this.getAccentColor());
                    }
                });
                builder2.setView(dialogLayout);
                builder2.setPositiveButton((CharSequence) getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CompressFormat compressFormat;
                        switch (radioFormatGroup.getCheckedRadioButtonId()) {
                            case R.id.radio_png:
                                compressFormat = CompressFormat.PNG;
                                break;
                            case R.id.radio_webp:
                                compressFormat = CompressFormat.WEBP;
                                break;
                            default:
                                compressFormat = CompressFormat.JPEG;
                                break;
                        }
                        Affix.Options options = new Affix.Options(swSaveHere.isChecked() ? DashboardActivity.this.getAlbum().getPath() : Affix.getDefaultDirectoryPath(), compressFormat, seekQuality.getProgress(), swVertical.isChecked());
                        new AsyncTask<Affix.Options, Integer, Void>() {
                            private AlertDialog dialog;

                            protected void onPreExecute() {
                                this.dialog = AlertDialogsHelper.getProgressDialog(DashboardActivity.this, new AlertDialog.Builder(DashboardActivity.this), DashboardActivity.this.getString(R.string.affix), DashboardActivity.this.getString(R.string.affix_text));
                                this.dialog.show();
                                super.onPreExecute();
                            }

                            protected Void doInBackground(Affix.Options... arg0) {
                                ArrayList<Bitmap> bitmapArray = new ArrayList();
                                for (int i = 0; i < DashboardActivity.this.getAlbum().getSelectedCount(); i++) {
                                    if (!DashboardActivity.this.getAlbum().getSelectedMedia(i).isVideo()) {
                                        bitmapArray.add(DashboardActivity.this.getAlbum().getSelectedMedia(i).getBitmap());
                                    }
                                }
                                if (bitmapArray.size() > 1) {
                                    Affix.AffixBitmapList(DashboardActivity.this.getApplicationContext(), bitmapArray, arg0[0]);
                                } else {
                                    DashboardActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(DashboardActivity.this.getApplicationContext(), R.string.affix_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return null;
                            }

                            protected void onPostExecute(Void result) {
                                DashboardActivity.this.editMode = false;
                                DashboardActivity.this.getAlbum().clearSelectedPhotos();
                                this.dialog.dismiss();
                                DashboardActivity.this.invalidateOptionsMenu();
                                DashboardActivity.this.mediaAdapter.notifyDataSetChanged();
                                new PreparePhotosTask().execute(new Void[0]);
                            }
                        }.execute(new Affix.Options[]{options});
                    }
                });
                builder2.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                builder2.show();
                return true;
            case R.id.settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private SpinnerAdapter getSpinnerAdapter(ArrayList<String> strings) {
        return null;
    }

    private void toggleRecyclersVisibility(boolean albumsMode) {
        int i;
        int i2 = 8;
        RecyclerView recyclerView = this.rvAlbums;
        if (albumsMode) {
            i = 0;
        } else {
            i = 8;
        }
        recyclerView.setVisibility(i);
        RecyclerView recyclerView2 = this.rvMedia;
        if (!albumsMode) {
            i2 = 0;
        }
        recyclerView2.setVisibility(i2);
    }

    @SuppressLint("WrongConstant")
    public void onBackPressed() {
        tool_text_title.setVisibility(View.VISIBLE);
        tool_text_title.setText(R.string.app_name);
        menudeletecheck = false;
        DashboardActivity.this.swipeRefreshLayout.setRefreshing(false);
        // FirebaseCrash.logcat(4, TAG, "Dashboard MoreApp onBackClick");
        if (this.drawer.isDrawerOpen(5)) {
            this.drawer.closeDrawer(5);
        } else if (this.editMode) {
            finishEditMode();
        } else if (!this.albumsMode) {
            displayAlbums();
            //   setRecentApp(getString(R.string.AppName));
        }
        else if (NetworkStatus.getConnectivityStatus(this)) {
            if (!this.sharedPref.getBoolean(REVIEW_DONE, false)) {
                checkAppOpenCount();
            }
           finish();
//            startActivity(new Intent(this, MoreApps.class));
        }
        else {
            finish();
        }
    }
    private static final String REVIEW_DONE = "reviewDone";
    private static final String APP_OPEN_COUNT = "appOpenCount";
    private static final String APP_OPEN_COUNT_SDK = "appOpenCountSDK";
    public static void showReviewDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.review_popup);
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(80);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.ll_review_liked_it).setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View v) {
                dialog.dismiss();
                Intent goToMarket = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName()));
                goToMarket.addFlags(1208483840);
                context.startActivity(goToMarket);
            }
        });
        dialog.findViewById(R.id.ll_review_hated_it).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.ib_review_dismiss).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
