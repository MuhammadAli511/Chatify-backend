package com.ass3.i190417_i192048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.ass3.i190417_i192048.Adapters.FragmentsAdapter;
import com.google.android.material.tabs.TabLayout;

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
}