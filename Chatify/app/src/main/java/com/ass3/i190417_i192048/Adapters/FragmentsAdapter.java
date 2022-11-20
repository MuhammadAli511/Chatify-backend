package com.ass3.i190417_i192048.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ass3.i190417_i192048.Fragments.CallsFragment;
import com.ass3.i190417_i192048.Fragments.ChatsFragment;
import com.ass3.i190417_i192048.Fragments.ContactsFragment;
import com.ass3.i190417_i192048.Fragments.NewContactFragment;
import com.ass3.i190417_i192048.Fragments.SettingsFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ChatsFragment();
        } else if (position == 1) {
            return new CallsFragment();
        } else if (position == 2) {
            return new NewContactFragment();
        } else if (position == 3) {
            return new ContactsFragment();
        } else if (position == 4) {
            return new SettingsFragment();
        }
        return new ChatsFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title = "Chats";
        } else if (position == 1) {
            title = "Calls";
        } else if (position == 2) {
            title = "New";
        } else if (position == 3) {
            title = "Contacts";
        } else if (position == 4) {
            title = "Setting";
        }
        return title;
    }
}
