package com.elf.remote.viewmodel.my_recorded;

import com.elf.remote.SubCall;

public class RecOkViewModel {
    private final SubCall navi;

    public RecOkViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.closeCall();
    }
}
