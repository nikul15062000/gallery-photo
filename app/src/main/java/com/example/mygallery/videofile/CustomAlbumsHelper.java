package com.example.mygallery.videofile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class CustomAlbumsHelper extends SQLiteOpenHelper {
    private static final String ALBUM_COLUMN_COUNT = "sorting_order";
    private static final String ALBUM_COVER_PATH = "cover_path";
    private static final String ALBUM_EXCLUDED = "excluded";
    private static final String ALBUM_PATH = "path";
    private static final String ALBUM_PINNED = "pinned";
    private static final String ALBUM_SORTING_MODE = "sorting_mode";
    private static final String ALBUM_SORTING_ORDER = "sort_ascending";
    private static final String DATABASE_NAME = "album_settings.db";
    private static final int DATABASE_VERSION = 16;
    private static final String TABLE_ALBUMS = "albums";
    private static CustomAlbumsHelper instance;

    public static CustomAlbumsHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (CustomAlbumsHelper.class) {
                if (instance == null) {
                    instance = new CustomAlbumsHelper(context);
                }
            }
        }
        return instance;
    }

    private CustomAlbumsHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, 16);
    }

    public void onCreate(SQLiteDatabase db) {
        createAlbumsTable(db);
        ContentValues values = new ContentValues();
        values.put(ALBUM_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        values.put(ALBUM_EXCLUDED, Integer.valueOf(1));
        db.insert(TABLE_ALBUMS, null, values);
    }

    private void createAlbumsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE albums(path TEXT,excluded INTEGER,pinned INTEGER,cover_path TEXT, sorting_mode INTEGER, sort_ascending INTEGER, sorting_order TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS albums");
        onCreate(db);
    }

    private void checkAndCreateAlbum(SQLiteDatabase db, String path) {
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(TABLE_ALBUMS, null, "path=?", new String[]{path}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(ALBUM_PATH, path);
            values.put(ALBUM_SORTING_MODE, Integer.valueOf(SortingMode.DATE.getValue()));
            values.put(ALBUM_SORTING_ORDER, Integer.valueOf(SortingOrder.DESCENDING.getValue()));
            values.put(ALBUM_EXCLUDED, Integer.valueOf(0));
            db.insert(TABLE_ALBUMS, null, values);
        }
        cursor.close();
    }

    AlbumSettings getSettings(String path) {
        AlbumSettings s;
        SQLiteDatabase db = getWritableDatabase();
        checkAndCreateAlbum(db, path);
        Cursor cursor = db.query(TABLE_ALBUMS, new String[]{ALBUM_COVER_PATH, ALBUM_SORTING_MODE, ALBUM_SORTING_ORDER, ALBUM_PINNED}, "path=?", new String[]{path}, null, null, null);
        if (cursor.moveToFirst()) {
            s = new AlbumSettings(path, cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
        } else {
            s = null;
        }
        cursor.close();
        db.close();
        return s;
    }

    public ArrayList<File> getExcludedFolders() {
        ArrayList<File> list = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ALBUMS, new String[]{ALBUM_PATH}, "excluded=1", null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                list.add(new File(cur.getString(0)));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        return list;
    }

    public ArrayList<String> getExcludedFoldersPaths() {
        ArrayList<String> list = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ALBUMS, new String[]{ALBUM_PATH}, "excluded=1", null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                list.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        return list;
    }



    public void excludeAlbum(String path) {
        SQLiteDatabase db = getWritableDatabase();
        checkAndCreateAlbum(db, path);
        ContentValues values = new ContentValues();
        values.put(ALBUM_EXCLUDED, Integer.valueOf(1));
        db.update(TABLE_ALBUMS, values, "path=?", new String[]{path});
        db.close();
    }


    void setAlbumSortingMode(String path, int column) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALBUM_SORTING_MODE, Integer.valueOf(column));
        db.update(TABLE_ALBUMS, values, "path=?", new String[]{path});
        db.close();
    }

    void setAlbumSortingOrder(String path, int sortingOrder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALBUM_SORTING_ORDER, Integer.valueOf(sortingOrder));
        db.update(TABLE_ALBUMS, values, "path=?", new String[]{path});
        db.close();
    }
}
