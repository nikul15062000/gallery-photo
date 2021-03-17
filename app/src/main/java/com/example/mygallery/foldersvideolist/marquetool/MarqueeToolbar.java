package com.example.mygallery.foldersvideolist.marquetool;

import android.content.Context;

import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;

public class MarqueeToolbar extends Toolbar {
    boolean reflected = false;
    TextView title;

    public MarqueeToolbar(Context context) {
        super(context);
    }

    public MarqueeToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(CharSequence title) {
        if (!this.reflected) {
            this.reflected = reflectTitle();
        }
        super.setTitle(title);
        selectTitle();
    }

    public void setTitle(int resId) {
        if (!this.reflected) {
            this.reflected = reflectTitle();
        }
        super.setTitle(resId);
        selectTitle();
    }

    private boolean reflectTitle() {
        try {
            Field field = Toolbar.class.getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            this.title = (TextView) field.get(this);
            this.title.setEllipsize(TruncateAt.MARQUEE);
            this.title.setMarqueeRepeatLimit(-1);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return false;
        } catch (NullPointerException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public void selectTitle() {
        if (this.title != null) {
            this.title.setSelected(true);
        }
    }
}
