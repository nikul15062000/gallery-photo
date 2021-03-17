package com.example.mygallery.videofile;

import java.util.Comparator;

;

public class AlbumsComparators {
    public static Comparator<Album> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {
        switch (sortingMode) {
            case NAME:
                return getNameComparator(sortingOrder);
            case SIZE:
                return getSizeComparator(sortingOrder);
            case NUMERIC:
                return getNumericComparator(sortingOrder);
            default:
                return getDateComparator(sortingOrder);
        }
    }

    private static Comparator<Album> getDateComparator(final SortingOrder sortingOrder) {
        return new Comparator<Album>() {
            public int compare(Album a1, Album a2) {
                int i = 1;
                switch (sortingOrder) {
                    case ASCENDING:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a1.getMedia(0).getDateModified().compareTo(a2.getMedia(0).getDateModified());
                        }
                        if (a1.isPinned()) {
                            return -1;
                        }
                        return 1;
                    default:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a2.getMedia(0).getDateModified().compareTo(a1.getMedia(0).getDateModified());
                        }
                        if (!a2.isPinned()) {
                            i = -1;
                        }
                        return i;
                }
            }
        };
    }

    private static Comparator<Album> getNameComparator(final SortingOrder sortingOrder) {
        return new Comparator<Album>() {
            public int compare(Album a1, Album a2) {
                int i = 1;
                switch (sortingOrder) {
                    case ASCENDING:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a1.getName().toLowerCase().compareTo(a2.getName().toLowerCase());
                        }
                        if (a1.isPinned()) {
                            return -1;
                        }
                        return 1;
                    default:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a2.getName().toLowerCase().compareTo(a1.getName().toLowerCase());
                        }
                        if (!a2.isPinned()) {
                            i = -1;
                        }
                        return i;
                }
            }
        };
    }

    private static Comparator<Album> getSizeComparator(final SortingOrder sortingOrder) {
        return new Comparator<Album>() {
            public int compare(Album a1, Album a2) {
                int i = 1;
                switch (sortingOrder) {
                    case ASCENDING:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a1.getCount() - a2.getCount();
                        }
                        if (a1.isPinned()) {
                            return -1;
                        }
                        return 1;
                    default:
                        if (a1.isPinned() == a2.isPinned()) {
                            return a2.getCount() - a1.getCount();
                        }
                        if (!a2.isPinned()) {
                            i = -1;
                        }
                        return i;
                }
            }
        };
    }

    private static Comparator<Album> getNumericComparator(final SortingOrder sortingOrder) {
        return new Comparator<Album>() {
            public int compare(Album a1, Album a2) {
                int i = 1;
                switch (sortingOrder) {
                    case ASCENDING:
                        if (a1.isPinned() == a2.isPinned()) {
                            return NumericComparator.filevercmp(a1.getName().toLowerCase(), a2.getName().toLowerCase());
                        }
                        if (a1.isPinned()) {
                            return -1;
                        }
                        return 1;
                    default:
                        if (a1.isPinned() == a2.isPinned()) {
                            return NumericComparator.filevercmp(a2.getName().toLowerCase(), a1.getName().toLowerCase());
                        }
                        if (!a2.isPinned()) {
                            i = -1;
                        }
                        return i;
                }
            }
        };
    }
}
