package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.SubCall;

public class JKindViewModel extends BaseObservable {
    private final SubCall navi;

    public JKindViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onJaeClick() {
        navi.oneCall();
    }

    public void onNumClick() {
        navi.twoCall();
    }

    public void onSingerClick() {
        navi.threeCall();
    }

    public void onCloseClick() {
        navi.closeCall();
    }
}