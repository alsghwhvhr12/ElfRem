package com.elf.remote.viewmodel;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class ChgNameViewModel extends BaseObservable {
    private final CallActivity navi;

    public ChgNameViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSubmitClick() {
        navi.exitActivity();
    }

    public void onChangeClick() {
        navi.callActivity();
    }
}
