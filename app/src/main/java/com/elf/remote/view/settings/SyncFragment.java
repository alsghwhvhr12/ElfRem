package com.elf.remote.view.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSyncBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.settings.SyncViewModel;

public class SyncFragment extends DialogFragment implements CallActivity {
    FragmentSyncBinding binding;

    SyncViewModel model;
    public SyncFragment() {
    }

    public static SyncFragment getInstance() {
        return new SyncFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new SyncViewModel(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sync, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        model.onCreate();

        final String[] fontK = {"나눔", "을지로(10년)", "을지로", "할아범", "무궁화", "즐거움", "위로", "소미"};
        final String[] fontC = {"검정", "하양", "빨강", "주황", "노랑", "초록", "파랑", "남색", "보라", "분홍", "하늘"};
        final String[] video = {"1080p", "720p", "480p", "360p"};

        final Spinner font = binding.spinner;
        final Spinner fontColor = binding.spinner2;
        final Spinner videoC = binding.spinner3;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, fontK);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, fontC);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, video);

        font.setAdapter(adapter);
        fontColor.setAdapter(adapter2);
        videoC.setAdapter(adapter3);

        switch (model.font) {
            case "nanum.ttf" :
                font.setSelection(0);
                break;
            case "eul10.ttf" :
                font.setSelection(1);
                break;
            case "eul.ttf" :
                font.setSelection(2);
                break;
            case "grand.ttf" :
                font.setSelection(3);
                break;
            case "mugung.ttf" :
                font.setSelection(4);
                break;
            case "happy.ttf" :
                font.setSelection(5);
                break;
            case "up.ttf" :
                font.setSelection(6);
                break;
            case "somi.ttf" :
                font.setSelection(7);
                break;
        }

        switch (model.color) {
            case "black" :
                fontColor.setSelection(0);
                break;
            case "white" :
                fontColor.setSelection(1);
                break;
            case "red" :
                fontColor.setSelection(2);
                break;
            case "orange" :
                fontColor.setSelection(3);
                break;
            case "yellow" :
                fontColor.setSelection(4);
                break;
            case "green" :
                fontColor.setSelection(5);
                break;
            case "blue" :
                fontColor.setSelection(6);
                break;
            case "navy" :
                fontColor.setSelection(7);
                break;
            case "purple" :
                fontColor.setSelection(8);
                break;
            case "pink" :
                fontColor.setSelection(9);
                break;
            case "skyblue" :
                fontColor.setSelection(10);
                break;
        }

        switch (model.video) {
            case "1080" :
                videoC.setSelection(0);
                break;
            case "720" :
                videoC.setSelection(1);
                break;
            case "480" :
                videoC.setSelection(2);
                break;
            case "360" :
                videoC.setSelection(3);
                break;
        }

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void callDialog() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.onDestroy();
    }
}