package com.example.mygallery;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mygallery.videofile.Album;
import com.example.mygallery.videofile.PreferenceUtil;
import com.example.mygallery.videofile.SharedMediaActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class OpenappActivity extends SharedMediaActivity {

    LinearLayout rate, openapp, privacy;
    public  static Bundle b;
    public static final String PICK_MODE = "video";
    private boolean PICK_INTENT = false;
    private PreferenceUtil SP;
    private Album album;
    private Activity activity;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openapp);


        activity = OpenappActivity.this;
        admobNativeAd();

        SP = PreferenceUtil.getInstance(getApplicationContext());
        rate = findViewById(R.id.rate);
        openapp = findViewById(R.id.openapp);
        privacy = findViewById(R.id.privacy);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        openapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(OpenappActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent=new Intent(OpenappActivity.this, PhotoGalleryActivity.class);
                    new PrefetchAlbumsData().execute(new Boolean[]{Boolean.valueOf(SP.getBoolean(getString(R.string.preference_auto_update_media), false))});
                    startActivity(intent);
                }
                else {
                    requestPermissionStoragePermission();
                }

            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpenappActivity.this, PrivacypolicyActivity.class));

            }
        });

    }

    private class PrefetchAlbumsData extends AsyncTask<Boolean, Boolean, Boolean> {
        private PrefetchAlbumsData() {
        }

        protected Boolean doInBackground(Boolean... arg0) {
            getAlbums().restoreBackup(getApplicationContext());
            if (getAlbums().dispAlbums.size() != 0) {
                return Boolean.valueOf(false);
            }
//            getAlbums().loadAlbums(getApplicationContext(), false);
            return Boolean.valueOf(true);
        }

        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            new Thread() {
                public void run() {
                        Intent i = new Intent(OpenappActivity.this, PhotoGalleryActivity.class);
//                        b = new Bundle();
//                        b.putInt("content", result.booleanValue() ? 23 : 60);
//                        b.putBoolean(PICK_MODE, PICK_INTENT);
//                        i.putExtras(b);
                        if (OpenappActivity.this.PICK_INTENT) {
                            startActivityForResult(i, 44);
                        } else {
                            startActivity(i);
                            finish();
                        }
                        if (result.booleanValue()) {
                            getAlbums().saveBackup(OpenappActivity.this.getApplicationContext());
                        }
                }
            }.start();


        }
    }
    private void requestPermissionStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(OpenappActivity.this,new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                new PrefetchAlbumsData().execute(new Boolean[]{Boolean.valueOf(this.SP.getBoolean(getString(R.string.preference_auto_update_media), false))});
                startActivity(new Intent(OpenappActivity.this, PhotoGalleryActivity.class));

            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OpenappActivity.this,StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }


    public void admobNativeAd() {
        try {
            final RelativeLayout rlAdvertisement = (RelativeLayout) findViewById(R.id.rlAdvertisement);

            com.google.android.gms.ads.AdLoader.Builder builder = new com.google.android.gms.ads.AdLoader.Builder(activity,  activity.getResources().getString(R.string.Nativeads));

            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                // OnUnifiedNativeAdLoadedListener implementation.
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater()
                            .inflate(R.layout.layout_admob_native_advanced, null);
                    unifiedNativeAdView(unifiedNativeAd, adView);
                    rlAdvertisement.removeAllViews();
                    rlAdvertisement.addView(adView);


                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            com.google.android.gms.ads.AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        try {

            com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
            adView.setNativeAd(nativeAd);


            VideoController vc = nativeAd.getVideoController();
            if (vc.hasVideoContent()) {

                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}