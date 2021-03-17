package com.example.mygallery.Affix;

import android.content.Context;

import com.example.mygallery.videofile.PreferenceUtil;
import com.example.mygallery.R;


public class SecurityHelper {
    private boolean activeSecurity;
    private Context context;
    private boolean passwordOnDelete;
    private boolean passwordOnHidden;
    private String passwordValue;

    public SecurityHelper(Context context) {
        this.context = context;
        updateSecuritySetting();
    }




    public void updateSecuritySetting() {
        PreferenceUtil SP = PreferenceUtil.getInstance(this.context);
        this.activeSecurity = SP.getBoolean(this.context.getString(R.string.preference_use_password), false);
        this.passwordOnDelete = SP.getBoolean(this.context.getString(R.string.preference_use_password_on_delete), false);
        this.passwordOnHidden = SP.getBoolean(this.context.getString(R.string.preference_use_password_on_hidden), true);
        this.passwordValue = SP.getString(this.context.getString(R.string.preference_password_value), "");
    }


}
