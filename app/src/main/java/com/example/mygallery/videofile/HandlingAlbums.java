package com.example.mygallery.videofile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;

import com.example.mygallery.R;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class HandlingAlbums {
    public static final String TAG = "HandlingAlbums";
    private static String backupFile = "albums.dat";
    private PreferenceUtil SP;
    private int current = 0;
    public ArrayList<Album> dispAlbums;
    private boolean hidden;
    private ArrayList<Album> selectedAlbums;

    public HandlingAlbums(Context context) {
        this.SP = PreferenceUtil.getInstance(context);
        this.dispAlbums = new ArrayList();
        this.selectedAlbums = new ArrayList();
    }

    public void loadAlbums(Context context, boolean hidden) {
        this.hidden = hidden;
        ArrayList<Album> list = new ArrayList();
        if (this.SP.getBoolean(context.getString(R.string.preference_use_alternative_provider), false)) {
            list = new StorageProvider(context).getAlbums(context, hidden);
        } else {
            list.addAll(MediaStoreProvider.getAlbums(context, hidden));
        }
        this.dispAlbums = list;
        sortAlbums(context);
    }

    public void addAlbum(int position, Album album) {
        this.dispAlbums.add(position, album);
        setCurrentAlbum(album);
    }

    public void setCurrentAlbum(Album album) {
        this.current = this.dispAlbums.indexOf(album);
    }

    public Album getCurrentAlbum() {
        return (Album) this.dispAlbums.get(this.current);
    }

    public void saveBackup(final Context context) {
        if (!this.hidden) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(new File(context.getCacheDir(), HandlingAlbums.backupFile)));
                        objectOutStream.writeObject(HandlingAlbums.this.dispAlbums);
                        objectOutStream.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e12) {
                        e12.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void addAlbumToBackup(final Context context, final Album album) {
        new Thread(new Runnable() {
            public void run() {
                boolean success = false;
                try {
                    File f = new File(context.getCacheDir(), HandlingAlbums.backupFile);
                    Object o = new ObjectInputStream(new FileInputStream(f)).readObject();
                    ArrayList<Album> list = null;
                    if (o != null) {
                        list = (ArrayList) o;
                        for (int i = 0; i < list.size(); i++) {
                            if (((Album) list.get(i)).equals(album)) {
                                list.remove(i);
                                list.add(i, album);
                                success = true;
                            }
                        }
                    }
                    if (success) {
                        ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(f));
                        objectOutStream.writeObject(list);
                        objectOutStream.close();
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e12) {
                    e12.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void restoreBackup(Context context) {
        try {
            ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(new File(context.getCacheDir(), backupFile)));
            this.dispAlbums = (ArrayList) objectInStream.readObject();
            objectInStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e12) {
            e12.printStackTrace();
        } catch (OptionalDataException e13) {
            e13.printStackTrace();
        } catch (StreamCorruptedException e14) {
            e14.printStackTrace();
        } catch (IOException e15) {
            e15.printStackTrace();
        }
    }

    private int toggleSelectAlbum(int index) {
        if (this.dispAlbums.get(index) != null) {
            ((Album) this.dispAlbums.get(index)).setSelected(!((Album) this.dispAlbums.get(index)).isSelected());
            if (((Album) this.dispAlbums.get(index)).isSelected()) {
                this.selectedAlbums.add(this.dispAlbums.get(index));
            } else {
                this.selectedAlbums.remove(this.dispAlbums.get(index));
            }
        }
        return index;
    }

    public int toggleSelectAlbum(Album album) {
        return toggleSelectAlbum(this.dispAlbums.indexOf(album));
    }

    public Album getAlbum(int index) {
        return (Album) this.dispAlbums.get(index);
    }

    public void selectAllAlbums() {
        Iterator it = this.dispAlbums.iterator();
        while (it.hasNext()) {
            Album dispAlbum = (Album) it.next();
            if (!dispAlbum.isSelected()) {
                dispAlbum.setSelected(true);
                this.selectedAlbums.add(dispAlbum);
            }
        }
    }

    public void removeCurrentAlbum() {
        this.dispAlbums.remove(this.current);
    }

    public int getSelectedCount() {
        return this.selectedAlbums.size();
    }

    public void clearSelectedAlbums() {
        Iterator it = this.dispAlbums.iterator();
        while (it.hasNext()) {
            ((Album) it.next()).setSelected(false);
        }
        this.selectedAlbums.clear();
    }

    public void installShortcutForSelectedAlbums(Context appCtx) {
        Iterator it = this.selectedAlbums.iterator();
        while (it.hasNext()) {
            Bitmap bitmap;
            Album selectedAlbum = (Album) it.next();
            Intent shortcutIntent = new Intent(appCtx, SplashScreen.class);
            shortcutIntent.setAction(SplashScreen.ACTION_OPEN_ALBUM);
            shortcutIntent.putExtra("albumPath", selectedAlbum.getPath());
            shortcutIntent.putExtra("albumId", selectedAlbum.getId());
            shortcutIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shortcutIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Intent addIntent = new Intent();
            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
            addIntent.putExtra("android.intent.extra.shortcut.NAME", selectedAlbum.getName());
            File image = new File(selectedAlbum.getCoverAlbum().getPath());
            String mime = StringUtils.getMimeType(image.getAbsolutePath());
            if (mime.startsWith("image")) {
                bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), new Options());
            } else if (mime.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
                bitmap = ThumbnailUtils.createVideoThumbnail(selectedAlbum.getCoverAlbum().getPath(), 1);
            } else {
                return;
            }
            addIntent.putExtra("android.intent.extra.shortcut.ICON", addWhiteBorder(Bitmap.createScaledBitmap(getCroppedBitmap(bitmap), 128, 128, false), 5));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            appCtx.sendBroadcast(addIntent);
        }
    }

    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + (borderSize * 2), bmp.getHeight() + (borderSize * 2), bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(-1);
        canvas.drawBitmap(bmp, (float) borderSize, (float) borderSize, null);
        return bmpWithBorder;
    }

    private Bitmap getCroppedBitmap(Bitmap srcBmp) {
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            return Bitmap.createBitmap(srcBmp, (srcBmp.getWidth() / 2) - (srcBmp.getHeight() / 2), 0, srcBmp.getHeight(), srcBmp.getHeight());
        }
        return Bitmap.createBitmap(srcBmp, 0, (srcBmp.getHeight() / 2) - (srcBmp.getWidth() / 2), srcBmp.getWidth(), srcBmp.getWidth());
    }

    private void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path, null, null);
    }

    public void hideAlbum(String path, Context context) {
        File file = new File(new File(path), ".nomedia");
        if (!file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                out.flush();
                out.close();
                scanFile(context, new String[]{file.getAbsolutePath()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void hideSelectedAlbums(Context context) {
        Iterator it = this.selectedAlbums.iterator();
        while (it.hasNext()) {
            hideAlbum((Album) it.next(), context);
        }
        clearSelectedAlbums();
    }

    private void hideAlbum(Album a, Context context) {
        hideAlbum(a.getPath(), context);
        this.dispAlbums.remove(a);
    }

    public void unHideAlbum(String path, Context context) {
        File file = new File(new File(path), ".nomedia");
        if (file.exists() && file.delete()) {
            scanFile(context, new String[]{file.getAbsolutePath()});
        }
    }

    public void unHideSelectedAlbums(Context context) {
        Iterator it = this.selectedAlbums.iterator();
        while (it.hasNext()) {
            unHideAlbum((Album) it.next(), context);
        }
        clearSelectedAlbums();
    }

    private void unHideAlbum(Album a, Context context) {
        unHideAlbum(a.getPath(), context);
        this.dispAlbums.remove(a);
    }

    public boolean deleteSelectedAlbums(Context context) {
        boolean success = true;
        Iterator it = this.selectedAlbums.iterator();
        while (it.hasNext()) {
            Album selectedAlbum = (Album) it.next();
            int index = this.dispAlbums.indexOf(selectedAlbum);
            if (deleteAlbum(selectedAlbum, context)) {
                this.dispAlbums.remove(index);
            } else {
                success = false;
            }
        }
        return success;
    }

    File file;
    public boolean deleteAlbum(Album album, Context context) {
        file=new File(album.getPath());
        return ContentHelper.deleteFilesInFolder(context, new File(album.getPath()));
    }

    public void excludeSelectedAlbums(Context context) {
        Iterator it = this.selectedAlbums.iterator();
        while (it.hasNext()) {
            excludeAlbum(context, (Album) it.next());
        }
        clearSelectedAlbums();
    }

    private void excludeAlbum(Context context, Album a) {
        CustomAlbumsHelper.getInstance(context).excludeAlbum(a.getPath());
        this.dispAlbums.remove(a);
    }

    public SortingMode getSortingMode() {
        return SortingMode.fromValue(this.SP.getInt("albums_sorting_mode", SortingMode.DATE.getValue()));
    }

    public SortingOrder getSortingOrder() {
        return SortingOrder.fromValue(this.SP.getInt("albums_sorting_order", SortingOrder.DESCENDING.getValue()));
    }

    public void setDefaultSortingMode(SortingMode sortingMode) {
        this.SP.putInt("albums_sorting_mode", sortingMode.getValue());
    }

    public void setDefaultSortingAscending(SortingOrder sortingOrder) {
        this.SP.putInt("albums_sorting_order", sortingOrder.getValue());
    }

    public void sortAlbums(Context context) {
        Album camera = null;
        Iterator it = this.dispAlbums.iterator();
        while (it.hasNext()) {
            Album album = (Album) it.next();
            if (album.getName().equals("Camera") && this.dispAlbums.remove(album)) {
                camera = album;
                break;
            }
        }
        Collections.sort(this.dispAlbums, AlbumsComparators.getComparator(getSortingMode(), getSortingOrder()));
        if (camera != null) {
            camera.setName(context.getString(R.string.camera));
            this.dispAlbums.add(0, camera);
        }
    }

    public Album getSelectedAlbum(int index) {
        return (Album) this.selectedAlbums.get(index);
    }

    public void loadAlbums(Context applicationContext) {
        loadAlbums(applicationContext, this.hidden);
    }
}
