package com.elf.remote.viewmodel.settings_manage;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class SaveNameViewModel extends BaseObservable {
    private final CallActivity navi;

    public SaveNameViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSubmitClick() {
        navi.exitActivity();
    }

    public void onChangeClick() {
        navi.callActivity();
    }
}
