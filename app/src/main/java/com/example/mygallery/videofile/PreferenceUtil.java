package com.example.mygallery.videofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    private static PreferenceUtil instance;
    private SharedPreferences SP;

    private PreferenceUtil(Context mContext) {
        this.SP = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static PreferenceUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (PreferenceUtil.class) {
                if (instance == null) {
                    instance = new PreferenceUtil(context);
                }
            }
        }
        return instance;
    }

    public Editor getEditor() {
        return this.SP.edit();
    }


    public String getString(String key, String defValue) {
        return this.SP.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return this.SP.getInt(key, defValue);
    }


    public boolean getBoolean(String key, boolean defValue) {
        return this.SP.getBoolean(key, defValue);
    }

}
