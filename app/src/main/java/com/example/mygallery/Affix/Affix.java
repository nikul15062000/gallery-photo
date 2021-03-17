package com.example.mygallery.Affix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Affix {
    private static final String DIRECTORY_NAME = "AffixedPictures";

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$CompressFormat = new int[CompressFormat.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Bitmap$CompressFormat[CompressFormat.JPEG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$CompressFormat[CompressFormat.PNG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$CompressFormat[CompressFormat.WEBP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static class Options {
        private String folderPath = null;
        private CompressFormat format = CompressFormat.JPEG;
        private int quality = 90;
        private boolean vertical = false;

        public Options(String folderPath, CompressFormat format, int quality, boolean vertical) {
            this.folderPath = folderPath;
            this.format = format;
            this.quality = quality;
            this.vertical = vertical;
        }

        String getFolderPath() {
            return this.folderPath;
        }

        public boolean isVertical() {
            return this.vertical;
        }

        public CompressFormat getFormat() {
            return this.format;
        }

        String getExtensionFormat() {
            switch (AnonymousClass1.$SwitchMap$android$graphics$Bitmap$CompressFormat[this.format.ordinal()]) {
                case 2:
                    return "png";
                case 3:
                    return "webp";
                default:
                    return "jpg";
            }
        }

        int getQuality() {
            return this.quality;
        }
    }

    public static void AffixBitmapList(Context ctx, ArrayList<Bitmap> bitmapArray, Options options) {
        Bitmap unionBitmap;
        if (options.isVertical()) {
            unionBitmap = Bitmap.createBitmap(getMaxBitmapWidth(bitmapArray), getBitmapsHeight(bitmapArray), Config.ARGB_8888);
        } else {
            unionBitmap = Bitmap.createBitmap(getBitmapsWidth(bitmapArray), getMaxBitmapHeight(bitmapArray), Config.ARGB_8888);
        }
        combineBitmap(new Canvas(unionBitmap), bitmapArray, options.isVertical());
        saveFile(ctx, unionBitmap, options);
    }

    private static Canvas combineBitmap(Canvas cs, ArrayList<Bitmap> bpmList, boolean vertical) {
        int i;
        if (vertical) {
            int height = ((Bitmap) bpmList.get(0)).getHeight();
            cs.drawBitmap((Bitmap) bpmList.get(0), 0.0f, 0.0f, null);
            for (i = 1; i < bpmList.size(); i++) {
                cs.drawBitmap((Bitmap) bpmList.get(i), 0.0f, (float) height, null);
                height += ((Bitmap) bpmList.get(i)).getHeight();
            }
        } else {
            int width = ((Bitmap) bpmList.get(0)).getWidth();
            cs.drawBitmap((Bitmap) bpmList.get(0), 0.0f, 0.0f, null);
            for (i = 1; i < bpmList.size(); i++) {
                cs.drawBitmap((Bitmap) bpmList.get(i), (float) width, 0.0f, null);
                width += ((Bitmap) bpmList.get(i)).getWidth();
            }
        }
        return cs;
    }

    private static void saveFile(Context context, Bitmap bmp, Options options) {
        try {
            File file = new File(options.getFolderPath(), System.currentTimeMillis() + "." + options.getExtensionFormat());
            if (file.createNewFile()) {
                OutputStream os = new FileOutputStream(file);
                bmp.compress(options.getFormat(), options.getQuality(), os);
                os.close();
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
            }
        } catch (IOException e) {
            Log.e("combineImages", "problem combining images", e);
        }
    }

    private static int getMaxBitmapWidth(ArrayList<Bitmap> bpmHeightArray) {
        int width = ((Bitmap) bpmHeightArray.get(0)).getWidth();
        for (int i = 1; i < bpmHeightArray.size(); i++) {
            if (width < ((Bitmap) bpmHeightArray.get(i)).getWidth()) {
                width = ((Bitmap) bpmHeightArray.get(i)).getWidth();
            }
        }
        return width;
    }

    private static int getBitmapsWidth(ArrayList<Bitmap> bpmHeightArray) {
        int width = 0;
        for (int i = 0; i < bpmHeightArray.size(); i++) {
            width += ((Bitmap) bpmHeightArray.get(i)).getWidth();
        }
        return width;
    }

    private static int getMaxBitmapHeight(ArrayList<Bitmap> bpmHeightArray) {
        int height = ((Bitmap) bpmHeightArray.get(0)).getHeight();
        for (int i = 1; i < bpmHeightArray.size(); i++) {
            if (height < ((Bitmap) bpmHeightArray.get(i)).getHeight()) {
                height = ((Bitmap) bpmHeightArray.get(i)).getHeight();
            }
        }
        return height;
    }

    private static int getBitmapsHeight(ArrayList<Bitmap> bpmHeightArray) {
        int height = 0;
        for (int i = 0; i < bpmHeightArray.size(); i++) {
            height += ((Bitmap) bpmHeightArray.get(i)).getHeight();
        }
        return height;
    }

    public static String getDefaultDirectoryPath() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + DIRECTORY_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }
}
