package com.example.mygallery.photo;

import java.io.Serializable;

public class ImageDetails implements Serializable {

    String imagePath;
    String dateTaken;

    public ImageDetails() {
    }

    public ImageDetails(String imagePath, String dateTaken) {
        this.imagePath = imagePath;
        this.dateTaken = dateTaken;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }
}
