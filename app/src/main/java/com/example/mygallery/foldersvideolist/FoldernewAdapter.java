package com.example.mygallery.foldersvideolist;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygallery.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;


public class FoldernewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity context;
    ArrayList<ArrayList<MediaFile>> mediaFiles = new ArrayList();
    String type;
    public static ArrayList<MediaFolder> mediaFolders = new ArrayList();

    ItemClickListener itemClickListener;
    ItemClickListener onItemClickdetail;
    ItemClickListener onItemClickrename;
    ItemClickListener onItemClickreitem;

    int AD_TYPE = 0;

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;


    private int ITEM_DATA = 0;
    private int ITEM_AD = 1;


    public interface ItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemClickdetail(View view, int position);

        public void onItemClickrename(View view, int position);

        void onFolderItemclick(View view, int position);
    }

    public FoldernewAdapter(FolderListActivity folderListActivity, ArrayList<MediaFolder> mediaFolders, String folderType, ArrayList<ArrayList<MediaFile>> mediaFiles,  ItemClickListener itemClickListener) {
        this.type = folderType;
        this.context = folderListActivity;
        this.mediaFiles = mediaFiles;
        this.mediaFolders = mediaFolders;
        this.itemClickListener = itemClickListener;
        this.onItemClickdetail = itemClickListener;
        this.onItemClickrename = itemClickListener;
        this.onItemClickreitem = itemClickListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tempfolderlist, parent, false);
            return new ViewHolder(view);
        } else if (viewType == ITEM_AD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ad_unified, parent, false);
            return new AdsViewHolder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holders, int position) {
        int viewType = getItemViewType(position);

        if (viewType == ITEM_DATA) {

            final ViewHolder holder = (ViewHolder) holders;

            String videoString = " " + this.type;
            if (mediaFolders.get(position).getNumberOfMediaFiles() > 0) {

                videoString = videoString + "s";

                holder.folderName.setText(mediaFolders.get(position).getDisplayName());

            }

            holder.videos.setText(mediaFolders.get(position).getNumberOfMediaFiles() + videoString);
            holder.menu.setTag(mediaFolders.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickreitem.onFolderItemclick(view, position);

                }
            });
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                    popupMenu.getMenuInflater().inflate(R.menu.video_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int i = menuItem.getItemId();
                            if (i == R.id.delete) {
                                int pos = position;
                                itemClickListener.onItemClick(view, position);
                                return true;
                            } else if (i == R.id.details) {
                                onItemClickdetail.onItemClickdetail(view, position);
                                return true;
                            } else if (i == R.id.rename) {
                                onItemClickrename.onItemClickrename(view, position);
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            });

        }else if (viewType == ITEM_AD) {

            final AdsViewHolder holder = (AdsViewHolder) holders;
            admobNativeAd(holder.rlAdvertisement);
        }

    }

    public void admobNativeAd(RelativeLayout rlAdvertisement) {

        try {
            com.google.android.gms.ads.AdLoader.Builder builder = new com.google.android.gms.ads.AdLoader.Builder(context, context.getResources().getString(R.string.Nativeads));

            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                // OnUnifiedNativeAdLoadedListener implementation.
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    UnifiedNativeAdView adView = (UnifiedNativeAdView) context.getLayoutInflater()
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
            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline is guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
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

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getVideoController();
            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        super.onVideoEnd();
                    }
                });
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mediaFolders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        TextView videos;
        ImageView menu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = (TextView) itemView.findViewById(R.id.folder_name);
            videos = (TextView) itemView.findViewById(R.id.no_of_videos);
            menu = (ImageView) itemView.findViewById(R.id.menu);


        }
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlAdvertisement;
        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            rlAdvertisement =  itemView.findViewById(R.id.rlAdvertisement);

        }
    }




    @Override
    public int getItemViewType(int position) {

        if (mediaFolders.get(position) == null) {
            return ITEM_AD;
        } else {
            return ITEM_DATA;
        }
    }

}
