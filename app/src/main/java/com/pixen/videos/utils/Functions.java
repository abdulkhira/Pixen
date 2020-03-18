package com.pixen.videos.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixen.videos.MainActivity;
import com.pixen.videos.R;
import com.pixen.videos.model.Home_Get_Set;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Functions {

    private static DatabaseReference finalRootReference = FirebaseDatabase.getInstance().getReference();

    public static void uploadVideoLikes(Home_Get_Set home_get_set, String action) {

        String video_id = "liked/" + home_get_set.videoId + "/" + getMacAddr();

        Map<String, Object> liked = new HashMap<>();
        liked.put("video_id", home_get_set.videoId);
        liked.put("action", action);
        liked.put("user_id", getMacAddr());

        HashMap<String, Object> likeBodyDetails = new HashMap<>();
        likeBodyDetails.put(video_id, liked);

        finalRootReference.updateChildren(likeBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("tag", databaseError.getMessage());
                } else {
                    Log.e("tag", "done");
                }
            }
        });

        String videoKey = "videos/" + home_get_set.videoId;

        Map<String, Object> user = new HashMap<>();
        user.put("likes", home_get_set.like_count);
        user.put("video_url", home_get_set.video_url);
        user.put("views", home_get_set.views);
        user.put("time", home_get_set.time);
        user.put("video_id", home_get_set.videoId);

        HashMap<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(videoKey, user);

        finalRootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("tag", databaseError.getMessage());
                } else {
                    Log.e("tag", "done");
                }
            }
        });

    }

    public static void uploadVideoViews(Home_Get_Set home_get_set, String action) {
        String video_id = "viewed/" + home_get_set.videoId + "/" + getMacAddr();

        Map<String, Object> liked = new HashMap<>();
        liked.put("video_id", home_get_set.videoId);
        liked.put("action", action);
        liked.put("user_id", getMacAddr());

        HashMap<String, Object> likeBodyDetails = new HashMap<>();
        likeBodyDetails.put(video_id, liked);

        finalRootReference.updateChildren(likeBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("tag", databaseError.getMessage());
                } else {
                    Log.e("tag", "done");
                }
            }
        });

        String videoKey = "videos/" + home_get_set.videoId;

        Map<String, Object> user = new HashMap<>();
        user.put("likes", home_get_set.like_count);
        user.put("video_url", home_get_set.video_url);
        user.put("views", home_get_set.views);
        user.put("time", home_get_set.time);
        user.put("video_id", home_get_set.videoId);

        HashMap<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(videoKey, user);

        finalRootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("tag", databaseError.getMessage());
                } else {
                    Log.e("tag", "done");
                }
            }
        });
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    // res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;

    public static void Show_determinent_loader(Context context, boolean outside_touch, boolean cancleable) {

        determinant_dialog = new Dialog(context);
        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
        Objects.requireNonNull(determinant_dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        determinant_progress=determinant_dialog.findViewById(R.id.pbar);

        if(!outside_touch)
            determinant_dialog.setCanceledOnTouchOutside(false);

        if(!cancleable)
            determinant_dialog.setCancelable(false);

        determinant_dialog.show();

    }

    public static void Show_loading_progress(int progress){
        if(determinant_progress!=null ){
            determinant_progress.setProgress(progress);

        }
    }

    public static void cancel_determinent_loader(){
        if(determinant_dialog!=null){
            determinant_progress=null;
            determinant_dialog.cancel();
        }
    }

}
