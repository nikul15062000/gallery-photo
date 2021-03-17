package com.example.mygallery.foldersvideolist.marquetool;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.mygallery.R;
import com.example.mygallery.foldersvideolist.FileDataHelper;
import com.example.mygallery.foldersvideolist.MediaFolder;


public class MediaFolderPropertiesDialog extends Dialog implements OnClickListener {
    public Activity c;
    MediaFolder propertiesModel;

    public MediaFolderPropertiesDialog(Activity a, MediaFolder propertiesModel) {
        super(a);
        this.c = a;
        this.propertiesModel = propertiesModel;
        show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.media_folder_properties_dialog);
        findViewById(R.id.ok_button).setOnClickListener(this);
        setView(this.propertiesModel);
        setDialogPosition();
        setCancelable(true);
    }

    public void setView(MediaFolder mediaFolder) {
        ((TextView) findViewById(R.id.dialog_title)).setText(mediaFolder.getDisplayName());
        ((TextView) findViewById(R.id.file_location)).setText(mediaFolder.getPath().substring(0, mediaFolder.getPath().lastIndexOf("/")));
        ((TextView) findViewById(R.id.file_duration)).setText(FileDataHelper.getFileDurationFormated(mediaFolder.getMediaDuration()));
        ((TextView) findViewById(R.id.file_size)).setText(FileDataHelper.getFileSize(mediaFolder.getMediaSize()));
        ((TextView) findViewById(R.id.no_of_files)).setText("" + mediaFolder.getNumberOfMediaFiles());
    }

    public void setDialogPosition() {
        Window window = getWindow();
        window.setGravity(17);
        LayoutParams params = window.getAttributes();
        params.width = this.c.getResources().getDisplayMetrics().widthPixels - ((this.c.getResources().getDisplayMetrics().widthPixels * 8) / 100);
        window.setAttributes(params);
    }

    public void onClick(View v) {
        v.getId();
        dismiss();
    }
}
