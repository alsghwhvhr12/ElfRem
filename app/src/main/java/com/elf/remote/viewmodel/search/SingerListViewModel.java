package com.elf.remote.viewmodel.search;

import com.elf.remote.CallActivity;

public class SingerListViewModel {
    private final CallActivity navi;

    public SingerListViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.exitActivity();
    }
}
