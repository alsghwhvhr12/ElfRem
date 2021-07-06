package com.elf.remote.viewmodel.search;

import com.elf.remote.SubCall;

public class SungokStartViewModel {
    private final SubCall navi;

    public SungokStartViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onStartClick() {
        navi.oneCall();
    }

    public void onCloseClick() {
        navi.closeCall();
    }
}
