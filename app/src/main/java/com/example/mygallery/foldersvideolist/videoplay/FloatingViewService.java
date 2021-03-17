package com.example.mygallery.foldersvideolist.videoplay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.FileDataHelper;
import com.example.mygallery.foldersvideolist.MediaFile;

import java.util.ArrayList;

public class FloatingViewService extends Service {
    int current;
    TextView current_position;
    GestureDetector gestureDetector = new GestureDetector(this.simpleOnGestureListener);
    private Runnable hideScreenControllsRunnable = new Runnable() {
        public void run() {
            try {
                if (FloatingViewService.this.music_controls.getVisibility() == View.VISIBLE) {
                    FloatingViewService.this.play_pause_btn.setVisibility(View.VISIBLE);
                    FloatingViewService.this.music_controls.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private View mFloatingView;
    private WindowManager mWindowManager;
    View music_controls;
    ImageView play_pause_btn;
    SeekBar seekbar_vplay;
    SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        @TargetApi(11)
        public boolean onSingleTapConfirmed(MotionEvent e) {
            FloatingViewService.this.music_controls.removeCallbacks(FloatingViewService.this.hideScreenControllsRunnable);
            if (FloatingViewService.this.music_controls.getVisibility() == View.GONE) {
                FloatingViewService.this.music_controls.setVisibility(View.VISIBLE);
                FloatingViewService.this.play_pause_btn.setVisibility(View.VISIBLE);
            } else {
                FloatingViewService.this.music_controls.setVisibility(View.GONE);
                FloatingViewService.this.play_pause_btn.setVisibility(View.GONE);
            }
            FloatingViewService.this.music_controls.postDelayed(FloatingViewService.this.hideScreenControllsRunnable, 5000);
            return super.onSingleTapConfirmed(e);
        }
    };
    int startFrom;
    TextView total_duration;
    private Runnable updateSeekBarTime = new Runnable() {
        @SuppressLint({"NewApi"})
        public void run() {
            try {
                String songTime;
                FloatingViewService.this.videoView.removeCallbacks(FloatingViewService.this.updateSeekBarTime);
                double d = (double) FloatingViewService.this.videoView.getCurrentPosition();
                if (d > 0.0d) {
                    FloatingViewService.this.seekbar_vplay.setMax(FloatingViewService.this.videoView.getDuration());
                    FloatingViewService.this.seekbar_vplay.setProgress((int) d);
                }
                int mns = (int) ((d % 3600000.0d) / 60000.0d);
                int scs = (int) (((d % 3600000.0d) % 60000.0d) / 1000.0d);
                if (((int) (d / 3600000.0d)) > 0) {
                    songTime = String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf((int) (d / 3600000.0d)), Integer.valueOf(mns), Integer.valueOf(scs)});
                } else {
                    songTime = String.format("%02d:%02d", new Object[]{Integer.valueOf(mns), Integer.valueOf(scs)});
                }
                FloatingViewService.this.current_position.setText(songTime);
                FloatingViewService.this.videoView.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    MediaFile videoFile;
    ArrayList<MediaFile> videoList = new ArrayList();
    VideoView videoView;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            this.videoList = (ArrayList) intent.getSerializableExtra("FOLDER_ITEMS");
            this.current = intent.getIntExtra("ITEM_POSITION", 0);
            this.videoFile = (MediaFile) this.videoList.get(this.current);
            this.startFrom = intent.getExtras().getInt("START_FROM", -1);
            setUpWindow();
        } catch (Exception e) {
            stopSelf();
        }
        return 1;
    }

    private void setUpWindow() {
        this.mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final LayoutParams params = new LayoutParams(-1, -2, 2002, 8, -3);
        params.gravity = 48;
        params.x = 0;
        params.y = 0;
        this.mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.mWindowManager.addView(this.mFloatingView, params);
        final View expandedView = this.mFloatingView.findViewById(R.id.expanded_container);
        this.play_pause_btn = (ImageView) this.mFloatingView.findViewById(R.id.play_pause_btn);
        this.total_duration = (TextView) this.mFloatingView.findViewById(R.id.left_time);
        this.total_duration.setText(FileDataHelper.getFileDurationFormated(this.videoFile.getDuration()));
        this.current_position = (TextView) this.mFloatingView.findViewById(R.id.current_position);
        this.music_controls = this.mFloatingView.findViewById(R.id.music_controls);
        this.seekbar_vplay = (SeekBar) this.mFloatingView.findViewById(R.id.video_seekbar);
        this.videoView = (VideoView) this.mFloatingView.findViewById(R.id.pop_up_video);
        this.videoView.setVideoPath(this.videoFile.getPath());
        this.videoView.start();
        this.videoView.seekTo(this.startFrom);
        this.videoView.postDelayed(this.updateSeekBarTime, 100);
        this.play_pause_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (FloatingViewService.this.videoView.isPlaying()) {
                    FloatingViewService.this.videoView.pause();
                    FloatingViewService.this.play_pause_btn.setImageDrawable(FloatingViewService.this.getApplicationContext().getResources().getDrawable(R.drawable.play));
                    return;
                }
                FloatingViewService.this.videoView.start();
                FloatingViewService.this.play_pause_btn.setImageDrawable(FloatingViewService.this.getApplicationContext().getResources().getDrawable(R.drawable.pause_btn));
            }
        });
        this.videoView.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                FloatingViewService.this.stopSelf();
            }
        });
        this.seekbar_vplay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progressval, boolean fromUser) {
                if (FloatingViewService.this.videoView != null && fromUser) {
                    FloatingViewService.this.videoView.seekTo(progressval);
                }
            }
        });
        this.videoView.setKeepScreenOn(true);
        ((ImageView) this.mFloatingView.findViewById(R.id.close_btn)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FloatingViewService.this.stopSelf();
            }
        });
        ((ImageView) this.mFloatingView.findViewById(R.id.open_full_video)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(FloatingViewService.this, VideoViewActivity.class);
                intent.putExtra("ITEM_POSITION", FloatingViewService.this.current);
                intent.putExtra("FOLDER_ITEMS", FloatingViewService.this.videoList);
                intent.putExtra("START_FROM", FloatingViewService.this.videoView.getCurrentPosition());
                FloatingViewService.this.startActivity(intent);
                FloatingViewService.this.stopSelf();
            }
        });
        this.mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View v, MotionEvent event) {
                FloatingViewService.this.gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case 0:
                        this.initialX = params.x;
                        this.initialY = params.y;
                        this.initialTouchX = event.getRawX();
                        this.initialTouchY = event.getRawY();
                        return true;
                    case 1:
                        int Ydiff = (int) (event.getRawY() - this.initialTouchY);
                        if (((int) (event.getRawX() - this.initialTouchX)) >= 10 || Ydiff >= 10 || !FloatingViewService.this.isViewCollapsed()) {
                            return true;
                        }
                        expandedView.setVisibility(View.VISIBLE);
                        return true;
                    case 2:
                        params.x = this.initialX + ((int) (event.getRawX() - this.initialTouchX));
                        params.y = this.initialY + ((int) (event.getRawY() - this.initialTouchY));
                        FloatingViewService.this.mWindowManager.updateViewLayout(FloatingViewService.this.mFloatingView, params);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void onCreate() {
        super.onCreate();
    }

    private boolean isViewCollapsed() {
        return this.mFloatingView == null;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mFloatingView != null) {
            this.mWindowManager.removeView(this.mFloatingView);
        }
    }
}
