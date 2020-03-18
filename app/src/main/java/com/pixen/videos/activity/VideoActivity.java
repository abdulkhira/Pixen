package com.pixen.videos.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.pixen.videos.utils.Functions;
import com.pixen.videos.R;
import com.pixen.videos.adapter.Watch_Videos_Adapter;
import com.pixen.videos.model.Home_Get_Set;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Objects;

public class VideoActivity extends AppCompatActivity implements Player.EventListener {

    Context context;

    RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    int currentPage=-1;
    LinearLayoutManager layoutManager;

    Watch_Videos_Adapter adapter;

    ProgressBar p_bar;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        context=this;

        p_bar=findViewById(R.id.p_bar);

        Intent bundle=getIntent();
        data_list = (ArrayList<Home_Get_Set>) bundle.getSerializableExtra("arraylist");
        int position=bundle.getIntExtra("position",0);


        recyclerView=findViewById(R.id.recylerview);
        layoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        SnapHelper snapHelper =  new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        adapter=new Watch_Videos_Adapter(context, data_list, new Watch_Videos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion,final Home_Get_Set item, View view) {

                switch(view.getId()){
                    case R.id.like_layout:
                        Like_Video(postion,item);
                        break;
                    case R.id.shared_layout:
                        downloadVideo(item, 1);
                        break;

                    case R.id.download_layout:
                        downloadVideo(item, 2);
                        break;
                }

            }
        });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);


        // this is the scroll listener of recycler view which will tell the current item number
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no=scrollOffset / height;

                if(page_no!=currentPage ){
                    currentPage=page_no;

                    Privious_Player();
                    Set_Player(currentPage);
                }

            }
        });

        recyclerView.scrollToPosition(position);



        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void downloadVideo(Home_Get_Set item, final int code) {

        Functions.Show_determinent_loader(context,false,false);
        PRDownloader.initialize(getApplicationContext());

        String directory = Environment.getExternalStorageDirectory() +"/Pixen/";
        String fileName = "pixen_video" + item.videoId  +".mp4";
        final String fullPath = directory + fileName;

        DownloadRequest prDownloader= PRDownloader.download(item.video_url, directory, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        int prog=(int)((progress.currentBytes*100)/progress.totalBytes);
                        Functions.Show_loading_progress(prog/2);

                    }
                });


        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Functions.cancel_determinent_loader();
                if (code == 1) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/mp4");
                    Uri uri = Uri.parse(fullPath);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(sharingIntent, "Share Video!"));
                } else {
                    Toast.makeText(context, "Video Downloaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Functions.cancel_determinent_loader();
            }


        });


    }

    @SuppressLint("ClickableViewAccessibility")
    public void Set_Player(final int currentPage){

        final Home_Get_Set item= data_list.get(currentPage);
        if (item.viewed.equals("0")) {
            item.views=""+(Integer.parseInt(item.views) +1);
            Functions.uploadVideoViews(item, "1");
            data_list.remove(currentPage);
            item.viewed="1";
            data_list.add(currentPage,item);
            adapter.notifyDataSetChanged();
        }
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "TikTok"));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(item.video_url));

        Log.d("resp",item.video_url);


        player.prepare(videoSource);

        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);

        View layout=layoutManager.findViewByPosition(currentPage);
        PlayerView playerView= Objects.requireNonNull(layout).findViewById(R.id.playerview);
        playerView.setPlayer(player);


        player.setPlayWhenReady(true);
        privious_player=player;


        playerView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    super.onFling(e1, e2, velocityX, velocityY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);
                    if(!player.getPlayWhenReady()){
                        privious_player.setPlayWhenReady(true);
                    }else{
                        privious_player.setPlayWhenReady(false);
                    }


                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if(!player.getPlayWhenReady()){
                        privious_player.setPlayWhenReady(true);
                    }
                    return super.onDoubleTap(e);

                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }


    SimpleExoPlayer privious_player;
    public void Privious_Player(){
        if(privious_player!=null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }


    public void Like_Video(final int position, final Home_Get_Set home_get_set){

        String action=home_get_set.liked;

        if(action.equals("1")){
            action="0";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) -1);
        }else {
            action="1";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) +1);
        }


        data_list.remove(position);
        home_get_set.liked=action;
        data_list.add(position,home_get_set);
        adapter.notifyDataSetChanged();

        Functions.uploadVideoLikes(home_get_set, action);

    }



    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onPause() {
        super.onPause();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
//        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(privious_player!=null){
            privious_player.release();
        }

//        keyboardHeightProvider.close();
    }



    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState==Player.STATE_BUFFERING){
            p_bar.setVisibility(View.VISIBLE);
        }
        else if(playbackState==Player.STATE_READY){
            p_bar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {


    }


}

