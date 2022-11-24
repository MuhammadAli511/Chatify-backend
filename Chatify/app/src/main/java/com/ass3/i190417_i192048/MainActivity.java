package com.ass3.i190417_i192048;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map config = new HashMap();
        config.put("cloud_name", "dit1cdpih");
        config.put("api_key", "874163382728423");
        config.put("api_secret", "VCKgXC1iVhfMTn4crtuzENqXV9E");
        config.put("secure", true);
        MediaManager.init(this, config);




        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}