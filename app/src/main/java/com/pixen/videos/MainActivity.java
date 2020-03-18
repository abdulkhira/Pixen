package com.pixen.videos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pixen.videos.fragment.LatestFragment;
import com.pixen.videos.fragment.PopularFragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;

    public DatabaseReference finalRootReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudienceNetworkAds.initialize(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), 0);

        adapter.AddFragment(new LatestFragment(), "Latest");
        adapter.AddFragment(new PopularFragment(), "Popular");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("tag","Permission is granted");
            } else {
                Log.e("tag","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("tag","Permission is granted");
        }
        amazon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.upload) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent,"Select Video"), 11);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                Uri selectedImageUri = null;
                if (data != null) {
                    selectedImageUri = data.getData();
                }

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {
                    uploadVideo(selectedImagePath);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void uploadVideo(final String path) {
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("videos").child(new File(path).getName())
                .putFile(Uri.fromFile(new File(path)));

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                return FirebaseStorage.getInstance().getReference().child("videos").child(new File(path).getName()).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();

                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        uploadVideoUrl(downloadUri.toString());
                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }

    public void uploadVideoUrl(String url) {

        String id = finalRootReference.child("videos").push().getKey();
        String video_id = "videos/" + id;

        Map<String, Object> user = new HashMap<>();
        user.put("video_url", url);
        user.put("views", "0");
        user.put("likes", "0");
        user.put("time", System.currentTimeMillis());
        user.put("video_id", id);

        HashMap<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(video_id, user);

        finalRootReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("tag", databaseError.getMessage());
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Log.e("tag", "done");
                    Toast.makeText(MainActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }


    public static class ViewPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments =new ArrayList<>();
        private final List<String>title =new ArrayList<>();

        ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle (int position){
            return title.get(position);
        }

        void  AddFragment(Fragment fragment, String titles){
            fragments.add(fragment);
            title.add(titles);

        }

    }

    // da2-js3w7p5h6vbfvks6sz547war4a

    private void amazon() {

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                try {
                    Amplify.addPlugin(new AWSS3StoragePlugin());
                    Amplify.configure(getApplicationContext());
                    Log.e("StorageQuickstart", "All set and ready to go!");
                } catch (Exception e) {
                    Log.e("StorageQuickstart", e.getMessage());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("StorageQuickstart", "Initialization error.", e);
            }
        });

    }
  public void uploadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        File file = new File(getApplicationContext().getFilesDir(), "sample.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append("Howdy World!");
            writer.close();
        }
        catch(Exception e) {
            Log.e("TAG", e.getMessage());
        }

        TransferObserver uploadObserver =
                transferUtility.upload("pixenvideo",
                        "sample.txt",
                        new File(getApplicationContext().getFilesDir(),"sample.txt"));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.e("TAG", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
            Log.e("tag", "done");
        }

        Log.e("TAG", "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.e("TAG", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

}
