package com.example.mygallery.videofile;

public class Count {

    public class MilliSeconds {
        public static final int ONE_HOUR = 3600000;
        public static final int ONE_MINUTE = 60000;
        public static final int ONE_SECOND = 1000;
    }

    public static String toFormattedTime(int time) {
        int remainingTime = time;
        int hours = remainingTime / MilliSeconds.ONE_HOUR;
        remainingTime -= MilliSeconds.ONE_HOUR * hours;
        int seconds = (remainingTime - ((remainingTime / MilliSeconds.ONE_MINUTE) * MilliSeconds.ONE_MINUTE)) / 1000;
        int minutes=0;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }
}
