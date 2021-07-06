package com.elf.remote.viewmodel.search;

import com.elf.remote.CallActivity;

public class FavoriteListViewModel {
    private final CallActivity navi;

    public FavoriteListViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.exitActivity();
    }
}
