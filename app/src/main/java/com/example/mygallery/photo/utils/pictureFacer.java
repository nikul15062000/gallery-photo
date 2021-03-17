package com.example.mygallery.photo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class pictureFacer  implements Parcelable {

    private String picturName;
    private String picturePath;
    private  String pictureSize;
    private  String imageUri;
    private Integer date;

    public pictureFacer(Parcel in) {
        picturName = in.readString();
        picturePath = in.readString();
        pictureSize = in.readString();
        imageUri = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readInt();
        }
        byte tmpSelected = in.readByte();
        selected = tmpSelected == 0 ? null : tmpSelected == 1;
    }


    public static final Creator<pictureFacer> CREATOR = new Creator<pictureFacer>() {
        @Override
        public pictureFacer createFromParcel(Parcel in) {
            return new pictureFacer(in);
        }

        @Override
        public pictureFacer[] newArray(int size) {
            return new pictureFacer[size];
        }
    };

    public pictureFacer(String filename, String path, String absolutePath) {
        this.picturName=filename;
        this.picturePath=path;
        this.imageUri=absolutePath;
    }

    public pictureFacer(String ss) {
        this.imageUri=ss;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    private Boolean selected = false;

    public pictureFacer(){

    }

    public pictureFacer(String picturName, String picturePath, String pictureSize, String imageUri,int date) {
        this.picturName = picturName;
        this.picturePath = picturePath;
        this.pictureSize = pictureSize;
        this.imageUri = imageUri;
        this.date=date;
    }


    public String getPicturName() {
        return picturName;
    }

    public void setPicturName(String picturName) {
        this.picturName = picturName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(String pictureSize) {
        this.pictureSize = pictureSize;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(picturName);
        parcel.writeString(picturePath);
        parcel.writeString(pictureSize);
        parcel.writeString(imageUri);
        if (date == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(date);
        }
        parcel.writeByte((byte) (selected == null ? 0 : selected ? 1 : 2));
    }
}
