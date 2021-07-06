package com.elf.remote.viewmodel.my_video;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class MyMovKindViewModel extends BaseObservable {
    private final CallActivity navi;

    public MyMovKindViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onCancledClick() {
        navi.exitActivity();
    }

    public void onSaveClick() {
        navi.callActivity();
    }
}
