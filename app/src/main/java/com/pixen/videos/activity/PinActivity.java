package com.pixen.videos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.pixen.videos.MainActivity;
import com.pixen.videos.R;

public class PinActivity extends AppCompatActivity {

    String pinStored = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        TextView textView = findViewById(R.id.profile_name);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        pinStored = sharedPreferences.getString("pin", "");

        if (pinStored.equals("")) {
            textView.setText("Set Pin");
        } else {
            textView.setText("Enter Pin");
        }

        PinLockView mPinLockView = findViewById(R.id.pin_lock_view);

        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (pinStored.equals("")) {
                SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
                editor.putString("pin",pin);
                editor.apply();
                startActivity(new Intent(PinActivity.this, MainActivity.class));
                Toast.makeText(PinActivity.this, "pin set", Toast.LENGTH_SHORT).show();
            } else if (pinStored.equals(pin)){
                startActivity(new Intent(PinActivity.this, MainActivity.class));
            } else {
                Toast.makeText(PinActivity.this, "wrong pin", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEmpty() {
            Log.e("TAG", "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.e("TAG", "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

}
