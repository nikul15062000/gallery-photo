package com.example.mygallery.photo;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Methods {

    private Activity activity;

    public Methods(Activity activity) {
        this.activity = activity;
    }

    public static ArrayList<ImageFolderDetails> listImageFolder = new ArrayList<>();

    DataListener dataListener;

    public interface DataListener {
        public void onDataLoaded();
    }

    public static void getAllImages(Activity activity, DataListener dataListener) {

        try {
            Methods methods = new Methods(activity);
            methods.dataListener = dataListener;

            listImageFolder.clear();

            String[] projection = new String[]{"_data", "_id", "bucket_display_name", "bucket_id", "datetaken"};

            Cursor cur = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "datetaken DESC");

            if (cur.moveToFirst()) {

                int bucketColumn = cur.getColumnIndex("bucket_display_name");
                int bucketIdColumn = cur.getColumnIndex("bucket_id");
                int dateColumn = cur.getColumnIndex("datetaken");

                do {

                    String imagePath = cur.getString(cur.getColumnIndex("_data"));

                    if (!imagePath.endsWith(".gif")) {

                        String date = cur.getString(dateColumn);
                        String folderName = cur.getString(bucketColumn);
                        String folderId = cur.getString(bucketIdColumn);

                        ArrayList<ImageDetails> listImage = new ArrayList<>();

                        ImageDetails imageDetails = new ImageDetails();
                        imageDetails.setImagePath(imagePath);
                        try {
                            imageDetails.setDateTaken(getDateFromTimeStamp(Long.parseLong(date)));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        listImage.add(imageDetails);

                        if (listImageFolder.size() == 0) {

                            listImageFolder.add(new ImageFolderDetails(folderName, listImage));

                        } else {

                            if (folderName != null) {

                                if (listImageFolder.toString().contains(folderName)) {

                                    for (int i = 0; i < listImageFolder.size(); i++) {

                                        if (listImageFolder.get(i).getFolderName().equals(folderName)) {
                                            listImageFolder.get(i).getListImage().add(imageDetails);
                                        }
                                    }

                                } else {
                                    listImageFolder.add(new ImageFolderDetails(folderName, listImage));
                                }
                            }
                        }
                    }

                } while (cur.moveToNext());
            }

            dataListener.onDataLoaded();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static String getDateFromTimeStamp(long time) {

        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));

        return sdf.format(date);
    }
}
