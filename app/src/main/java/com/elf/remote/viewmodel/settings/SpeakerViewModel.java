package com.elf.remote.viewmodel.settings;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class SpeakerViewModel extends BaseObservable {
    private final CallActivity navi;

    public SpeakerViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSycClick() {
        navi.callActivity();
    }

    public void onSpeakerClick() {
        navi.callDialog();
    }

    public void onTimeSetClick() {
        navi.exitActivity();
    }
}
