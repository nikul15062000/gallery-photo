package com.example.mygallery.videoview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {

    int _overrideHeight = 300;
    int _overrideWidth = 800;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet set) {
        super(context, set);
    }

    public void setFixedVideoSize(int width, int height) {
        _overrideHeight = height;
        _overrideWidth = width;
        getHolder().setFixedSize(width, height);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(_overrideWidth, _overrideHeight);
    }

}