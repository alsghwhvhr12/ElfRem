package com.elf.remote.viewmodel.main;

import androidx.databinding.BaseObservable;

import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.BaseViewModel;

public class PermissionViewModel extends BaseObservable implements BaseViewModel {
    private final CallActivity navi;

    public PermissionViewModel(CallActivity navi) {
        this.navi = navi;
    }

    public void onSaveClick() {
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
