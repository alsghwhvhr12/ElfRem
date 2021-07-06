package com.elf.remote.viewmodel.view_recorded;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class MovAnotherViewModel extends BaseObservable {
    private final CallActivity navi;

    public MovAnotherViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onCancledClick() {
        navi.exitActivity();
    }

    public void onSaveClick() {
        navi.callActivity();
    }
}
