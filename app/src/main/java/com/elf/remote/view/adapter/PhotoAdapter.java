package com.elf.remote.view.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.elf.remote.view.VideoSlide;

public class PhotoAdapter extends FragmentStateAdapter {
    private final int kind;
    public PhotoAdapter(FragmentActivity fm) {
        super(fm);
        kind = fm.getIntent().getExtras().getInt("kind");
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new VideoSlide();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        bundle.putInt("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return kind;
    }
}
