package com.example.mygallery.videoview;

public class VideoPlayerState {
    private String filename;
    private int start = 0, stop = 0;
    private int currentTime = 0;
    private String messageText;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDuration() {
        // TODO Auto-generated method stub
        return stop - start;
    }

}
