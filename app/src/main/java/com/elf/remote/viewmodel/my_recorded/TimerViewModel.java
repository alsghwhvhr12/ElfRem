package com.elf.remote.viewmodel.my_recorded;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;

public class TimerViewModel extends BaseObservable {
    private final CallActivity navi;

    public TimerViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSubmitClick() {
        navi.exitActivity();
    }
}
