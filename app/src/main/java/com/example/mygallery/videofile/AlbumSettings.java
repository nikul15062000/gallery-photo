package com.example.mygallery.videofile;

import android.content.Context;
/*import androidx.annotation.Nullable;*/

import java.io.Serializable;


public class AlbumSettings implements Serializable {
    private String coverPath;
    private FilterMode filterMode = FilterMode.ALL;
    private String path;
    private boolean pinned;
    private int sortingMode;
    private int sortingOrder;

    static AlbumSettings getSettings(Context context, Album album) {
        return CustomAlbumsHelper.getInstance(context).getSettings(album.getPath());
    }

    static AlbumSettings getDefaults() {
        return new AlbumSettings(null, null, SortingMode.DATE.getValue(), SortingOrder.DESCENDING.getValue(), 0);
    }

    AlbumSettings(String path, String cover, int sortingMode, int sortingOrder, int pinned) {
        boolean z = true;
        this.path = path;
        this.coverPath = cover;
        this.sortingMode = sortingMode;
        this.sortingOrder = sortingOrder;
        if (pinned != 1) {
            z = false;
        }
        this.pinned = z;
    }

    FilterMode getFilterMode() {
        return this.filterMode;
    }



    String getCoverPath() {
        return this.coverPath;
    }

    public SortingMode getSortingMode() {
        return SortingMode.fromValue(this.sortingMode);
    }

    public SortingOrder getSortingOrder() {
        return SortingOrder.fromValue(this.sortingOrder);
    }

    void changeSortingMode(Context context, SortingMode sortingMode) {
        this.sortingMode = sortingMode.getValue();
        CustomAlbumsHelper.getInstance(context).setAlbumSortingMode(this.path, sortingMode.getValue());
    }

    void changeSortingOrder(Context context, SortingOrder sortingOrder) {
        this.sortingOrder = sortingOrder.getValue();
        CustomAlbumsHelper.getInstance(context).setAlbumSortingOrder(this.path, sortingOrder.getValue());
    }



    boolean isPinned() {
        return this.pinned;
    }


}
