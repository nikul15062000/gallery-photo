package com.example.mygallery.foldersvideolist.videoplay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.TrackInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.IdRes;
import androidx.core.app.NotificationManagerCompat;

import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.FileDataHelper;
import com.example.mygallery.foldersvideolist.MediaFile;
import com.example.mygallery.videoview.GestureDetection2;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoViewActivity extends Activity implements GestureDetection2.SimpleGestureListener {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static int oneTimeOnly = 0;
    static int vari = 0;
    boolean aa = true;
    List<Integer> audioTracksIndexes;
    List<String> audioTracksList;
    private SeekBar brightbar;
    private int brightness;
    private ContentResolver cResolver;
    private GoogleApiClient client;
    Context context;
    int current;
    int currentPosition;
    TextView current_position;
    private Handler durationHandler = new Handler();
    Bundle extras;
    ImageView forward_btn;
    GestureDetector gestureDetector;
    private Runnable hideScreenControllsRunnable = new Runnable() {
        public void run() {
            if (music_controls.getVisibility() == View.VISIBLE) {
                music_controls.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
            }
            if (video_header_controls.getVisibility() == View.VISIBLE) {
                video_header_controls.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
            }
             getWindow().setFlags(1024, 1024);
            VideoViewActivity.vari = 1;
            music_controls.removeCallbacks(hideScreenControllsRunnable);
        }
    };
    private Runnable horizontalScrollRunnable = new Runnable() {
        public void run() {
            if (Long.valueOf(System.currentTimeMillis()).longValue() >= lastSeekUpdateTime.longValue() + 1000) {
                Log.e("Scroll", "Stopped");
                mAudioManager.setStreamMute(3, false);
                scroll_position.setVisibility(View.GONE);
                if (videoview.isPlaying()) {
                    music_controls.setVisibility(View.GONE);
                }
                videoview.removeCallbacks(horizontalScrollRunnable);
                return;
            }
            videoview.postDelayed(horizontalScrollRunnable, 1000);
        }
    };
    ImageView hundred_screensize;

    boolean isAdLoaded = false;
    boolean isPlaying = false;
    ImageView ivg;
    ImageView land,img_rotate;
    Long lastSeekUpdateTime = null;
    Long lastVolumeUpdateTime = null;
    LinearLayout laylock;
    SeekBar left_press;
    ImageView lock;
    boolean lock_click = false;
    ImageView locked;
    private AudioManager mAudioManager;
    private Activity mContext;
    private float mCurBrightness = -1.0f;
    private int mCurVolume = -1;
    Handler mHandlerss;
    private int mMaxVolume;
    MediaPlayer mediaPlayer;
    View music_controls;
    ImageView open_pop_up_video;
    ImageView pause_btn;
    ImageView play_btn;
    ImageButton playbutton;
    boolean played = false;
    ImageView portrat;
    View rel_bar;
    TextView remainingDurationTv;
    ImageView rewind_btn;
    SeekBar right_press;
    TextView screen_sizes;
    TextView scroll_position;
    SeekBar seekbar_vplay;
    int selectedAudioTrack;
    SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        @SuppressLint({"NewApi"})
        @TargetApi(11)
        public boolean onDoubleTap(MotionEvent e) {
            if (videoview.isPlaying()) {
                music_controls.setVisibility(View.GONE);
                video_header_controls.setVisibility(View.GONE);
                 getWindow().setFlags(1024, 1024);
                VideoViewActivity.vari = 1;
            }
            return true;
        }

        public boolean isFullScreen() {
            if ((getWindow().getAttributes().flags & 1024) == 1024) {
                 getWindow().setFlags(1024, 1024);
                return true;
            }
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            music_controls.removeCallbacks(hideScreenControllsRunnable);
             getWindow().setFlags(1024, 1024);
            if (!lock_click) {
                if (music_controls.getVisibility() == View.GONE) {
                    music_controls.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                } else {
                    scroll_position.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                    music_controls.setVisibility(View.GONE);
                }
                if (video_header_controls.getVisibility() == View.GONE) {
                    video_header_controls.setVisibility(View.VISIBLE);
                    img_rotate.setVisibility(View.VISIBLE);
                } else {
                    scroll_position.setVisibility(View.GONE);
                    img_rotate.setVisibility(View.GONE);
                    video_header_controls.setVisibility(View.GONE);
                }
                music_controls.postDelayed(hideScreenControllsRunnable, 5000);
                if (isFullScreen()) {
                     getWindow().setFlags(1024, 1024);
                    VideoViewActivity.vari = 0;
                } else {
                     getWindow().setFlags(1024, 1024);
                    VideoViewActivity.vari = 1;
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaX = e1.getRawX() - e2.getRawX();
            float deltaY = e1.getRawY() - e2.getRawY();
            Long currentTime = Long.valueOf(System.currentTimeMillis());
            setGestureListener();
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > 20.0f && currentTime.longValue() >= lastVolumeUpdateTime.longValue() + 1000) {
                    boolean z;
                    lastSeekUpdateTime = currentTime;
                    VideoViewActivity videoViewActivity = VideoViewActivity.this;
                    if (deltaX < 0.0f) {
                        z = true;
                    } else {
                        z = false;
                    }
                    videoViewActivity.onHorizontalScroll(z);
                }
            } else if (Math.abs(deltaY) > BitmapDescriptorFactory.HUE_YELLOW && currentTime.longValue() >= lastSeekUpdateTime.longValue() + 1000) {
                if (((double) e1.getX()) < ((double) VideoViewActivity.getDeviceWidth(context)) * 0.5d) {
                    lastVolumeUpdateTime = currentTime;
                    onVerticalScroll(deltaY / ((float) VideoViewActivity.getDeviceHeight(context)), 1);
                } else if (((double) e1.getX()) > ((double) VideoViewActivity.getDeviceWidth(context)) * 0.5d) {
                    lastVolumeUpdateTime = currentTime;
                    onVerticalScroll(deltaY / ((float) VideoViewActivity.getDeviceHeight(context)), 2);
                }
            }
            return true;
        }
    };

    public static LinearLayout nativeAdContainer;
    public static LinearLayout adView;
    ImageView size_screen;
    ImageView size_screenback;
    int stopPosition = -1;
    TextView textbrightness;
    TextView textvolume;
    public double timeElapsed = 0.0d;
    private Runnable updateSeekBarTime = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            String songTime;
            durationHandler.removeCallbacks(updateSeekBarTime);
            timeElapsed = (double) videoview.getCurrentPosition();
            if (videoview.getCurrentPosition() > 0) {
                seekbar_vplay.setMax(videoview.getDuration());
                seekbar_vplay.setProgress(videoview.getCurrentPosition());
                videoFile.setResumePosition(videoview.getCurrentPosition());
                AppPreferences.setResumePositionByPath(VideoViewActivity.this, videoFile);
            }
            double d = timeElapsed;
            TextView localTextView = current_position;
            int mns = (int) ((d % 3600000.0d) / 60000.0d);
            int scs = (int) (((d % 3600000.0d) % 60000.0d) / 1000.0d);
            if (((int) (d / 3600000.0d)) > 0) {
                songTime = String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf((int) (d / 3600000.0d)), Integer.valueOf(mns), Integer.valueOf(scs)});
            } else {
                songTime = String.format("%02d:%02d", new Object[]{Integer.valueOf(mns), Integer.valueOf(scs)});
            }
            localTextView.setText(songTime);
            scroll_position.setText(songTime);
            durationHandler.postDelayed(this, 100);
        }
    };
    MediaFile videoFile;
    ArrayList<MediaFile> videoList = new ArrayList();
    View video_header_controls;
    TextView video_title;
    VideoView videoview;
    private SeekBar volumebar;
    String str_check_music = "1";
    Dialog dialog,dialog_filelist;
    private Window window;
   public ImageView audiotrack;
   RelativeLayout rel_nt;

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

    @SuppressLint({"NewApi"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoFile = new MediaFile();
        Long valueOf = Long.valueOf(System.currentTimeMillis());
        lastVolumeUpdateTime = valueOf;
        lastSeekUpdateTime = valueOf;
        setContentView(R.layout.activity_video_view);
        getWindow().setFlags(1024, 1024);
        rel_nt=(RelativeLayout) findViewById(R.id.rel_nt);
      switch (((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation()) {
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
        startService(new Intent(this, FloatingViewService.class));
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initComponents();
        gestureDetector= new GestureDetector(simpleOnGestureListener);
        videoFile.setResumePosition(AppPreferences.getResumePositionByPath(this, videoFile.getPath()));
        setUpComponents();
        playbutton.performClick();
        ImageView propeties = (ImageView) findViewById(R.id.btnpro);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            propeties.setVisibility(View.INVISIBLE);
        } else {
            propeties.setVisibility(View.VISIBLE);
        }
        propeties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_filelist = new Dialog(VideoViewActivity.this);
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
        });


        audiotrack = (ImageView) findViewById(R.id.ic_audio_tracks);
        audiotrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(VideoViewActivity.this);
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
    }

    public void backArrowListener(View v) {
        onBackPressed();
    }

    private void setUpComponents() {
        getWindowManager().getDefaultDisplay();
        open_pop_up_video.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View v) {
                if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(VideoViewActivity.this)) {
                    durationHandler.removeCallbacks(updateSeekBarTime);
                    Intent intent = new Intent(VideoViewActivity.this, FloatingViewService.class);
                    intent.putExtra("START_FROM", videoview.getCurrentPosition());
                    intent.putExtra("ITEM_POSITION", current);
                    intent.putExtra("FOLDER_ITEMS", videoList);
                    intent.addCategory("android.intent.category.HOME");
                    startService(intent);
                    Intent startMain = new Intent("android.intent.action.MAIN");
                    startMain.addCategory("android.intent.category.HOME");
                    startMain.setFlags(DriveFile.MODE_READ_ONLY);
                    startActivity(startMain);
                    finish();
                    return;
                }
                startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), VideoViewActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            }
        });
        music_controls.postDelayed(hideScreenControllsRunnable, 3000);
        locked.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                aa = false;
                lock_click = true;
                music_controls.setVisibility(View.GONE);
                video_header_controls.setVisibility(View.GONE);
                lock.setVisibility(View.VISIBLE);
                laylock.setEnabled(false);
                size_screenback.setEnabled(false);
                hundred_screensize.setEnabled(false);
                screen_sizes.setEnabled(false);
                left_press.setEnabled(false);
                right_press.setEnabled(false);
                volumebar.setEnabled(false);
                brightbar.setEnabled(false);
                seekbar_vplay.setEnabled(false);
                rewind_btn.setEnabled(false);
                play_btn.setEnabled(false);
                forward_btn.setEnabled(false);
                locked.setTag("a");
                music_controls.setEnabled(false);
            }
        });
        hundred_screensize.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                screen_sizes.setVisibility(View.VISIBLE);
                screen_sizes.setText("100%");
                screen_sizes.postDelayed(new Runnable() {
                    public void run() {
                        screen_sizes.setVisibility(View.GONE);
                    }
                }, 2000);
                size_screenback.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
                scroll_position.setVisibility(View.GONE);
                hundred_screensize.setVisibility(View.GONE);
                size_screen.setVisibility(View.VISIBLE);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                LayoutParams params = (LayoutParams) videoview.getLayoutParams();
                params.width = (int) (1000.0f * metrics.density);
                params.leftMargin = 150;
                params.rightMargin = 150;
                params.topMargin = 0;
                params.bottomMargin = 0;
                videoview.setLayoutParams(params);
            }
        });
        size_screenback.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                screen_sizes.setVisibility(View.VISIBLE);
                screen_sizes.setText("FIT TO SCREEN");
                screen_sizes.postDelayed(new Runnable() {
                    public void run() {
                        screen_sizes.setVisibility(View.GONE);
                    }
                }, 2000);
                size_screenback.setVisibility(View.GONE);
                size_screen.setVisibility(View.GONE);
                scroll_position.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
                hundred_screensize.setVisibility(View.VISIBLE);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                LayoutParams params = (LayoutParams) videoview.getLayoutParams();
                params.width = metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                params.rightMargin = 0;
                params.topMargin = 0;
                params.bottomMargin = 0;
                videoview.setLayoutParams(params);
            }
        });
        size_screen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                screen_sizes.setVisibility(View.VISIBLE);
                screen_sizes.setText("CROP");
                screen_sizes.postDelayed(new Runnable() {
                    public void run() {
                        screen_sizes.setVisibility(View.GONE);
                    }
                }, 2000);
                size_screenback.setVisibility(View.VISIBLE);
                scroll_position.setVisibility(View.GONE);
                size_screen.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
                hundred_screensize.setVisibility(View.GONE);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                LayoutParams params = (LayoutParams) videoview.getLayoutParams();
                params.width = (int) (400.0f * metrics.density);
                params.height = (int) (BitmapDescriptorFactory.HUE_MAGENTA * metrics.density);
                params.leftMargin = 150;
                params.rightMargin = 150;
                params.topMargin = 150;
                params.bottomMargin = 150;
                videoview.setLayoutParams(params);
            }
        });
        lock.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                aa = true;
                laylock.setEnabled(true);
                lock.setVisibility(View.GONE);
                img_rotate.setVisibility(View.GONE);
                left_press.setEnabled(true);
                right_press.setEnabled(true);
                volumebar.setEnabled(true);
                brightbar.setEnabled(true);
                music_controls.setEnabled(true);
                seekbar_vplay.setEnabled(true);
                rewind_btn.setEnabled(true);
                play_btn.setEnabled(true);
                forward_btn.setEnabled(true);
                lock_click = false;
                lock.setTag("b");
                seekbar_vplay.setVisibility(View.VISIBLE);
                rewind_btn.setVisibility(View.VISIBLE);
                forward_btn.setVisibility(View.VISIBLE);
                music_controls.setVisibility(View.VISIBLE);
                video_header_controls.setVisibility(View.VISIBLE);
                if (videoview.isPlaying()) {
                    pause_btn.setVisibility(View.VISIBLE);
                    play_btn.setVisibility(View.INVISIBLE);
                    return;
                }
                pause_btn.setVisibility(View.GONE);
                play_btn.setVisibility(View.VISIBLE);
            }
        });
        pause_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rel_nt.setVisibility(View.VISIBLE);
                rel_bar.setVisibility(View.VISIBLE);
                play_btn.setVisibility(View.VISIBLE);
                pause_btn.setVisibility(View.GONE);
                music_controls.removeCallbacks(hideScreenControllsRunnable);
                videoview.pause();
            }
        });
        play_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rel_nt.setVisibility(View.GONE);
                pause_btn.setVisibility(View.VISIBLE);
                play_btn.setVisibility(View.INVISIBLE);
                playbutton.setVisibility(View.GONE);
                music_controls.postDelayed(hideScreenControllsRunnable, 1000);
                VideoViewActivity.vari = 1;
                //hideAd();
                videoview.start();
            }
        });
        forward_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                aa = false;
                if (current + 1 == videoList.size()) {
                    current = 0;
                } else {
                    VideoViewActivity videoViewActivity = VideoViewActivity.this;
                    videoViewActivity.current++;
                }
                loadVideo();
            }
        });
        rewind_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                aa = false;
                if (current == 0) {
                    current = videoList.size() - 1;
                } else {
                    VideoViewActivity videoViewActivity = VideoViewActivity.this;
                    videoViewActivity.current--;
                }
                loadVideo();
            }
        });
        seekbar_vplay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progressval, boolean fromUser) {
                if (videoview != null && fromUser) {
                    progress = progressval;
                    videoview.seekTo(progress);
                }
            }
        });
        videoview.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                videoFile.setResumePosition(0);
                AppPreferences.setResumePositionByPath(VideoViewActivity.this, videoFile);
                if (current + 1 == videoList.size()) {
                    play_btn.setVisibility(View.VISIBLE);
                    pause_btn.setVisibility(View.GONE);
                    playbutton.setVisibility(View.VISIBLE);
                    music_controls.post(hideScreenControllsRunnable);
                    return;
                }
                forward_btn.performClick();
            }
        });
        mHandlerss = new Handler();
        videoview.setKeepScreenOn(true);
        img_rotate=(ImageView)findViewById(R.id.img_rotation);
        img_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                if (rotation == 1 || rotation == 3) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    return;
                }

            }
        });

        window = getWindow();
        brightbar.setMax(100);
        right_press.setMax(100);
        volumebar.setMax(mAudioManager.getStreamMaxVolume(3));
        volumebar.setProgress(mAudioManager.getStreamVolume(3));
        volumebar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(3, progress, 0);
                if (progress > 0) {
                    progress = (progress * 100) / mMaxVolume;
                }
                textvolume.setText(String.valueOf(progress));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        volumebar.setKeyProgressIncrement(1);
        brightbar.setKeyProgressIncrement(1);
        try {
            brightness = Settings.System.getInt(cResolver, "screen_brightness");
        } catch (SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }
        videoview.requestFocus();
        music_controls.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        playbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (videoview.isPlaying()) {
                    videoview.stopPlayback();
                    videoview.setZOrderOnTop(true);
                    return;
                }
                seekbar_vplay.setMax(videoFile.getDuration());
                playbutton.setVisibility(View.GONE);
                getWindow().clearFlags(2048);
                getWindow().addFlags(1024);
                VideoViewActivity.vari = 1;
                play_btn.setVisibility(View.INVISIBLE);
                pause_btn.setVisibility(View.VISIBLE);
                seekbar_vplay.setVisibility(View.VISIBLE);
                videoview.setZOrderOnTop(false);
                videoview.start();
                if (getIntent().hasExtra("START_FROM")) {
                    videoview.seekTo(extras.getInt("START_FROM"));
                    getIntent().removeExtra("START_FROM");
                } else if (videoFile.getResumePosition() > 0 && !played) {
                    videoview.seekTo(videoFile.getResumePosition());
                    played = true;
                }
                timeElapsed = (double) videoview.getCurrentPosition();
                seekbar_vplay.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 10);
                rel_bar.setVisibility(View.VISIBLE);
                video_header_controls.setVisibility(View.VISIBLE);
                if (VideoViewActivity.oneTimeOnly == 0) {
                    seekbar_vplay.setMax(videoFile.getDuration());
                    VideoViewActivity.oneTimeOnly = 1;
                }
            }
        });
        videoview.setOnPreparedListener(new OnPreparedListener() {
            @SuppressLint("WrongConstant")
            public void onPrepared(MediaPlayer mp) {
                videoview.setBackgroundColor(0);
                audioTracksList = new ArrayList();
                audioTracksIndexes = new ArrayList();
                selectedAudioTrack = -1;
                if (VERSION.SDK_INT >= 16) {
                    audiotrack.setVisibility(View.VISIBLE);
                    TrackInfo[] trackInfoArray = mp.getTrackInfo();
                    int j = 0;
                    for (int i = 0; i < trackInfoArray.length; i++) {
                        if (trackInfoArray[i].getTrackType() == 2) {
                            String language_name = trackInfoArray[i].getLanguage();
                            if (language_name.equals("und") || language_name.isEmpty()) {
                                j++;
                                language_name = "Audio track #" + j;
                            } else {
                                Locale loc = new Locale(language_name);
                                language_name = loc.getDisplayLanguage(loc);
                            }
                            audioTracksList.add(language_name);
                            audioTracksIndexes.add(Integer.valueOf(i));
                            Log.d("AudioTrack", i + " : " + language_name);
                        }
                    }
                    if (!audioTracksIndexes.isEmpty()) {
                        selectedAudioTrack = ((Integer) audioTracksIndexes.get(0)).intValue();
                    }
                    mediaPlayer = mp;
                }
            }
        });
    }

    public void loadVideo() {
        videoview.stopPlayback();
        get_video(current);
        seekbar_vplay.setMax(videoFile.getDuration());
        seekbar_vplay.setMax(videoFile.getDuration());
        playbutton.setVisibility(View.GONE);
        play_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        seekbar_vplay.setVisibility(View.VISIBLE);
        videoview.setZOrderOnTop(false);
        //hideAd();
        if (videoFile.getResumePosition() > 0 && !played) {
            videoview.seekTo(videoFile.getResumePosition());
            played = true;
        }
        videoview.start();
        timeElapsed = (double) videoview.getCurrentPosition();
        seekbar_vplay.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        rel_bar.setVisibility(View.VISIBLE);
        music_controls.postDelayed(hideScreenControllsRunnable, 3000);
    }

    public void enlistAudioTracks(View view) {
        if (VERSION.SDK_INT < 16 || audioTracksList == null || audioTracksList.size() <= 0) {
            Toast.makeText(this, "No Audio track found", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.audiotracks_dialog);
        onPause();
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        for (int i = 0; i < audioTracksList.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText((CharSequence) audioTracksList.get(i));
            rb.setTextColor(getResources().getColor(R.color.white));
            if (((Integer) audioTracksIndexes.get(i)).intValue() == selectedAudioTrack) {
                rb.setChecked(true);
                rb.setButtonDrawable(R.drawable.ic_radio_button_checked);
            } else {
                rb.setButtonDrawable(R.drawable.ic_radio_button_unchecked);
            }
            rg.addView(rb);
        }
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) rg.findViewById(checkedId);
                int index = rg.indexOfChild(radioButton);
                selectedAudioTrack = ((Integer) audioTracksIndexes.get(index)).intValue();
                radioButton.setButtonDrawable(R.drawable.ic_radio_button_checked);
                if (VERSION.SDK_INT >= 16) {
                    mediaPlayer.selectTrack(selectedAudioTrack);
                }
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                onResume();
            }
        });
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (VERSION.SDK_INT < 23 || !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Draw over other app permission not available. Closing the application", Toast.LENGTH_SHORT).show();
        } else {
            open_pop_up_video.performClick();
        }
    }

    private void initComponents() {
        video_header_controls = findViewById(R.id.video_header);
        video_title = (TextView) findViewById(R.id.video_title);
        open_pop_up_video = (ImageView) findViewById(R.id.open_pop_up_video);
        context = this;
        mContext = this;
        extras = getIntent().getExtras();
        videoList = (ArrayList) getIntent().getSerializableExtra("FOLDER_ITEMS");
        current = getIntent().getIntExtra("ITEM_POSITION", 0);
        videoFile = (MediaFile) videoList.get(current);
        scroll_position = (TextView) findViewById(R.id.scroll_position);
        current_position = (TextView) findViewById(R.id.current_position);
        remainingDurationTv = (TextView) findViewById(R.id.left_time);
        remainingDurationTv.setText(FileDataHelper.getFileDurationFormated(videoFile.getDuration()));
        videoview = (VideoView) findViewById(R.id.videoView);
        playbutton = (ImageButton) findViewById(R.id.play_button);
        left_press = (SeekBar) findViewById(R.id.left_press);
        right_press = (SeekBar) findViewById(R.id.right_press);
        //portrat = (ImageView) findViewById(R.id.switch_to_portrait);
       // land = (ImageView) findViewById(R.id.switch_to_landscape);
        textvolume = (TextView) findViewById(R.id.textvolume);
        textbrightness = (TextView) findViewById(R.id.textbrightness);
        pause_btn = (ImageView) findViewById(R.id.pause_btn);
        play_btn = (ImageView) findViewById(R.id.play_btn);
        play_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        rel_bar = findViewById(R.id.music_controls);
        rel_bar.setVisibility(View.GONE);
        forward_btn = (ImageView) findViewById(R.id.forward_btn);
        rewind_btn = (ImageView) findViewById(R.id.rewind_btn);
        seekbar_vplay = (SeekBar) findViewById(R.id.video_seekbar);
        lock = (ImageView) findViewById(R.id.lock);
        locked = (ImageView) findViewById(R.id.locked);
        laylock = (LinearLayout) findViewById(R.id.laylock);
        music_controls = findViewById(R.id.music_controls);
        videoview.setVideoPath(videoFile.getPath());
        videoFile.setDuration(videoview.getDuration());
        video_title.setText(videoFile.getFileName());
        brightbar = (SeekBar) findViewById(R.id.brightness_seekbar);
        volumebar = (SeekBar) findViewById(R.id.volume_seekbar);
        cResolver = getContentResolver();
        brightbar.setVisibility(View.GONE);
        volumebar.setVisibility(View.GONE);
        textvolume.setVisibility(View.GONE);
        textbrightness.setVisibility(View.GONE);
//        land.setVisibility(View.VISIBLE);
//        portrat.setVisibility(View.GONE);
        lock.setVisibility(View.GONE);
        seekbar_vplay.setVisibility(View.GONE);
        setVolumeControlStream(3);
        size_screen = (ImageView) findViewById(R.id.size_screen);
        size_screenback = (ImageView) findViewById(R.id.size_screenback);
        screen_sizes = (TextView) findViewById(R.id.screen_sizes);
        size_screen.setVisibility(View.VISIBLE);
        scroll_position.setVisibility(View.GONE);
        screen_sizes.setVisibility(View.GONE);
        size_screenback.setVisibility(View.GONE);
        hundred_screensize = (ImageView) findViewById(R.id.hundred_screensize);
        hundred_screensize.setVisibility(View.GONE);
    }

    public void onPause() {
        Log.d("VideoView", "onPause called");
        super.onPause();
        stopPosition = videoview.getCurrentPosition();
        if (videoview.isPlaying()) {
            videoview.pause();
            isPlaying = true;
            return;
        }
        isPlaying = false;
    }

    public void onResume() {
        super.onResume();
        Log.d("VideoView", "onResume called");
        if (stopPosition > 0) {
            videoview.seekTo(stopPosition);
            if (isPlaying) {
                videoview.start();
            }
        }
    }
    
    private void get_video(int position) {
        videoFile = (MediaFile) videoList.get(position);
        volumebar.setEnabled(false);
        brightbar.setEnabled(false);
        aa = true;
        videoview.stopPlayback();
        videoview.setVideoPath(videoFile.getPath());
        videoFile.setResumePosition(AppPreferences.getResumePositionByPath(this, videoFile.getPath()));
        video_title.setText(videoFile.getFileName());
        videoview.seekTo(100);
        current_position.setText("00:00");
        remainingDurationTv.setText(FileDataHelper.getFileDurationFormated(videoFile.getDuration()));
        scroll_position.setText(FileDataHelper.getFileDurationFormated(videoFile.getDuration()));
        played = false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 1:
                mCurVolume = -1;
                mCurBrightness = -1.0f;
                volumebar.postDelayed(new Runnable() {
                    public void run() {
                        volumebar.setVisibility(View.GONE);
                    }
                }, 3000);
                textvolume.postDelayed(new Runnable() {
                    public void run() {
                        textvolume.setVisibility(View.GONE);
                    }
                }, 3000);
                brightbar.postDelayed(new Runnable() {
                    public void run() {
                        brightbar.setVisibility(View.GONE);
                    }
                }, 3000);
                textbrightness.postDelayed(new Runnable() {
                    public void run() {
                        textbrightness.setVisibility(View.GONE);
                    }
                }, 3000);
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void onSwipe(int direction) {
        switch (direction) {
            case 1:
                Log.d("111-SWIPE_UP-111", "111-SWIPE_UP-111");
                return;
            case 2:
                Log.d("111-SWIPE_DOWN-111", "111-SWIPE_DOWN-111");
                return;
            case 3:
                Log.d("111-SWIPE_LEFT-111", "111-SWIPE_LEFT-111");
                currentPosition = videoview.getCurrentPosition();
                currentPosition = videoview.getCurrentPosition() + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
                videoview.seekTo(currentPosition);
                return;
            case 4:
                Log.d("111-SWIPE_RIGHT-111", "111-SWIPE_RIGHT-111");
                currentPosition = videoview.getCurrentPosition();
                currentPosition = videoview.getCurrentPosition() + 1000;
                videoview.seekTo(currentPosition);
                return;
            default:
                return;
        }
    }

    public void onStart(Boolean bol) {
    }

    public void setGestureListener() {
        mMaxVolume = mAudioManager.getStreamMaxVolume(3);
    }

    public void onVerticalScroll(float percent, int direction) {
        if (direction == 1) {
            changeBrightness(percent * 2.0f);
        } else {
            changeVolume(percent * 2.0f);
        }
    }

    public void onHorizontalScroll(boolean seekForward) {
        if (((seekForward && videoview.canSeekForward()) || (!seekForward && videoview.canSeekBackward())) && aa) {
            if (music_controls.getVisibility() == View.GONE) {
                music_controls.setVisibility(View.VISIBLE);

            }
            mAudioManager.setStreamMute(3, true);
            videoview.removeCallbacks(horizontalScrollRunnable);
            if (scroll_position.getVisibility() == View.GONE) {
                scroll_position.setVisibility(View.VISIBLE);
            }
            videoview.postDelayed(horizontalScrollRunnable, 1000);
            if (seekForward) {
                Log.i("ViewGestureListener", "Forwarding");
                currentPosition = videoview.getCurrentPosition();
                currentPosition = videoview.getCurrentPosition() + 700;
                videoview.seekTo(currentPosition);
                return;
            }
            Log.i("ViewGestureListener", "Rewinding");
            currentPosition = videoview.getCurrentPosition();
            currentPosition = videoview.getCurrentPosition() - 700;
            videoview.seekTo(currentPosition);
        }
    }

    private void changeBrightness(float percent) {
        img_rotate.setVisibility(View.GONE);
        if (mCurBrightness == -1.0f) {
            mCurBrightness = mContext.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness <= 0.01f) {
                mCurBrightness = 0.01f;
            }
        }
        brightbar.setVisibility(View.VISIBLE);
        textbrightness.setVisibility(View.VISIBLE);
        WindowManager.LayoutParams attributes = mContext.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness >= TextTrackStyle.DEFAULT_FONT_SCALE) {
            attributes.screenBrightness = TextTrackStyle.DEFAULT_FONT_SCALE;
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        mContext.getWindow().setAttributes(attributes);
        float p = attributes.screenBrightness * 100.0f;
        brightbar.setProgress((int) p);
        textbrightness.setText(String.valueOf((int) p));
    }

    private void changeVolume(float percent) {
        volumebar.setVisibility(View.VISIBLE);
        textvolume.setVisibility(View.VISIBLE);
        img_rotate.setVisibility(View.GONE);
        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(3);
            if (((float) mCurVolume) < 0.01f) {
                mCurVolume = 0;
            }
        }
        int volume = ((int) (((float) mMaxVolume) * percent)) + mCurVolume;
        if (volume > mMaxVolume) {
            volume = mMaxVolume;
        }
        if (((float) volume) < 0.01f) {
            volume = 0;
        }
        volumebar.setProgress(volume);
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (videoview != null && videoview.isPlaying()) {
            videoview.stopPlayback();
        }
        finish();
    }



    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }
}
