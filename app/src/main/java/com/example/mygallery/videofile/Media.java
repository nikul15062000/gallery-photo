package com.example.mygallery.videofile;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.drew.lang.annotations.NotNull;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.File;
import java.io.Serializable;


public class Media implements Parcelable, Serializable {
    public static final Creator<Media> CREATOR = new Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
    private long dateModified;
    private int duration;
    private MetadataItem metadata;
    private String mimeType;
    private int orientation;
    private String path;
    private boolean selected;
    private long size;
    private String uri;

    public Media() {
        this.path = null;
        this.dateModified = -1;
        this.mimeType = null;
        this.orientation = 0;
        this.duration = 0;
        this.uri = null;
        this.size = 0;
        this.selected = false;
    }

    public Media(String path, long dateModified) {
        this.path = null;
        this.dateModified = -1;
        this.mimeType = null;
        this.orientation = 0;
        this.duration = 0;
        this.uri = null;
        this.size = 0;
        this.selected = false;
        this.path = path;
        this.dateModified = dateModified;
        this.mimeType = StringUtils.getMimeType(path);
    }

    public Media(File file) {
        this(file.getPath(), file.lastModified());
        this.size = file.length();
        this.mimeType = StringUtils.getMimeType(this.path);
    }

    public Media(String path) {
        this(path, -1);
    }

    public Media(Context context, Uri mediaUri) {
        this.path = null;
        this.dateModified = -1;
        this.mimeType = null;
        this.orientation = 0;
        this.duration = 0;
        this.uri = null;
        this.size = 0;
        this.selected = false;
        this.uri = mediaUri.toString();
        this.path = null;
        this.mimeType = context.getContentResolver().getType(getUri());
    }

    public Media(@NotNull Cursor cur) {
        this.path = null;
        this.dateModified = -1;
        this.mimeType = null;
        this.orientation = 0;
        this.duration = 0;
        this.uri = null;
        this.size = 0;
        this.selected = false;
        this.path = cur.getString(0);
        this.dateModified = cur.getLong(1);
        this.mimeType = cur.getString(2);
        this.size = cur.getLong(3);
        this.orientation = cur.getInt(4);
        this.duration = cur.getInt(5);
    }


    public void setUri(String uriString) {
        this.uri = uriString;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public boolean isSelected() {
        return this.selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isGif() {
        return this.mimeType.endsWith("gif");
    }

    public boolean isImage() {
        return this.mimeType.startsWith("image");
    }

    public boolean isVideo() {
        return this.mimeType.startsWith(MimeTypes.BASE_TYPE_VIDEO);
    }

    public Uri getUri() {
        return this.uri != null ? Uri.parse(this.uri) : Uri.fromFile(new File(this.path));
    }

    public String getName() {
        return StringUtils.getPhotoNameByPath(this.path);
    }

    public long getSize() {
        return this.size;
    }

    public String getPath() {
        return this.path;
    }

    public Long getDateModified() {
        return Long.valueOf(this.dateModified);
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(this.path, new Options());
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
    }


    public File getFile() {
        if (this.path != null) {
            return new File(this.path);
        }
        return null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.dateModified);
        dest.writeString(this.mimeType);
        dest.writeLong(this.size);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Media(Parcel in) {
        boolean z = false;
        this.path = null;
        this.dateModified = -1;
        this.mimeType = null;
        this.orientation = 0;
        this.duration = 0;
        this.uri = null;
        this.size = 0;
        this.selected = false;
        this.path = in.readString();
        this.dateModified = in.readLong();
        this.mimeType = in.readString();
        this.size = in.readLong();
        if (in.readByte() != (byte) 0) {
            z = true;
        }
        this.selected = z;
    }

    public int getOrientation() {
        return this.orientation;
    }
}
