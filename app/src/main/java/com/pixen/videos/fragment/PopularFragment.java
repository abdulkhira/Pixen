package com.pixen.videos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pixen.videos.MainActivity;
import com.pixen.videos.R;
import com.pixen.videos.adapter.MainAdapter;
import com.pixen.videos.model.Home_Get_Set;
import com.pixen.videos.model.LikedVideoItem;
import com.pixen.videos.model.VideoItem;
import com.pixen.videos.utils.Functions;

import java.util.ArrayList;

public class PopularFragment extends Fragment {

    private MainActivity mainActivity;
    private ArrayList<Home_Get_Set> data_list = new ArrayList<>();

    private RecyclerView recyclerView;

    private MainAdapter mainAdapter;

    public PopularFragment() {
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_latest, container, false);

        recyclerView = v.findViewById(R.id.custom_recycler_view);



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainAdapter = new MainAdapter(data_list, mainActivity);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mainAdapter);

        getVideos();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private void getVideos() {
        mainActivity.progressDialog.setMessage("Getting Videos....");
        mainActivity.progressDialog.show();

        Query query = mainActivity.finalRootReference.child("videos")
                .orderByChild("views");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot1.getValue(VideoItem.class);

                    if (videoItem != null) {
                        Home_Get_Set item = new Home_Get_Set();
                        item.like_count =  videoItem.getLikes();
                        item.views = videoItem.getViews();
                        item.viewed = "0";
                        item.time = videoItem.getTime();
                        item.video_url = videoItem.getVideo_url();
                        item.videoId = videoItem.getVideo_id();
                        item.liked =  "0";

                        getViewed(videoItem, item);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("tag", "   = = = error = =  =  " + databaseError.getMessage());
                mainActivity.progressDialog.dismiss();
                Toast.makeText(mainActivity, "failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getViewed(final VideoItem videoItem, final Home_Get_Set item) {
        mainActivity.finalRootReference.child("viewed").child(videoItem.getVideo_id()).child(Functions.getMacAddr())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            LikedVideoItem likedVideoItem = dataSnapshot.getValue(LikedVideoItem.class);

                            if (likedVideoItem != null) {
                                if (Functions.getMacAddr().equals(likedVideoItem.getUser_id())) {
                                    item.viewed = likedVideoItem.getAction();
                                    getLikes(videoItem, item);
                                }
                            }

                        } else {
                            getLikes(videoItem, item);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getLikes(videoItem, item);
                    }
                });
    }

    private void getLikes(VideoItem videoItem, final Home_Get_Set item) {
        mainActivity.finalRootReference.child("liked").child(videoItem.getVideo_id()).child(Functions.getMacAddr())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            LikedVideoItem likedVideoItem = dataSnapshot.getValue(LikedVideoItem.class);

                            if (likedVideoItem != null) {
                                if (Functions.getMacAddr().equals(likedVideoItem.getUser_id())) {
                                    item.liked = likedVideoItem.getAction();
                                    data_list.add(item);
                                    mainAdapter.notifyItemInserted(data_list.size() -1);
                                    mainActivity.progressDialog.dismiss();
                                    if (data_list.size() % 5 == 0) {
                                        insertAdView();
                                    }
                                }
                            }

                        } else {
                            data_list.add(item);
                            mainAdapter.notifyItemInserted(data_list.size() -1);
                            mainActivity.progressDialog.dismiss();
                            if (data_list.size() % 5 == 0) {
                                insertAdView();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        data_list.add(item);
                        mainAdapter.notifyItemInserted(data_list.size() -1);
                        mainActivity.progressDialog.dismiss();
                        if (data_list.size() % 5 == 0) {
                            insertAdView();
                        }
                    }
                });
    }

    private void insertAdView() {
        Home_Get_Set item = new Home_Get_Set();
        item.video_url = "ad_view";
        data_list.add(item);
        mainAdapter.notifyItemInserted(data_list.size() -1);
    }

}
