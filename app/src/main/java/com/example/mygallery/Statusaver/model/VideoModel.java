package com.example.mygallery.Statusaver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoModel implements Parcelable {
    String path,thumb;
    boolean boolen_selected;

    public VideoModel(Parcel in) {
        path = in.readString();
        thumb = in.readString();
        boolen_selected = in.readByte() != 0;
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public VideoModel() {

    }

    public VideoModel(String absolutePath) {
        this.thumb=absolutePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public boolean isBoolen_selected(boolean b) {
        return boolen_selected;
    }

    public void setBoolen_selected(boolean boolen_selected) {
        this.boolen_selected = boolen_selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(thumb);
        parcel.writeByte((byte) (boolen_selected ? 1 : 0));
    }
}
