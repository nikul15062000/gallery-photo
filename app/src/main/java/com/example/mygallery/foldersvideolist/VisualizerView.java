package com.example.mygallery.foldersvideolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.util.AttributeSet;
import android.view.View;

import com.example.mygallery.foldersvideolist.rendee.AudioData;
import com.example.mygallery.foldersvideolist.rendee.FFTData;
import com.example.mygallery.foldersvideolist.rendee.Renderer;

import java.util.HashSet;
import java.util.Set;

public class VisualizerView extends View {
    private static final String TAG = "VisualizerView";
    private byte[] mBytes;
    Canvas mCanvas;
    Bitmap mCanvasBitmap;
    private byte[] mFFTBytes;
    private Paint mFadePaint;
    boolean mFlash;
    private Paint mFlashPaint;
    private Rect mRect;
    private Set<Renderer> mRenderers;
    private Visualizer mVisualizer;

    public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.mRect = new Rect();
        this.mFlashPaint = new Paint();
        this.mFadePaint = new Paint();
        this.mFlash = false;
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context) {
        this(context, null, 0);
    }

    private void init() {
        this.mBytes = null;
        this.mFFTBytes = null;
        this.mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
        this.mFadePaint.setColor(Color.argb(238, 255, 255, 255));
        this.mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
        this.mRenderers = new HashSet();
    }

    public void link(MediaPlayer player) {
        if (player == null) {
            throw new NullPointerException("Cannot link to null MediaPlayer");
        }
        this.mVisualizer = new Visualizer(player.getAudioSessionId());
        this.mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        this.mVisualizer.setDataCaptureListener(new OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                VisualizerView.this.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                VisualizerView.this.updateVisualizerFFT(bytes);
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
        this.mVisualizer.setEnabled(true);
        player.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                VisualizerView.this.mVisualizer.setEnabled(false);
            }
        });
    }

    public void addRenderer(Renderer renderer) {
        if (renderer != null) {
            this.mRenderers.add(renderer);
        }
    }

    public void updateVisualizer(byte[] bytes) {
        this.mBytes = bytes;
        invalidate();
    }

    public void updateVisualizerFFT(byte[] bytes) {
        this.mFFTBytes = bytes;
        invalidate();
    }

    public void flash() {
        this.mFlash = true;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mRect.set(0, 0, getWidth(), getHeight());
        if (this.mCanvasBitmap == null) {
            this.mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
        }
        if (this.mCanvas == null) {
            this.mCanvas = new Canvas(this.mCanvasBitmap);
        }
        if (this.mBytes != null) {
            AudioData audioData = new AudioData(this.mBytes);
            for (Renderer r : this.mRenderers) {
                r.render(this.mCanvas, audioData, this.mRect);
            }
        }
        if (this.mFFTBytes != null) {
            FFTData fftData = new FFTData(this.mFFTBytes);
            for (Renderer r2 : this.mRenderers) {
                r2.render(this.mCanvas, fftData, this.mRect);
            }
        }
        this.mCanvas.drawPaint(this.mFadePaint);
        if (this.mFlash) {
            this.mFlash = false;
            this.mCanvas.drawPaint(this.mFlashPaint);
        }
        canvas.drawBitmap(this.mCanvasBitmap, new Matrix(), null);
    }
}
