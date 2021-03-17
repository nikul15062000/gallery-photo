package com.example.mygallery.videofile;

import android.annotation.TargetApi;
import androidx.annotation.RequiresApi;
import android.view.KeyEvent;

@TargetApi(18)
@RequiresApi(18)
interface TransportMediatorCallback {
    long getPlaybackPosition();

    void handleAudioFocusChange(int i);

    void handleKey(KeyEvent keyEvent);

    void playbackPositionUpdate(long j);
}
