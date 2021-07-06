package com.elf.remote.viewmodel;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class DelOkViewModel extends BaseObservable {
    private final CallActivity navi;

    public DelOkViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSubmitClick() {
        navi.exitActivity();
    }

    public void onDelClick() {
        navi.callActivity();
    }
}
