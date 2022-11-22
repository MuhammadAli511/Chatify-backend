package com.ass3.i190417_i192048;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ass3.i190417_i192048.Adapters.FragmentsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatMainScreen extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main_screen);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_chat_bubble_outline_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_call_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_add_box_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_person_outline_24);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_baseline_settings_24);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus("Online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("Online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setStatus("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        setStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("Online");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dateText = formatter.format(cal.getTime());
        String finalText = "Last Seen: " + dateText;
        setStatus(finalText);
    }

    public void setStatus(String statusValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String emailStr = preferences.getString("email", "");
        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", emailStr)
                .add("status", statusValue)
                .build();

        Request request = new Request.Builder().url("http://10.0.2.2:5000/updateStatus").post(body).build();
        okhttpclient.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {}

        });
    }
}