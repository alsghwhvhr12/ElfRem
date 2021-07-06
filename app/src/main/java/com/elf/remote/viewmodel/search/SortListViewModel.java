package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.SubCall;

public class SortListViewModel extends BaseObservable {
    private final SubCall navi;

    public SortListViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onCloseClick() {
        navi.closeCall();
    }

    public void onJaeAscClick() {
        navi.oneCall();
    }

    public void onRegClick() {
        navi.twoCall();
    }

    public void onSingAscClick() {
        navi.threeCall();
    }

    public void onJaeDscClick() {
        navi.fourCall();
    }

    public void onSingDscClick() {
        navi.fiveCall();
    }
}