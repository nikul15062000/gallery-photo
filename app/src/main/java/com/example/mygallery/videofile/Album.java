package com.example.mygallery.videofile;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.example.mygallery.Adapter.MediaAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class Album implements Serializable {
    private int count;
    private int currentMediaIndex;
    private boolean found_id_album;
    private long id;
    private ArrayList<Media> media;
    private String name;
    private String path;
    private boolean selected;
    private ArrayList<Media> selectedMedias;
    public AlbumSettings settings;

    private Album() {
        this.name = null;
        this.path = null;
        this.id = -1;
        this.count = -1;
        this.currentMediaIndex = 0;
        this.selected = false;
        this.settings = null;
        this.found_id_album = false;
        this.media = new ArrayList();
        this.selectedMedias = new ArrayList();
    }

    public Album(Context context, String path, long id, String name, int count) {
        this();
        this.path = path;
        this.name = name;
        this.count = count;
        this.id = id;
        this.settings = AlbumSettings.getSettings(context, this);
    }

    public Album(Context context, @NotNull File mediaPath) {
        this.name = null;
        this.path = null;
        this.id = -1;
        this.count = -1;
        this.currentMediaIndex = 0;
        this.selected = false;
        this.settings = null;
        this.found_id_album = false;
        File folder = mediaPath.getParentFile();
        this.path = folder.getPath();
        this.name = folder.getName();
        this.settings = AlbumSettings.getSettings(context, this);
        updatePhotos(context);
        setCurrentPhoto(mediaPath.getAbsolutePath());
    }

    public Album(Context context, Uri mediaUri) {
        this.name = null;
        this.path = null;
        this.id = -1;
        this.count = -1;
        this.currentMediaIndex = 0;
        this.selected = false;
        this.settings = null;
        this.found_id_album = false;
        this.media.add(0, new Media(context, mediaUri));
        setCurrentPhotoIndex(0);
    }

    public static Album getEmptyAlbum() {
        Album album = new Album();
        album.settings = AlbumSettings.getDefaults();
        return album;
    }

    public ArrayList<Media> getMedia() {
        ArrayList<Media> mediaArrayList = new ArrayList();
        Iterator it;
        Media media1;
        switch (getFilterMode()) {
            case ALL:
                return this.media;
            case GIF:
                it = this.media.iterator();
                while (it.hasNext()) {
                    media1 = (Media) it.next();
                    if (media1.isGif()) {
                        mediaArrayList.add(media1);
                    }
                }
                return mediaArrayList;
            case IMAGES:
                it = this.media.iterator();
                while (it.hasNext()) {
                    media1 = (Media) it.next();
                    if (media1.isImage()) {
                        mediaArrayList.add(media1);
                    }
                }
                return mediaArrayList;
            case VIDEO:
                it = this.media.iterator();
                while (it.hasNext()) {
                    media1 = (Media) it.next();
                    if (media1.isVideo()) {
                        mediaArrayList.add(media1);
                    }
                }
                return mediaArrayList;
            default:
                return mediaArrayList;
        }
    }

    public void updatePhotos(Context context) {
        this.media = getMedia(context);
        sortPhotos();
        setCount(this.media.size());
    }


    private ArrayList<Media> getMedia(Context context) {
        PreferenceUtil SP = PreferenceUtil.getInstance(context);
        ArrayList<Media> mediaArrayList = new ArrayList();
        if (isFromMediaStore()) {
            mediaArrayList.addAll(MediaStoreProvider.getMedia(context, this.id, SP.getBoolean("set_include_video", true)));
        } else {
            mediaArrayList.addAll(StorageProvider.getMedia(getPath(), SP.getBoolean("set_include_video", true)));
        }
        return mediaArrayList;
    }

    public ArrayList<Media> getSelectedMedia() {
        return this.selectedMedias;
    }

    public Media getSelectedMedia(int index) {
        return (Media) this.selectedMedias.get(index);
    }

    private boolean isFromMediaStore() {
        return this.id != -1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return this.id;
    }


    public boolean isPinned() {
        return this.settings.isPinned();
    }


    public boolean addMedia(@Nullable Media media) {
        if (media == null) {
            return false;
        }
        this.media.add(media);
        return true;
    }

    public boolean hasCustomCover() {
        return this.settings.getCoverPath() != null;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Media getMedia(int index) {
        return (Media) this.media.get(index);
    }

    public void setCurrentPhotoIndex(int index) {
        this.currentMediaIndex = index;
    }

    public void setCurrentPhotoIndex(Media m) {
        setCurrentPhotoIndex(this.media.indexOf(m));
    }


    public int getCurrentMediaIndex() {
        return this.currentMediaIndex;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    private void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }



    public Media getCoverAlbum() {
        if (hasCustomCover()) {
            return new Media(this.settings.getCoverPath());
        }
        if (this.media.size() > 0) {
            return (Media) this.media.get(0);
        }
        return new Media();
    }


    private void setCurrentPhoto(String path) {
        for (int i = 0; i < this.media.size(); i++) {
            if (((Media) this.media.get(i)).getPath().equals(path)) {
                this.currentMediaIndex = i;
            }
        }
    }

    public int getSelectedCount() {
        return this.selectedMedias.size();
    }

    public boolean areMediaSelected() {
        return getSelectedCount() != 0;
    }

    public void selectAllPhotos() {
        for (int i = 0; i < this.media.size(); i++) {
            if (!((Media) this.media.get(i)).isSelected()) {
                ((Media) this.media.get(i)).setSelected(true);
                this.selectedMedias.add(this.media.get(i));
            }
        }
    }

    private int toggleSelectPhoto(int index) {
        if (this.media.get(index) != null) {
            ((Media) this.media.get(index)).setSelected(!((Media) this.media.get(index)).isSelected());
            if (((Media) this.media.get(index)).isSelected()) {
                this.selectedMedias.add(this.media.get(index));
            } else {
                this.selectedMedias.remove(this.media.get(index));
            }
        }
        return index;
    }

    public int toggleSelectPhoto(Media m) {
        return toggleSelectPhoto(this.media.indexOf(m));
    }

    public void setDefaultSortingMode(Context context, SortingMode column) {
        this.settings.changeSortingMode(context, column);
    }


    public void setDefaultSortingAscending(Context context, SortingOrder sortingOrder) {
        this.settings.changeSortingOrder(context, sortingOrder);
    }

    public void selectAllPhotosUpTo(int targetIndex, MediaAdapter adapter) {
        int indexRightBeforeOrAfter = -1;
        Iterator it = this.selectedMedias.iterator();
        while (it.hasNext()) {
            int indexNow = this.media.indexOf((Media) it.next());
            if (indexRightBeforeOrAfter == -1) {
                indexRightBeforeOrAfter = indexNow;
            }
            if (indexNow > targetIndex) {
                break;
            }
            indexRightBeforeOrAfter = indexNow;
        }
        if (indexRightBeforeOrAfter != -1) {
            int index = Math.min(targetIndex, indexRightBeforeOrAfter);
            while (index <= Math.max(targetIndex, indexRightBeforeOrAfter)) {
                if (!(this.media.get(index) == null || ((Media) this.media.get(index)).isSelected())) {
                    ((Media) this.media.get(index)).setSelected(true);
                    this.selectedMedias.add(this.media.get(index));
                    adapter.notifyItemChanged(index);
                }
                index++;
            }
        }
    }

    public int getIndex(Media m) {
        return this.media.indexOf(m);
    }

    public void clearSelectedPhotos() {
        Iterator it = this.media.iterator();
        while (it.hasNext()) {
            ((Media) it.next()).setSelected(false);
        }
        this.selectedMedias.clear();
    }

    public void sortPhotos() {
        Collections.sort(this.media, MediaComparators.getComparator(this.settings.getSortingMode(), this.settings.getSortingOrder()));
    }




    private boolean deleteMedia(Context context, Media media) {
        boolean success = ContentHelper.deleteFile(context, new File(media.getPath()));
        if (success) {
            File file=new File(media.getPath());
            scanFile(context, new String[]{file.getPath()});
        }
        return success;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Album) {
            return getPath().equals(((Album) obj).getPath());
        }
        return super.equals(obj);
    }

    public boolean deleteSelectedMedia(Context context) {
        boolean success = true;
        Iterator it = this.selectedMedias.iterator();
        while (it.hasNext()) {
            Media selectedMedia = (Media) it.next();
            if (deleteMedia(context, selectedMedia)) {
                this.media.remove(selectedMedia);
            } else {
                success = false;
            }
        }
        if (success) {
            clearSelectedPhotos();
            setCount(this.media.size());
        }
        return success;
    }


    public void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path, null, null);
    }


    public FilterMode getFilterMode() {
        return this.settings != null ? this.settings.getFilterMode() : FilterMode.ALL;
    }
}
