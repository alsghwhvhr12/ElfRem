package com.elf.remote.viewmodel.banjugi;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.BaseViewModel;

public class BanListEditViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    public BanListEditViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onExitClick() {
        navi.exitActivity();
    }

    public void onDelClick() {
        navi.callActivity();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
