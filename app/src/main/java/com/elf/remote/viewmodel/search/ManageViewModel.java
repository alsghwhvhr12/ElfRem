package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class ManageViewModel extends BaseObservable {
    private final CallActivity navi;

    public ManageViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSaveClick() {
        navi.callDialog();
    }

    public void onLoadClick() {
        navi.callActivity();
    }

    public void onCloseClick() {
        navi.exitActivity();
    }
}
