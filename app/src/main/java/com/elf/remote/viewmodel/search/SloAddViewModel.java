package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class SloAddViewModel extends BaseObservable {

    private final CallActivity navi;

    public SloAddViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.exitActivity();
    }

    public void onSavedClick() {
        navi.callActivity();
    }
}