package com.elf.remote.viewmodel.banjugi;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class BanConnectViewModel extends BaseObservable {
    private final CallActivity navi;

    public BanConnectViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onEditClicked() {
        navi.callActivity();
    }

    public void onFinishClicked() {
        navi.exitActivity();
    }

    public void findClicked() {
        navi.callDialog();
    }
}
