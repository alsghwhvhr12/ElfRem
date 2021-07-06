package com.elf.remote.viewmodel.my_recorded;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class MyRecordedViewModel extends BaseObservable {

    private final CallActivity navi;

    public MyRecordedViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onRemoteClicked() {
        navi.callActivity();
    }

    public void onFindClicked() {
        navi.exitActivity();
    }

    public void onTimerClicked() {
        navi.callDialog();
    }
}
