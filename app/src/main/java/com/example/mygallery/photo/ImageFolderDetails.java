package com.example.mygallery.photo;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageFolderDetails implements Serializable {

    String folderName;
    ArrayList<ImageDetails> listImage = new ArrayList<>();

    public ImageFolderDetails() {
    }

    public ImageFolderDetails(String folderName, ArrayList<ImageDetails> listImage) {
        this.folderName = folderName;
        this.listImage = listImage;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<ImageDetails> getListImage() {
        return listImage;
    }

    public void setListImage(ArrayList<ImageDetails> listImage) {
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public String toString() {

        return this.folderName;
    }
}
