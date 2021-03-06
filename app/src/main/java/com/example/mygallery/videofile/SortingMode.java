package com.example.mygallery.videofile;

public enum SortingMode {
    NAME(0),
    DATE(1),
    SIZE(2),
    TYPE(3),
    NUMERIC(4);
    
    int value;

    private SortingMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static SortingMode fromValue(int value) {
        switch (value) {
            case 0:
                return NAME;
            case 2:
                return SIZE;
            case 3:
                return TYPE;
            case 4:
                return NUMERIC;
            default:
                return DATE;
        }
    }
}
