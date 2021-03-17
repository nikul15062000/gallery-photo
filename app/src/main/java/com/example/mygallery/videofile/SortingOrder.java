package com.example.mygallery.videofile;

public enum SortingOrder {
    ASCENDING(0),
    DESCENDING(1);
    
    int value;

    private SortingOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SortingOrder fromValue(int value) {
        switch (value) {
            case 0:
                return ASCENDING;
            default:
                return DESCENDING;
        }
    }
}
