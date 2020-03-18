package com.pixen.videos.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.pixen.videos.MainActivity;
import com.pixen.videos.R;
import com.pixen.videos.activity.VideoActivity;
import com.pixen.videos.model.Home_Get_Set;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.ads.AudienceNetworkAds.TAG;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Home_Get_Set> videoItems;
    private MainActivity mainActivity;

    public MainAdapter(ArrayList<Home_Get_Set> videoItems, MainActivity mainActivity) {
        this.videoItems = videoItems;
        this.mainActivity = mainActivity;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.ad_view_item, parent, false);
            return new AdViewHolder(listItem);
        }
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.image_item, parent, false);
        return new VideoViewHolder(listItem);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == 1) {

        } else {

            final VideoViewHolder viewHolder = (VideoViewHolder) holder;

            final Home_Get_Set videoItem = videoItems.get(viewHolder.getAdapterPosition());

            Glide.with(mainActivity)
                    .load(videoItem.video_url)
                    .placeholder(R.drawable.progress_animation)
                    .centerCrop()
                    .into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mainActivity, VideoActivity.class);
                    intent.putExtra("arraylist", videoItems);
                    intent.putExtra("position", viewHolder.getAdapterPosition());
                    mainActivity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (videoItems.get(position).video_url.equals("ad_view")) {
            return 1;
        }
        return 3;
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_);

        }

    }

    class AdViewHolder extends RecyclerView.ViewHolder {


        NativeBannerAd nativeBannerAd;

        @SuppressWarnings("deprecation")
        AdViewHolder(@NonNull View itemView) {
            super(itemView);

            nativeBannerAd = new NativeBannerAd(mainActivity, "441249523053937_800668057112080");
            nativeBannerAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            });
            // load the ad
            nativeBannerAd.loadAd();

        }

    }

}
