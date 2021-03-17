package com.example.mygallery.foldersvideolist.videoplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.multidex.BuildConfig;

import com.example.mygallery.videofile.Show_Data;
import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.VideoListActivity;
import com.example.mygallery.utility.Glob;
import com.example.mygallery.utility.TimeUtils;
import com.example.mygallery.utility.Util;
import com.example.mygallery.videoview.CustomVideoView;
import com.example.mygallery.videoview.GestureDetection2;
import com.example.mygallery.videoview.ISwipeRefresh;
import com.example.mygallery.videoview.OnScrollTouchListenerControl;
import com.example.mygallery.videoview.VerticleSeekbar;
import com.example.mygallery.videoview.VideoPlayerState;
import com.example.mygallery.videoview.getRealPath;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VideotemppPlayingActivity extends Activity implements ISwipeRefresh {
    public int postion;
    public int toolspostion = -1;
    public int lockpostion = -1;
    int zoominoutpostion = 0;
    boolean aa = true;
    Dialog dialog_brightness, dialog_zoominout, dialog_native;

    int loopsong = -1;
    AlertDialog alertDialogbrithness, alertDialogvolume;

    int videowidth, videoheight;
    String mFilename;
    private VideoPlayerState videoPlayerState = new VideoPlayerState();
    CustomVideoView videoView;

    TextView textViewLeft, textViewRight;

    private AudioManager audio;

    SeekBar seekbarvideo;
    LinearLayout bottumlaout, linearlayouttop, unlock, rotationlayout;

    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;

    ImageButton btnback;
    ImageButton screenlock;
    ImageButton rotationscreen;
    ImageButton addfuncality;

    TextView titlename;

    ImageButton btnpuaseplay;
    ImageButton btnnext;
    ImageButton btnprevise;
    ImageButton btnforword;
    ImageButton btnbackword;
    ImageButton btnplaervolume;
    ImageButton btnplayerbrithness;
    ImageButton btnzoominout;
    ImageView img_rotate, img_rotate_landscap;
    ImageButton btnaudiotrack;
    ImageButton unscreenlock;

    TextView songname;
    View volumelayout;
    LayoutInflater inflater;
    View view;

    Cursor videocursor;
    String strVideofilename;
    int currentpostion;

    int videosongcount;
    private GestureDetector gd;
    private GestureDetection2 detector;
    private GestureDetector mGestureDetector;
    private int currentPosition;
    private int volume_level;
    TextView txtScalView, txtbrighness;
    private VerticleSeekbar seekVolume, seekbrightness;
    public Dialog dialog, dialog_filelist;
    private boolean mCanControlVolume;
    private ProgressBar mCenterProgress;
    private float mCurBrightness;
    private int mCurVolume;
    private AudioManager mAudioManager;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mMaxVolume;
    boolean screenbLockFlag = false;
    String str_check_music = "1";
    String str_check_zoominout = "1";
    RelativeLayout adViewContainer;

    @SuppressLint({"NewApi", "WrongConstant", "ClickableViewAccessibility"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_playing);
        switch (((WindowManager) getSystemService("window")).getDefaultDisplay().getOrientation()) {
            case 1:
                setRequestedOrientation(0);
                break;
            case 2:
                setRequestedOrientation(1);
                break;
        }
        final Context context = this;
        this.mAudioManager = (AudioManager) this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.mMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
        bottumlaout = (LinearLayout) findViewById(R.id.linearlayoutbottm);
        linearlayouttop = (LinearLayout) findViewById(R.id.linearlayouttop);
        seekVolume = (VerticleSeekbar) findViewById(R.id.seekVolume);
        img_rotate = (ImageView) findViewById(R.id.img_rotation);
        img_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rotation = ((WindowManager) getSystemService("window")).getDefaultDisplay().getRotation();
                if (rotation == 1 || rotation == 3) {
                    setRequestedOrientation(1);
                    return;
                } else {
                    setRequestedOrientation(0);
                    return;
                }

            }
        });
        seekbrightness = (VerticleSeekbar) findViewById(R.id.seekbrightness);
        seekVolume.setClickable(false);
        seekbrightness.setClickable(false);

        seekVolume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        seekbrightness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        bottumlaout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottumlaout.setVisibility(View.VISIBLE);
            }
        });

        adViewContainer = (RelativeLayout) findViewById(R.id.adView);


        linearlayouttop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayouttop.setVisibility(View.VISIBLE);
            }
        });
        String[] parameters = new String[]{"_id", "_data", "_display_name", "_size", "duration", "date_added"};
        String[] selectionArgs = new String[]{"%" + Show_Data.albumname + "%"};
        this.videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, parameters, "_data like?", selectionArgs, "datetaken DESC");

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri selectedImageUri = getIntent().getData();
            System.out.println("Path-->" + selectedImageUri);

            String ss = getRealPath.getPath(getApplicationContext(), selectedImageUri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(ss));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            long timeInMillisec = Long.parseLong(time);
            Util.albumname = album;
        } else {
            currentpostion = getIntent().getExtras().getInt("ITEM_POSITION");
            this.strVideofilename = getIntent().getExtras().getString("FOLDER_ITEMS");
        }
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        videosongcount = videocursor.getCount();
        txtScalView = (TextView) findViewById(R.id.txtScalView);
        txtbrighness = (TextView) findViewById(R.id.txtbrighness);
        txtScalView.setVisibility(View.GONE);
        txtScalView.postDelayed(onEverySecond2, 2000);
        String[] columns = this.videocursor.getColumnNames();
        int name = this.videocursor.getColumnIndex("_data");
        videocursor.moveToPosition(currentpostion);
        this.mFilename = this.strVideofilename;

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri selectedImageUri = getIntent().getData();
            System.out.println("Path-->" + selectedImageUri);

            String ss = getRealPath.getPath(getApplicationContext(), selectedImageUri);

            System.out.println("ss-->" + ss);
            mFilename = ss;

            System.out.println("DDDD---" + mFilename);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(mFilename));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            long timeInMillisec = Long.parseLong(time);
            Util.videoduration = TimeUtils.toFormattedTime((int) timeInMillisec);
        }
        videoPlayerState.setFilename(mFilename);

        seekbarvideo = (SeekBar) findViewById(R.id.seekBar1);
        songname = (TextView) findViewById(R.id.songname);
        btnzoominout = (ImageButton) findViewById(R.id.zoominout);

        File setfilename = new File(mFilename);
        songname.setText(setfilename.getName().toString());

        unscreenlock = (ImageButton) findViewById(R.id.unsreecnlock);

        videoView = (CustomVideoView) findViewById(R.id.video_view);
        call();
        this.mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
        this.mGestureDetector = new GestureDetector(this, new MySimpleOnGestureListener());

        this.videoView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                VideotemppPlayingActivity.this.mGestureDetector.onTouchEvent(event);
                VideotemppPlayingActivity.this.mScaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });


        btnpuaseplay = (ImageButton) findViewById(R.id.puaseplay);
        btnpuaseplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog_native.show();

                if (videoView.isPlaying()) {
                    if (videoView != null) {
                        adViewContainer.setVisibility(View.VISIBLE);
                        Util.stopsongstop = 1;
                        Glob.current = videoView.getCurrentPosition();
                        Glob.check = true;
                        videoView.pause();
                        seekbarvideo.postDelayed(onEverySecond, 1000);
                        BitmapDrawable bg = (BitmapDrawable) getResources()
                                .getDrawable(R.drawable.play);
                        btnpuaseplay.setBackgroundDrawable(bg);
                    }
                } else {
                    if (videoView != null) {
                        adViewContainer.setVisibility(View.GONE);
                        Util.stopsongstop = 0;
                        videoView.start();
                        seekbarvideo.postDelayed(onEverySecond, 1000);
                        BitmapDrawable bg = (BitmapDrawable) getResources()
                                .getDrawable(R.drawable.ic_stop);
                        btnpuaseplay.setBackgroundDrawable(bg);
                    }
                }
            }
        });


        btnnext = (ImageButton) findViewById(R.id.btnnext);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

                    int currentPosition = videoView.getCurrentPosition();
                    currentPosition = videoView.getCurrentPosition() + 3000;
                    videoView.seekTo(currentPosition);

                    if (!videoView.isPlaying()) {
                        seekbarvideo.setProgress(videoView.getCurrentPosition());
                        textViewLeft.setText(getTimeForTrackFormat(
                                videoView.getCurrentPosition(), true));
                    }
                } else {
                    btnnext();
                }
            }
        });

        btnprevise = (ImageButton) findViewById(R.id.btnprevise);
        btnprevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

                    int currentPosition = videoView.getCurrentPosition();
                    currentPosition = videoView.getCurrentPosition() - 3000;
                    videoView.seekTo(currentPosition);

                    if (!videoView.isPlaying()) {
                        seekbarvideo.setProgress(videoView.getCurrentPosition());
                        textViewLeft.setText(getTimeForTrackFormat(
                                videoView.getCurrentPosition(), true));
                    }

                } else {
                    btnPrevious();
                }
            }
        });

        btnplayerbrithness = (ImageButton) findViewById(R.id.btnplayerbrithness);
        btnplayerbrithness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean settingsCanWrite = Settings.System.canWrite(context);

                if (!settingsCanWrite) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                }
                dialog_brightness = new Dialog(VideotemppPlayingActivity.this);
                Window window = dialog_brightness.getWindow();
                dialog_brightness.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_brightness.setContentView(R.layout.brightnessnew);
                dialog_brightness.setCanceledOnTouchOutside(true);
                dialog_brightness.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                SeekBar sb = (SeekBar) window.findViewById(R.id.seekBar12);

                float curBrightnessValue = 0;
                try {
                    curBrightnessValue = Settings.System
                            .getInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                int screen_brightness = (int) curBrightnessValue;
                sb.setProgress(screen_brightness);
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Settings.System
                                .putInt(getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS,
                                        progress);
                        dialog_brightness.dismiss();
                    }
                });
                dialog_brightness.show();


            }
        });

        btnback = (ImageButton) findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnzoominout = (ImageButton) findViewById(R.id.zoominout);
        btnzoominout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_zoominout = new Dialog(VideotemppPlayingActivity.this);
                Window window = dialog_zoominout.getWindow();
                dialog_zoominout.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_zoominout.setContentView(R.layout.zoominout);
                dialog_zoominout.setCanceledOnTouchOutside(true);
                dialog_zoominout.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                RadioButton r_original = (RadioButton) window.findViewById(R.id.r_original);
                RadioButton r_25 = (RadioButton) window.findViewById(R.id.r_25);
                RadioButton r_50 = (RadioButton) window.findViewById(R.id.r_50);
                RadioButton r_75 = (RadioButton) window.findViewById(R.id.r_75);
                RadioButton r_100 = (RadioButton) window.findViewById(R.id.r_100);
                r_original.setChecked(false);
                r_25.setChecked(false);
                r_50.setChecked(false);
                r_75.setChecked(false);
                r_100.setChecked(false);
                if (str_check_zoominout.equals("1")) {
                    r_original.setChecked(true);
                } else if (str_check_zoominout.equals("2")) {
                    r_25.setChecked(true);
                } else if (str_check_zoominout.equals("3")) {
                    r_50.setChecked(true);
                } else if (str_check_zoominout.equals("4")) {
                    r_75.setChecked(true);
                } else if (str_check_zoominout.equals("5")) {
                    r_100.setChecked(true);
                }
                r_original.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_zoominout = "1";
                        if (Util.screenrotationobject == 1) {
                            Util.rotationscreen = 1;
                            call();
                            dialog_zoominout.dismiss();
                            zoominoutpostion = 0;
                        } else {
                            Util.rotationscreen = 0;
                            call();
                            dialog_zoominout.dismiss();
                            zoominoutpostion = 0;
                        }
                    }
                });
                r_25.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_zoominout = "2";
                        int screenwidth = 350;
                        int screenheight = 350;
                        videoView.setFixedVideoSize(screenwidth, screenheight);
                        dialog_zoominout.dismiss();
                        zoominoutpostion = 1;
                    }
                });
                r_50.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_zoominout = "3";
                        fiftinecall();
                        dialog_zoominout.dismiss();
                        zoominoutpostion = 2;
                    }
                });
                r_75.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_zoominout = "4";
                        seventiyfivecall();
                        dialog_zoominout.dismiss();
                        zoominoutpostion = 3;
                    }
                });
                r_100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_zoominout = "5";
                        hadredcall();
                        dialog_zoominout.dismiss();
                        zoominoutpostion = 4;
                    }
                });

                dialog_zoominout.show();
            }
        });


        ImageButton audiotrack = (ImageButton) findViewById(R.id.btnaudio);
        audiotrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(VideotemppPlayingActivity.this);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.audiodialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                final RadioButton audiotrackradio = (RadioButton) dialog.findViewById(R.id.audiotrack);
                final RadioButton disable = (RadioButton) dialog.findViewById(R.id.disable);

                audiotrackradio.setChecked(false);
                disable.setChecked(false);
                if (str_check_music.equals("1")) {
                    audiotrackradio.setChecked(true);
                } else {
                    disable.setChecked(true);
                }
                audiotrackradio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_check_music = "1";
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        am.setStreamMute(AudioManager.STREAM_MUSIC,
                                false);
                        dialog.dismiss();
                    }
                });
                disable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        str_check_music = "2";
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        am.setStreamMute(AudioManager.STREAM_MUSIC,
                                true);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        ImageButton propeties = (ImageButton) findViewById(R.id.btnpro);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            propeties.setVisibility(View.INVISIBLE);
        } else {
            propeties.setVisibility(View.VISIBLE);
        }

        propeties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] columns = videocursor.getColumnNames();
                int name = videocursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA);
                videocursor.moveToPosition(currentpostion);
                String mFilenamepro = videocursor.getString(name);

                File filename = new File(mFilenamepro);
                String file = filename.getName();
                String parent = filename.getParent();

                double bytes = filename.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                double gigabytes = (megabytes / 1024);
                double terabytes = (gigabytes / 1024);
                double petabytes = (terabytes / 1024);
                double exabytes = (petabytes / 1024);
                double zettabytes = (exabytes / 1024);
                double yottabytes = (zettabytes / 1024);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                dialog_filelist = new Dialog(VideotemppPlayingActivity.this);
                Window window = dialog_filelist.getWindow();
                dialog_filelist.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_filelist.setContentView(R.layout.filedetails);
                dialog_filelist.setCanceledOnTouchOutside(true);
                dialog_filelist.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView t_filename = (TextView) window.findViewById(R.id.t_showfilename);
                TextView t_loaction = (TextView) window.findViewById(R.id.t_showlocation);
                TextView t_size = (TextView) window.findViewById(R.id.t_showsize);
                TextView t_date = (TextView) window.findViewById(R.id.t_showdate);
                TextView t_resolution = (TextView) window.findViewById(R.id.t_showresolution);
                t_filename.setText(file);
                t_loaction.setText(parent);
                t_date.setText(sdf.format(filename.lastModified()) + "");
                t_size.setText(Math.round(megabytes * 100.0) / 100.0 + " MB");
                t_resolution.setText(videowidth + "*" + videoheight);
                dialog_filelist.show();

            }
        });


        screenlock = (ImageButton) findViewById(R.id.screenlock);
        screenlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slide_down = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.slide_down);

                bottumlaout.startAnimation(slide_down);

                int visibilityone = bottumlaout.getVisibility();

                if (visibilityone == View.VISIBLE) {
                    bottumlaout.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    bottumlaout.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }

                Animation slide_up = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.top_slide_up);

                linearlayouttop.startAnimation(slide_up);

                int visibilitytwo = linearlayouttop.getVisibility();

                if (visibilitytwo == View.VISIBLE) {
                    linearlayouttop.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    linearlayouttop.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }

                unscreenlock.setVisibility(View.VISIBLE);

                Util.unhidealllayout = 55;
            }
        });

        unscreenlock = (ImageButton) findViewById(R.id.unsreecnlock);
        unscreenlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unscreenlock.setVisibility(View.GONE);

                Animation slide_up = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.slide_up);
                bottumlaout.startAnimation(slide_up);

                int visibilityone = bottumlaout.getVisibility();

                if (visibilityone == View.VISIBLE) {
                    bottumlaout.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    bottumlaout.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }

                Animation slide_down = AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.top_slide_down);

                linearlayouttop.startAnimation(slide_down);

                int visibilitytwo = linearlayouttop.getVisibility();

                if (visibilitytwo == View.VISIBLE) {
                    linearlayouttop.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    linearlayouttop.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }
                Util.unhidealllayout = 0;
                Util.slideshow = 1;
                adViewContainer.setVisibility(View.GONE);
                Util.stopsongstop = 0;
                videoView.start();
                seekbarvideo.postDelayed(onEverySecond, 1000);
                BitmapDrawable bg = (BitmapDrawable) getResources()
                        .getDrawable(R.drawable.ic_stop);
                btnpuaseplay.setBackgroundDrawable(bg);
            }
        });


        textViewLeft = (TextView) findViewById(R.id.start);
        textViewRight = (TextView) findViewById(R.id.end);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videowidth = mp.getVideoWidth();
                    videoheight = mp.getVideoHeight();

                    Util.slideshow = 1;
                    long timeInMillis = System.currentTimeMillis();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTimeInMillis(timeInMillis);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    String gettime = dateFormat.format(cal1.getTime());


                    if (!Glob.check) {

                        videoView.start();
                    }
                    System.out.println("FFFFFFFFF");
                }
                seekbarvideo.setMax(videoView.getDuration());
                textViewRight.setText(getTimeForTrackFormat(
                        videoView.getDuration(), true));
                seekbarvideo.postDelayed(onEverySecond, 1000);
            }
        });

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri selectedImageUri = getIntent().getData();
            System.out.println("Path-->" + selectedImageUri);

            String ss = getRealPath.getPath(getApplicationContext(), selectedImageUri);

            System.out.println("ss-->" + ss);

            videoView.setVideoURI(Uri.parse(ss));
        } else {

            System.out.println("DD-->" + videoPlayerState.getFilename());

            videoView.setVideoPath(videoPlayerState.getFilename());
        }

        videoView.setOnTouchListener(new OnScrollTouchListenerControl(this, this));

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayouttop.setVisibility(View.GONE);

                if (Util.unhidealllayout == 55) {

                } else {
                    hideshowLayout();
                }
            }
        });

        seekbarvideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }
        });
    }

    private void hideshowLayout() {

        if (Util.slideshow == 0) {
            Animation slide_up = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.slide_up);
            bottumlaout.startAnimation(slide_up);

            int visibilityone = bottumlaout.getVisibility();

            if (visibilityone == View.VISIBLE) {
                bottumlaout.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
            } else {
                bottumlaout.setVisibility(View.VISIBLE);
                img_rotate.setVisibility(View.VISIBLE);
            }

            Animation slide_down = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.top_slide_down);

            linearlayouttop.startAnimation(slide_down);

            int visibilitytwo = linearlayouttop.getVisibility();

            if (visibilitytwo == View.VISIBLE) {
                linearlayouttop.setVisibility(View.GONE);
            } else {
                linearlayouttop.setVisibility(View.VISIBLE);
            }

            long timeInMillis = System.currentTimeMillis();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timeInMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "hh:mm a");
            String gettime = dateFormat.format(cal1.getTime());


            Util.slideshow = 1;
        } else {
            Animation slide_down = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.slide_down);
            bottumlaout.startAnimation(slide_down);

            int visibilityone = bottumlaout.getVisibility();

            if (visibilityone == View.VISIBLE) {
                bottumlaout.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
            } else {
                bottumlaout.setVisibility(View.VISIBLE);
                img_rotate.setVisibility(View.VISIBLE);
            }

            Animation slide_up = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.top_slide_up);

            linearlayouttop.startAnimation(slide_up);

            int visibilitytwo = linearlayouttop.getVisibility();

            if (visibilitytwo == View.VISIBLE) {
                linearlayouttop.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
            } else {
                linearlayouttop.setVisibility(View.VISIBLE);
                img_rotate.setVisibility(View.VISIBLE);
            }

            long timeInMillis = System.currentTimeMillis();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(timeInMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "hh:mm a");
            String gettime = dateFormat.format(cal1.getTime());

            Util.slideshow = 0;
        }
    }

    public void fiftinecall() {
        DisplayMetrics dm1;
        dm1 = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm1);
        int width1 = dm1.widthPixels;

        videoView.setFixedVideoSize(width1, 500);
    }

    public void seventiyfivecall() {
        DisplayMetrics dm1;
        dm1 = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm1);
        int width1 = dm1.widthPixels;

        videoView.setFixedVideoSize(width1, 800);
    }

    public void hadredcall() {
        DisplayMetrics dm1;
        dm1 = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm1);
        int width1 = dm1.widthPixels;

        videoView.setFixedVideoSize(width1, 1500);
    }

    public void call() {
        if (Util.rotationscreen == 1) {
            DisplayMetrics dm;
            dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;

            videoView.setFixedVideoSize(width, height - 500);
        } else {
            DisplayMetrics dm;
            dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;

            videoView.setFixedVideoSize(width, height);
        }
    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (seekbarvideo != null) {
                seekbarvideo.setProgress(videoView.getCurrentPosition());
                textViewLeft.setText(getTimeForTrackFormat(
                        videoView.getCurrentPosition(), true));
            }

            if (videoView.isPlaying()) {
                seekbarvideo.postDelayed(onEverySecond, 1000);
            } else {
                if (Util.stopsongstop == 1) {

                } else {
                    videoView.seekTo(0);
                    seekbarvideo.postDelayed(onEverySecond, 1000);
                }
            }
            seekVolume.setVisibility(View.GONE);
            txtScalView.setVisibility(View.GONE);
            seekbrightness.setVisibility(View.GONE);

        }
    };
    private Runnable onEverySecond2 = new Runnable() {
        @Override
        public void run() {

            seekVolume.setVisibility(View.GONE);
            seekbrightness.setVisibility(View.GONE);

            if (txtScalView.getVisibility() == View.VISIBLE) {

                txtScalView.setVisibility(View.GONE);
                seekVolume.setVisibility(View.GONE);
                seekbrightness.setVisibility(View.GONE);

            }
            if (seekVolume.getVisibility() == View.VISIBLE) {
                seekVolume.setVisibility(View.GONE);
            }
            if (seekbrightness.getVisibility() == View.VISIBLE) {
                seekbrightness.setVisibility(View.GONE);
            }
        }
    };

    public static String getTimeForTrackFormat(int timeInMills,
                                               boolean display2DigitsInMinsSection) {
        int minutes = (timeInMills / (60 * 1000));
        int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
        String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
        result += minutes + ":";
        if (seconds < 10) {
            result += "0" + seconds;
        } else {
            result += seconds;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Glob.current = 0;


    }

    public void onVerticalScroll(float percent, int direction) {
        if (direction == 1) {
            updateBrightness(percent);
        } else {
            updateVolume(percent);
        }
    }

    private void updateBrightness(float percent) {
        if (this.mCurBrightness == -1.0f) {
            this.mCurBrightness = this.getWindow().getAttributes().screenBrightness;
            if (this.mCurBrightness <= 0.01f) {
                this.mCurBrightness = 0.01f;
            }
        }
        this.seekbrightness.setVisibility(View.VISIBLE);
        this.txtbrighness.setVisibility(View.VISIBLE);
        this.txtScalView.setVisibility(View.GONE);
        WindowManager.LayoutParams attributes = this.getWindow().getAttributes();
        attributes.screenBrightness = this.mCurBrightness + percent;
        if (attributes.screenBrightness >= 1f) {
            attributes.screenBrightness = 1f;
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        this.getWindow().setAttributes(attributes);
        float p = attributes.screenBrightness * 100.0f;
        this.mCenterProgress.setProgress((int) p);
        this.seekbrightness.setProgress((int) p);
        this.txtbrighness.setText(String.valueOf((int) p));
    }

    private void updateVolume(float percent) {
        this.seekbrightness.setVisibility(View.GONE);
        this.txtbrighness.setVisibility(View.GONE);
        this.seekVolume.setVisibility(View.VISIBLE);
        this.txtScalView.setVisibility(View.VISIBLE);
        if (this.mCurVolume == -1) {
            this.mCurVolume = this.mAudioManager.getStreamVolume(3);
            if (this.mCurVolume < 0) {
                this.mCurVolume = 0;
            }
        }
        int volume = ((int) (((float) this.mMaxVolume) * percent)) + this.mCurVolume;
        if (volume > this.mMaxVolume) {
            volume = this.mMaxVolume;
        }
        if (volume < 0) {
            volume = 0;
        }
        this.mAudioManager.setStreamVolume(3, volume, 0);
        int progress = (volume * 100) / this.mMaxVolume;
        this.seekVolume.setProgress(progress);
        this.mCenterProgress.setProgress(progress);
        this.txtScalView.setText(String.valueOf(progress));
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {

            System.out.println("DDD");

            return gestureDetector.onTouchEvent(motionEvent);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            private static final String TAG = "DD";

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    float deltaX = e1.getRawX() - e2.getRawX();
                    float deltaY = e1.getRawY() - e2.getRawY();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {

                                System.out.println("Right x=" + diffX);
                                System.out.println("Right y=" + diffY);

                                onSwipeRight();
                            } else {

                                System.out.println("Left x=" + diffX);
                                System.out.println("Left y=" + diffY);
                                onSwipeLeft();
                            }
                        }
                    } else if (((double) Math.abs(deltaY)) > 60.0d) {
                        if (((double) e1.getX()) < ((double) VideotemppPlayingActivity.getDeviceWidth(VideotemppPlayingActivity.this)) * 0.5d) {
                            VideotemppPlayingActivity.this.onVerticalScroll(deltaY / ((float) VideotemppPlayingActivity.getDeviceHeight(VideotemppPlayingActivity.this)), 1);
                        } else if (((double) e1.getX()) > ((double) VideotemppPlayingActivity.getDeviceWidth(VideotemppPlayingActivity.this)) * 0.5d) {
                            VideotemppPlayingActivity.this.onVerticalScroll(deltaY / ((float) VideotemppPlayingActivity.getDeviceHeight(VideotemppPlayingActivity.this)), 2);
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                System.out.println("Bottom x=" + diffX);
                                System.out.println("Bottom y=" + diffY);
                                onSwipeBottom();
                            } else {
                                System.out.println("Top x=" + diffX);
                                System.out.println("Top y=" + diffY);
                                onSwipeTop();
                            }
                        }
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {

        }

        public void onSwipeLeft() {

        }

        public void onSwipeTop() {

        }

        public void onSwipeBottom() {

        }

    }

    public void btnPrevious() {

        if (currentpostion > 0) {

            String[] columns = VideotemppPlayingActivity.this.videocursor.getColumnNames();
            int name = VideotemppPlayingActivity.this.videocursor.getColumnIndex("_data");
            VideotemppPlayingActivity.this.videocursor.moveToPosition(VideotemppPlayingActivity.this.currentpostion - 1);
            String filename = VideotemppPlayingActivity.this.videocursor.getString(name);
            videoPlayerState.setFilename(filename);

            File setfilename1 = new File(filename);
            songname.setText(setfilename1.getName().toString());

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    } else {
                        videoView.start();
                    }

                    call();
                    seekbarvideo.setMax(videoView.getDuration());
                    textViewRight
                            .setText(getTimeForTrackFormat(
                                    videoView.getDuration(),
                                    true));
                    seekbarvideo.postDelayed(onEverySecond,
                            1000);
                }
            });

            videoView.setVideoPath(videoPlayerState.getFilename());
