package com.example.mygallery.Statusaver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.mygallery.R;
import com.example.mygallery.Statusaver.fragments.Utils;


public class VideoPreviewActivity extends AppCompatActivity {

	VideoView displayVV;
	ImageView backIV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saver_activity_video_preview);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		backIV = findViewById(R.id.backIV);
		backIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		    displayVV = (VideoView) findViewById(R.id.displayVV);
			displayVV.setVideoPath(Utils.mPath);
			displayVV.start();

	}


	@Override
    protected void onResume() {
        super.onResume();
            displayVV.setVideoPath(Utils.mPath);
            displayVV.start();
    }


}
