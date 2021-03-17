package com.example.mygallery.videoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class OnScrollTouchListenerControl implements View.OnTouchListener {

    private static final String TAG = OnScrollTouchListenerControl.class.getName();
    private final GestureDetector gestureDetector;
    private ISwipeRefresh iSwipeRefresh;

    public OnScrollTouchListenerControl(Context ctx, ISwipeRefresh iSwipeRefresh) {
        this.iSwipeRefresh = iSwipeRefresh;
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }


//    public OnScrollTouchListenerControl(Context context){
//        gestureDetector=new GestureDetector(context,new GestureListener());
//    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener implements GestureDetector.OnGestureListener {

        private static final int SWIPE_THRESHOLD = 10;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                System.out.println("D--x--" + distanceX);
                System.out.println("D--y--" + distanceY);
                System.out.println("D--xy--" + diffX);
                System.out.println("D--yx--" + diffY);

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(distanceX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            iSwipeRefresh.rightSwipe();
                            return true;
                        } else {
                            iSwipeRefresh.leftSwipe();
                            return true;
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(distanceY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            Log.d("Gesture ", " onVolumeDown");
                            iSwipeRefresh.downSwipe();
                            return true;
                        } else {
                            Log.d("Gesture ", " onVolumeUp");
                            iSwipeRefresh.upSwipe();
                            return true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("Gesture ", " onSingleTapUp");
            iSwipeRefresh.onsingleClick();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("Gesture ", " onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d("Gesture ", " onShowPress");
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("Gesture ", " onLongPress");
        }
    }
}