//                    videoView.setVideoPath(mFilename);
            currentpostion = currentpostion - 1;
        } else {

            String[] columns = VideotemppPlayingActivity.this.videocursor.getColumnNames();
            int name = VideotemppPlayingActivity.this.videocursor.getColumnIndex("_data");
            VideotemppPlayingActivity.this.videocursor.moveToPosition(VideotemppPlayingActivity.this.videosongcount - 1);
            String filename = VideotemppPlayingActivity.this.videocursor.getString(name);

            videoPlayerState.setFilename(filename);

            File setfilename1 = new File(filename);
            songname.setText(setfilename1.getName().toString());

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    } else {
                        videoView.start();
                    }

                    call();

                    seekbarvideo.setMax(videoView.getDuration());
                    textViewRight
                            .setText(getTimeForTrackFormat(
                                    videoView.getDuration(),
                                    true));
                    seekbarvideo.postDelayed(onEverySecond,
                            1000);
                }
            });

            videoView.setVideoPath(videoPlayerState.getFilename());
            currentpostion = videosongcount - 1;
        }
    }

    int current = 0;

    public void btnnext() {

        String filename = VideoListActivity.videoList.get(currentpostion).getFileName();
        VideotemppPlayingActivity.this.aa = false;
        if (VideotemppPlayingActivity.this.current + 1 == VideoListActivity.videoList.size()) {
            VideotemppPlayingActivity.this.current = 0;
        } else {
            VideotemppPlayingActivity videoViewActivity = VideotemppPlayingActivity.this;
            videoViewActivity.current++;
        }
        videoPlayerState.setFilename(filename);

        File setfilename1 = new File(filename);
        songname.setText(setfilename1.getName().toString());

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }

                call();
                seekbarvideo.setMax(videoView.getDuration());
                textViewRight
                        .setText(getTimeForTrackFormat(
                                videoView.getDuration(),
                                true));
                seekbarvideo.postDelayed(onEverySecond,
                        1000);
            }
        });

        videoView.setVideoPath(videoPlayerState.getFilename());
        currentpostion = currentpostion + 1;
    }

    @Override
    public void leftSwipe() {
        new AsyncSlow().execute();
    }

    public void testPause(int current) {

        seekbarvideo.setProgress(current);
        textViewLeft.setText(getTimeForTrackFormat(current, true));
    }

    @Override
    public void rightSwipe() {
        new AsyncFast().execute();
    }

    @Override
    public void upSwipe() {
        volume_level = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level + 1, 0);
        new MyAsyncTask().execute();

    }

    @Override
    public void downSwipe() {
        volume_level = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level - 1, 0);
        new MyAsyncTask1().execute();
    }

    @Override
    public void onsingleClick() {

        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDD");
        if (Util.unhidealllayout == 55) {
        } else {
            hideshowLayout();
            txtScalView.setVisibility(View.GONE);
            seekVolume.setVisibility(View.GONE);
            seekbrightness.setVisibility(View.GONE);
        }
    }

    private class AsyncFast extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentPosition = videoView.getCurrentPosition();
            txtScalView.setVisibility(View.VISIBLE);
            currentPosition = videoView.getCurrentPosition() + (videoView.getDuration() / 200);
            videoView.seekTo(currentPosition);
            txtScalView.setText(" " + getTimeForTrackFormat(
                    currentPosition, true) + "\n+" + (float) (videoView.getDuration() / 200) / 1000);

            if (!videoView.isPlaying()) {
                seekbarvideo.setProgress(currentPosition);
                textViewLeft.setText(getTimeForTrackFormat(
                        currentPosition, true));
                txtScalView.setText(" " + getTimeForTrackFormat(
                        currentPosition, true) + "\n+" + (float) (videoView.getDuration() / 200) / 1000);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtScalView.setVisibility(View.GONE);
        }
    }

    private class AsyncSlow extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentPosition = videoView.getCurrentPosition();
            txtScalView.setVisibility(View.VISIBLE);
            currentPosition = videoView.getCurrentPosition() - (videoView.getDuration() / 200);
            videoView.seekTo(currentPosition);
            txtScalView.setText(" " + getTimeForTrackFormat(
                    currentPosition, true) + "\n-" + (float) (videoView.getDuration() / 200) / 1000);

            if (!videoView.isPlaying()) {
                seekbarvideo.setProgress(currentPosition);
                textViewLeft.setText(getTimeForTrackFormat(
                        currentPosition, true));
                txtScalView.setText(" " + getTimeForTrackFormat(
                        currentPosition, true) + "\n+" + (float) (videoView.getDuration() / 200) / 1000);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            txtScalView.setVisibility(View.GONE);
        }
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mH;
        private int mW;

        private MyScaleGestureListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            if (!VideotemppPlayingActivity.this.screenbLockFlag) {
                this.mW = (int) (((float) this.mW) * detector.getScaleFactor());
                this.mH = (int) (((float) this.mH) * detector.getScaleFactor());
                if (this.mW < 100) {
                    this.mW = VideotemppPlayingActivity.this.videoView.getWidth();
                    this.mH = VideotemppPlayingActivity.this.videoView.getHeight();
                }
                Log.d("onScale", "scale=" + detector.getScaleFactor() + ", w=" + this.mW + ", h=" + this.mH);
                VideotemppPlayingActivity.this.videoView.setFixedVideoSize(this.mW, this.mH);
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            this.mW = VideotemppPlayingActivity.this.videoView.getWidth();
            this.mH = VideotemppPlayingActivity.this.videoView.getHeight();
            Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() + ", w=" + this.mW + ", h=" + this.mH);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w=" + this.mW + ", h=" + this.mH);
        }
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private MySimpleOnGestureListener() {
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            VideotemppPlayingActivity.this.mCurVolume = -1;
            VideotemppPlayingActivity.this.mCurBrightness = -1.0f;
            VideotemppPlayingActivity.this.seekVolume.setVisibility(View.GONE);
            VideotemppPlayingActivity.this.txtScalView.setVisibility(View.GONE);
            VideotemppPlayingActivity.this.seekbrightness.setVisibility(View.GONE);
            VideotemppPlayingActivity.this.txtbrighness.setVisibility(View.GONE);
            //   VideoPlayingActivity.this.textScreenSize.setVisibility(8);
            if (Show_Data.slideshow == 0) {
                VideotemppPlayingActivity.this.bottumlaout.startAnimation(AnimationUtils.loadAnimation(VideotemppPlayingActivity.this.getApplicationContext(), R.anim.translate_animation));
                if (VideotemppPlayingActivity.this.bottumlaout.getVisibility() == View.VISIBLE) {
                    VideotemppPlayingActivity.this.bottumlaout.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    VideotemppPlayingActivity.this.bottumlaout.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }
                VideotemppPlayingActivity.this.linearlayouttop.startAnimation(AnimationUtils.loadAnimation(VideotemppPlayingActivity.this.getApplicationContext(), R.anim.top_slide_animation));
                if (VideotemppPlayingActivity.this.linearlayouttop.getVisibility() == View.VISIBLE) {
                    VideotemppPlayingActivity.this.linearlayouttop.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    VideotemppPlayingActivity.this.linearlayouttop.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }
                Show_Data.slideshow = 1;
            } else {
                VideotemppPlayingActivity.this.bottumlaout.startAnimation(AnimationUtils.loadAnimation(VideotemppPlayingActivity.this.getApplicationContext(), R.anim.side_di_animation));
                if (VideotemppPlayingActivity.this.bottumlaout.getVisibility() == View.VISIBLE) {
                    VideotemppPlayingActivity.this.bottumlaout.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    VideotemppPlayingActivity.this.bottumlaout.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }
                VideotemppPlayingActivity.this.linearlayouttop.startAnimation(AnimationUtils.loadAnimation(VideotemppPlayingActivity.this.getApplicationContext(), R.anim.top_slide_up_animation));
                if (VideotemppPlayingActivity.this.linearlayouttop.getVisibility() == View.VISIBLE) {
                    VideotemppPlayingActivity.this.linearlayouttop.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                } else {
                    VideotemppPlayingActivity.this.linearlayouttop.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                }
                Show_Data.slideshow = 0;
            }
            return true;
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            volume_level = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            txtScalView.setVisibility(View.VISIBLE);
            seekVolume.setVisibility(View.VISIBLE);
            txtScalView.setText("" + volume_level);
            seekVolume.setProgress(volume_level);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            txtScalView.setVisibility(View.GONE);
            seekVolume.setVisibility(View.GONE);

        }

    }

    private class MyAsyncTask1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            volume_level = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            txtScalView.setText("" + volume_level);
            txtScalView.setVisibility(View.VISIBLE);
            seekVolume.setVisibility(View.VISIBLE);
            seekVolume.setProgress(volume_level);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            txtScalView.setVisibility(View.GONE);
            seekVolume.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Glob.check) {
            videoView.pause();
            System.out.println("Value-" + Glob.current);
            videoView.seekTo(Glob.current);
            System.out.println("Resume");

            if (!videoView.isPlaying()) {
                BitmapDrawable bg = (BitmapDrawable) getResources()
                        .getDrawable(R.drawable.play);
                btnpuaseplay.setBackgroundDrawable(bg);
            } else {

                BitmapDrawable bg = (BitmapDrawable) getResources()
                        .getDrawable(R.drawable.ic_stop);
                btnpuaseplay.setBackgroundDrawable(bg);
            }
        }
    }

}


