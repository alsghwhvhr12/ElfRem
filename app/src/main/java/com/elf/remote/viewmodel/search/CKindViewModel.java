package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.SubCall;

public class CKindViewModel extends BaseObservable {
    private final SubCall navi;

    public CKindViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onCloseClick() {
        navi.closeCall();
    }

    public void onKorClick() {
        navi.oneCall();
    }

    public void onPoPClick() {
        navi.twoCall();
    }

    public void onChinaClick() {
        navi.threeCall();
    }

    public void onJapClick() {
        navi.fourCall();
    }

    public void onChanClick() {
        navi.fiveCall();
    }

    public void onGatolClick() {
        navi.sixCall();
    }
}